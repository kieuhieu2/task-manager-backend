package com.vnua.task_manager.repository;

import com.vnua.task_manager.entity.UserTaskId;
import com.vnua.task_manager.entity.UserTaskStatus;
import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserTaskStatusRepository extends JpaRepository<UserTaskStatus, UserTaskId> {
    List<UserTaskStatus> findAllByUser_UserIdAndTask_Group_GroupId(String userId, Integer groupId);
    
    List<UserTaskStatus> findAllByUser_UserIdAndTask_Group_GroupIdAndStateNot(String userId, Integer groupId, TaskState state);
    
    List<UserTaskStatus> findByUser_UserIdAndTask_Group_GroupIdAndStateOrderByUpdatedAtDesc(String userId, Integer groupId, TaskState state, Pageable pageable);
    
    List<UserTaskStatus> findAllByTask_TaskId(Integer taskId);
    
    // New methods for task positioning
    List<UserTaskStatus> findByUser_UserIdAndStateAndTask_Group_GroupIdAndPositionInColumnGreaterThanEqual(
        String userId, TaskState state, Integer groupId, Integer position);

    List<UserTaskStatus> findByUser_UserIdAndStateAndTask_Group_GroupIdAndPositionInColumnBetween(
        String userId, TaskState state, Integer groupId, Integer startPosition, Integer endPosition);

    List<UserTaskStatus> findByUser_UserIdAndStateAndTask_Group_GroupId(
        String userId, TaskState state, Integer groupId);
        
    @Query("SELECT uts FROM UserTaskStatus uts WHERE uts.user.userId = :userId AND uts.task.group.groupId = :groupId")
    List<UserTaskStatus> findTaskStatusesByUserAndGroup(@Param("userId") String userId, @Param("groupId") Integer groupId);
    
    // Query for non-DONE tasks
    @Query(value = "SELECT * FROM user_task_status uts " +
           "WHERE uts.user_id = :userId AND uts.task_id IN " +
           "(SELECT t.task_id FROM task t WHERE t.group_id = :groupId) " +
           "AND uts.state != 'DONE'",
           nativeQuery = true)
    List<UserTaskStatus> findNonDoneTasksForUserAndGroup(
            @Param("userId") String userId, 
            @Param("groupId") Integer groupId);
            
    // Query for recent DONE tasks
    @Query(value = "SELECT * FROM user_task_status uts " +
           "WHERE uts.user_id = :userId AND uts.task_id IN " +
           "(SELECT t.task_id FROM task t WHERE t.group_id = :groupId) " +
           "AND uts.state = 'DONE' " +
           "ORDER BY uts.updated_at DESC LIMIT :limit",
           nativeQuery = true)
    List<UserTaskStatus> findRecentDoneTasksForUserAndGroup(
            @Param("userId") String userId, 
            @Param("groupId") Integer groupId,
            @Param("limit") Integer limit);

    void deleteByUser_UserIdAndTask_TaskId(String userId, Integer taskId);

}
