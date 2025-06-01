package com.vnua.task_manager.dto.response.notificationRes;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRes {
    Long notificationId;
    String message;
    LocalDateTime createdAt;
}
