package com.vnua.task_manager.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.vnua.task_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByCode(String code);
    Optional<User> findByUsername(String username);
    List<User> findByCodeIn(Collection<String> codes);

    @Query("SELECT CONCAT(COALESCE(u.firstName, ''), ' ', COALESCE(u.lastName, '')) FROM User u WHERE u.code = :code")
    String findFullNameByUserCode(@Param("code") String code);
}
