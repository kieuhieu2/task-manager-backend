package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.notificationReq.WasReadNotificationReq;
import com.vnua.task_manager.dto.response.notificationRes.NotificationRes;
import java.util.List;

public interface NotificationService {
    List<NotificationRes> getMyNotifications(String userCode);
    Boolean setNotificationWasRead(WasReadNotificationReq request);
    Boolean markNotificationAsRead(Long notificationId);
}
