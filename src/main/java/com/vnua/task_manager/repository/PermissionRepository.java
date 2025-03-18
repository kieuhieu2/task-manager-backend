package com.vnua.task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vnua.task_manager.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
