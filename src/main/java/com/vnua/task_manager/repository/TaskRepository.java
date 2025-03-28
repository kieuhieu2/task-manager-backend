package com.vnua.task_manager.repository;

import com.vnua.task_manager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByGroup_GroupId(Integer groupId);
    Optional<Task> findByTaskId(Integer taskId);
}
