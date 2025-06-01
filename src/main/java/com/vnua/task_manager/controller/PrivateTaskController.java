package com.vnua.task_manager.controller;

import com.vnua.task_manager.dto.ApiResponse;
import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.service.PrivateTaskService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/private-task")
public class PrivateTaskController {
    PrivateTaskService privateTaskService;

    @PostMapping()
    public ApiResponse<Boolean> createPrivateTask(@ModelAttribute TaskCreationRequest request) {
        return ApiResponse.<Boolean>builder()
                .result(privateTaskService.createPrivateTask(request))
                .build();
    }
}
