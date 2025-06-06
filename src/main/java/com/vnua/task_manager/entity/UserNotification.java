package com.vnua.task_manager.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class UserNotification implements Serializable {
    @EmbeddedId
    private UserNotificationId id = new UserNotificationId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("notificationId")
    @JoinColumn(name = "notification_id")
    private Notification notification;

    private Boolean wasRead = false;
}
