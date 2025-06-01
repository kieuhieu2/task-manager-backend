package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;

public interface PrivateTaskService {
    Boolean createPrivateTask(TaskCreationRequest request);
}
