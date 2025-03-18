package com.vnua.task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vnua.task_manager.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
