package com.vnua.task_manager.repository;

import com.vnua.task_manager.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {
    Optional<Group> findByGroupId(Integer id);
    Optional<Boolean> deleteByGroupId(Integer groupId);
}
