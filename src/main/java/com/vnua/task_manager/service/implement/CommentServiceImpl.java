package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.commentReq.CommentReq;
import com.vnua.task_manager.dto.response.commentRes.CommentRes;
import com.vnua.task_manager.entity.Comment;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.exception.ErrorCode;
import com.vnua.task_manager.repository.CommentRepository;
import com.vnua.task_manager.service.CommentService;
import com.vnua.task_manager.service.factories.factoryImpl.CommentFactoryImpl;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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
    public boolean createComment(CommentReq commentReq) {
        try {
            Comment comment = commentFactory.createComment(commentReq);
            log.info("Saving comment: user_id={}, task_id={}, text={}",
                    comment.getUser() != null ? comment.getUser().getUserId() : "null",
                    comment.getTask() != null ? comment.getTask().getTaskId() : "null",
                    comment.getCommentText());
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
                        .taskId(comment.getTask().getTaskId())
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
}
