package com.vnua.task_manager.repository;

import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findByUser(User user);
    List<UserNotification> findByUser_UserIdAndNotification_NotificationIdIn(String userId, List<Long> notificationIds);

}
