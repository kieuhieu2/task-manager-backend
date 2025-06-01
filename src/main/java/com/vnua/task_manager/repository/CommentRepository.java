package com.vnua.task_manager.repository;

import com.vnua.task_manager.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c WHERE c.task.taskId = :taskId")
    List<Comment> findByTaskId(Integer taskId);
}
