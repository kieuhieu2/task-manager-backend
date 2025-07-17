package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.commentReq.CommentReq;
import com.vnua.task_manager.dto.response.commentRes.CommentRes;
import com.vnua.task_manager.entity.Comment;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.exception.ErrorCode;
import com.vnua.task_manager.repository.CommentRepository;
import com.vnua.task_manager.service.CommentService;
import com.vnua.task_manager.service.factories.factoryImpl.CommentFactoryImpl;
import com.vnua.task_manager.utils.FileUtils;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommentServiceImpl implements CommentService {
    CommentRepository commentRepository;
    CommentFactoryImpl commentFactory;

    @Override
    @Transactional
    public boolean createComment(CommentReq commentReq, MultipartFile commentFile) {
        try {
            Comment comment = commentFactory.createComment(commentReq);
            log.info("Saving comment: user_id={}, task_id={}, text={}",
                    comment.getUser() != null ? comment.getUser().getUserId() : "null",
                    comment.getTask() != null ? comment.getTask().getTaskId() : "null",
                    comment.getCommentText());
            
            // Handle file upload if a file is provided
            if (commentFile != null && !commentFile.isEmpty()) {
                try {
                    // Get group name from task (assuming each task belongs to a group)
                    String groupName = comment.getTask().getGroup().getNameOfGroup();
                    String taskName = comment.getTask().getTitle();

                    // Ensure group folder exists
                    String groupFolder = "FileOfGroup/" + groupName + "/" + taskName;
                    File folder = new File(groupFolder);
                    if (!folder.exists()) {
                        boolean folderCreated = folder.mkdirs();
                        if (!folderCreated) {
                            throw new AppException(ErrorCode.FOLDER_CREATION_FAILED);
                        }
                    }
                    
                    // Save comment file to group folder
                    String filePath = FileUtils.saveFileToPath(groupFolder, commentFile);
                    
                    // Set the file path in the comment
                    comment.setFilePath(filePath);
                    
                    log.info("Comment file uploaded successfully to: {}", filePath);
                } catch (IOException e) {
                    log.error("Failed to upload comment file: {}", e.getMessage(), e);
                    throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
                }
            }
            
            commentRepository.saveAndFlush(comment);
            return true;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.COMMENT_CREATION_FAILED);
        } catch (Exception e) {
            log.error("Failed to save comment: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.COMMENT_CREATION_FAILED);
        }
    }

    @Override
    public List<CommentRes> findCommentsByTaskId(Integer taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(comment -> CommentRes.builder()
                        .commentId(comment.getCommentId())
                        .commentText(comment.getCommentText())
                        .userName(comment.getUser() != null ? comment.getUser().getUsername() : null)
                        .userCode(comment.getUser().getCode())
                        .filePath(comment.getFilePath())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Boolean updateComment(Integer commentId, CommentReq commentReq) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow( () -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        comment.setCommentText(commentReq.getCommentText());
        try {
            commentRepository.save(comment);
            return true;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.COMMENT_CREATION_FAILED);
        } catch (Exception e) {
            log.error("Failed to update comment: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.COMMENT_CREATION_FAILED);
        }
    }

    @Override
    public Boolean deleteComment(Integer commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new AppException(ErrorCode.COMMENT_NOT_FOUND);
        }

        try {
            commentRepository.deleteById(commentId);
            return true;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.COMMENT_CREATION_FAILED);
        } catch (Exception e) {
            log.error("Failed to delete comment: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.COMMENT_CREATION_FAILED);
        }
    }

    @Override
    public ResponseEntity<Resource> getCommentFile(Integer taskId, Integer commentId) {
        try {
            // Get comments for the task
            List<CommentRes> comments = findCommentsByTaskId(taskId);
            
            // Find the specific comment
            CommentRes targetComment = comments.stream()
                    .filter(comment -> comment.getCommentId().equals(commentId))
                    .findFirst()
                    .orElse(null);
            
            if (targetComment == null || targetComment.getFilePath() == null || targetComment.getFilePath().isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Get the file path
            Path filePath = Paths.get(targetComment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            // Check if the file exists and is readable
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            // Determine the content type of the file
            String contentType;
            try {
                contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }
            } catch (IOException e) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            
            // Extract filename from path
            String filename = filePath.getFileName().toString();
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("Failed to get comment file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
