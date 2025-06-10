package com.vnua.task_manager.repository;

import com.vnua.task_manager.entity.UserTaskId;
import com.vnua.task_manager.entity.UserTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTaskStatusRepository extends JpaRepository<UserTaskStatus, UserTaskId> {
    List<UserTaskStatus> findAllByUser_UserIdAndTask_Group_GroupId(String userId, Integer groupId);
}
