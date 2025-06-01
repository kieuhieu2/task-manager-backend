package com.vnua.task_manager.dto.request.notificationReq;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults (level = AccessLevel.PRIVATE)
public class WasReadNotificationReq {
    List<Long> notificationIds;
    String userCode;
}
