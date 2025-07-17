package com.vnua.task_manager.service.eventListeners;

import com.vnua.task_manager.entity.Group;
import com.vnua.task_manager.entity.Notification;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.entity.UserNotification;
import com.vnua.task_manager.event.TaskCreatedEvent;
import com.vnua.task_manager.repository.GroupRepository;
import com.vnua.task_manager.repository.NotificationRepository;
import com.vnua.task_manager.repository.UserNotificationRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskListener {
    NotificationRepository notificationRepository;
    GroupRepository groupRepository;
    UserNotificationRepository userNotificationRepository;

    @Async
    @EventListener
    @Transactional
    public void taskCreatedEventListener(TaskCreatedEvent taskCreatedEvent) {
        Notification notification = new Notification();
        notification.setMessage(taskCreatedEvent.getMessage());
        notificationRepository.save(notification);

        Group group = groupRepository.findByGroupId(taskCreatedEvent.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        Set<User>  users = group.getMembers();
        for (User user : users) {
            UserNotification userNotification = new UserNotification();
            userNotification.setUser(user);
            userNotification.setNotification(notification);
            userNotification.setWasRead(false);

            userNotificationRepository.save(userNotification);
        }
    }

}
