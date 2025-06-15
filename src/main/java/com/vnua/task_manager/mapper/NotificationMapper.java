package com.vnua.task_manager.mapper;

import com.vnua.task_manager.dto.response.notificationRes.NotificationRes;
import com.vnua.task_manager.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "message", target = "message")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "notificationId", target = "notificationId")
    List<NotificationRes> toNotificationResList(List<Notification> notifications);
}
