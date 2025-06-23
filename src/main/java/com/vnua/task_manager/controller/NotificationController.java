package com.vnua.task_manager.controller;

import com.vnua.task_manager.dto.ApiResponse;
import com.vnua.task_manager.dto.request.notificationReq.WasReadNotificationReq;
import com.vnua.task_manager.dto.response.notificationRes.NotificationRes;
import com.vnua.task_manager.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @GetMapping("/{userCode}")
    public ApiResponse<List<NotificationRes>> getMyNotifications(@PathVariable String userCode) {
        return ApiResponse.<List<NotificationRes>>builder()
                .result(notificationService.getMyNotifications(userCode))
                .build();
    }

    @PostMapping("/was-read")
    public ApiResponse<Boolean> setNotificationWasRead(@RequestBody WasReadNotificationReq request) {
        return ApiResponse.<Boolean>builder()
                .result(notificationService.setNotificationWasRead(request))
                .build();
    }
    
    @PutMapping("/{notificationId}/read")
    public ApiResponse<Boolean> markNotificationAsRead(@PathVariable Long notificationId) {
        return ApiResponse.<Boolean>builder()
                .result(notificationService.markNotificationAsRead(notificationId))
                .build();
    }
}
