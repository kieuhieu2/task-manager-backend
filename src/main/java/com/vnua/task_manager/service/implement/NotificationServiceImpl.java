package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.notificationReq.WasReadNotificationReq;
import com.vnua.task_manager.dto.response.notificationRes.NotificationRes;
import com.vnua.task_manager.entity.Notification;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.entity.UserNotification;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.exception.ErrorCode;
import com.vnua.task_manager.mapper.NotificationMapper;
import com.vnua.task_manager.repository.NotificationRepository;
import com.vnua.task_manager.repository.UserNotificationRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    UserRepository userRepository;
    NotificationMapper notificationMapper;
    NotificationRepository notificationRepository;
    UserNotificationRepository userNotificationRepository;

    @Override
    public List<NotificationRes> getMyNotifications(String userCode) {
        User user = userRepository.findByCode(userCode)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserNotification> userNotifications = userNotificationRepository.findByUser(user);

        List<Notification> unreadNotifications = userNotifications.stream()
                .filter(un -> !Boolean.TRUE.equals(un.getIsRead()))
                .map(UserNotification::getNotification)
                .toList();

        return notificationMapper.toNotificationResList(unreadNotifications);
    }

    @Override
    public Boolean setNotificationWasRead(WasReadNotificationReq request) {
        User user = userRepository.findByCode(request.getUserCode())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Long> notificationIds = request.getNotificationIds();

        List<UserNotification> userNotifications = userNotificationRepository
                .findByUser_UserIdAndNotification_NotificationIdIn(user.getUserId(), notificationIds);

        for (UserNotification userNotification : userNotifications) {
            userNotification.setIsRead(true);
        }

        userNotificationRepository.saveAll(userNotifications);

        return true;
    }
}
