package com.vnua.task_manager.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.vnua.task_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    Optional<User> findByCode(String code);
    Optional<User> findByUsername(String username);
    List<User> findByCodeIn(Collection<String> codes);

    Optional<User> findByUserId(String userId);
}
