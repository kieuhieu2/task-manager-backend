package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.notificationReq.WasReadNotificationReq;
import com.vnua.task_manager.dto.response.notificationRes.NotificationRes;
import com.vnua.task_manager.entity.Notification;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.entity.UserNotification;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.exception.ErrorCode;
import com.vnua.task_manager.mapper.NotificationMapper;
import com.vnua.task_manager.repository.UserNotificationRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    UserRepository userRepository;
    NotificationMapper notificationMapper;
    UserNotificationRepository userNotificationRepository;

    @Override
    public List<NotificationRes> getMyNotifications(String userCode) {
        User user = userRepository.findByCode(userCode)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserNotification> userNotifications = userNotificationRepository.findByUser(user);
        
        // Create a map to store wasRead status per notification ID
        Map<Long, Boolean> wasReadMap = new HashMap<>();
        for (UserNotification un : userNotifications) {
            wasReadMap.put(un.getNotification().getNotificationId(), un.getWasRead());
        }

        List<Notification> unreadNotifications = userNotifications.stream()
                .filter(un -> !Boolean.TRUE.equals(un.getWasRead()))
                .map(UserNotification::getNotification)
                .toList();
        
        log.info("Unread notifications count: {}", unreadNotifications.size());

        // If there are no unread notifications, return the 10 most recent notifications
        if (unreadNotifications.isEmpty()) {
            List<Notification> recentNotifications = userNotifications.stream()
                    .map(UserNotification::getNotification)
                    .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
            
            // Get the notification responses from the mapper
            List<NotificationRes> result = notificationMapper.toNotificationResList(recentNotifications);
            
            // Update wasRead values from our map
            for (NotificationRes res : result) {
                Boolean wasRead = wasReadMap.get(res.getNotificationId());
                res.setWasRead(wasRead != null ? wasRead : false);
            }
            
            return result;
        }

        // Get the notification responses from the mapper for unread notifications
        List<NotificationRes> result = notificationMapper.toNotificationResList(unreadNotifications);
        
        // All these should be unread, but let's make sure
        for (NotificationRes res : result) {
            res.setWasRead(false);
        }
        
        return result;
    }

    @Override
    public Boolean setNotificationWasRead(WasReadNotificationReq request) {
        User user = userRepository.findByCode(request.getUserCode())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Long> notificationIds = request.getNotificationIds();

        List<UserNotification> userNotifications = userNotificationRepository
                .findByUser_UserIdAndNotification_NotificationIdIn(user.getUserId(), notificationIds);

        for (UserNotification userNotification : userNotifications) {
            userNotification.setWasRead(true);
        }

        userNotificationRepository.saveAll(userNotifications);

        return true;
    }
    
    @Override
    public Boolean markNotificationAsRead(Long notificationId) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        
        String userCode = authentication.getName();
        User user = userRepository.findByCode(userCode)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        // Find notifications for this user and the given notification ID
        List<UserNotification> userNotifications = userNotificationRepository
                .findByUser_UserIdAndNotification_NotificationIdIn(
                        user.getUserId(), 
                        Collections.singletonList(notificationId)
                );
                
        if (!userNotifications.isEmpty()) {
            for (UserNotification userNotification : userNotifications) {
                userNotification.setWasRead(true);
            }
            userNotificationRepository.saveAll(userNotifications);
            return true;
        }
        
        return false;
    }
}
