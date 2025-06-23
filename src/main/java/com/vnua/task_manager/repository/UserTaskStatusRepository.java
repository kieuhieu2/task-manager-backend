package com.vnua.task_manager.repository;

import com.vnua.task_manager.entity.UserTaskId;
import com.vnua.task_manager.entity.UserTaskStatus;
import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTaskStatusRepository extends JpaRepository<UserTaskStatus, UserTaskId> {
    List<UserTaskStatus> findAllByUser_UserIdAndTask_Group_GroupId(String userId, Integer groupId);
    
    List<UserTaskStatus> findAllByUser_UserIdAndTask_Group_GroupIdAndStateNot(String userId, Integer groupId, TaskState state);
    
    List<UserTaskStatus> findByUser_UserIdAndTask_Group_GroupIdAndStateOrderByUpdatedAtDesc(String userId, Integer groupId, TaskState state, Pageable pageable);
    
    List<UserTaskStatus> findAllByTask_TaskId(Integer taskId);
}
