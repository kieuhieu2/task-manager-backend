package com.vnua.task_manager.service.factories.factoryImpl;

import com.vnua.task_manager.dto.request.commentReq.CommentReq;
import com.vnua.task_manager.entity.Comment;
import com.vnua.task_manager.entity.Task;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.exception.ErrorCode;
import com.vnua.task_manager.repository.TaskRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.factories.CommentFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class CommentFactoryImpl implements CommentFactory {
    UserRepository userRepository;
    TaskRepository taskRepository;

    @Override
    public Comment createComment(CommentReq commentReq) {
        Comment comment = new Comment();
        var context = SecurityContextHolder.getContext();
        String code = context.getAuthentication().getName();

        User user = userRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("User not found with code: {}", code);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });

        comment.setUser(user);

        Task task = taskRepository.findByTaskId(commentReq.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + commentReq.getTaskId()));

        comment.setTask(task);
        comment.setCommentText(commentReq.getCommentText());
        comment.setCreatedAt(new java.util.Date());
        return comment;
    }
}
