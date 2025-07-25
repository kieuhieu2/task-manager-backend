package com.vnua.task_manager.repository;

import com.vnua.task_manager.entity.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByGroup_GroupId(Integer groupId);
    Optional<Task> findByTaskId(Integer taskId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.group.groupId = :groupId")
    void deleteByGroupId(@Param("groupId") Integer groupId);
    
    @Query("SELECT t FROM Task t WHERE t.whoCreated.code = :userCode AND t.createdAt BETWEEN :fromDate AND :toDate")
    List<Task> findTasksByUserCodeAndDateRange(@Param("userCode") String userCode, 
                                              @Param("fromDate") Date fromDate, 
                                              @Param("toDate") Date toDate);
}
