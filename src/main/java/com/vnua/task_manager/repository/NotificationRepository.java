package com.vnua.task_manager.repository;

import com.vnua.task_manager.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
