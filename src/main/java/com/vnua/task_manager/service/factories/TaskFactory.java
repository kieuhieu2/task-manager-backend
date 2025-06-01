package com.vnua.task_manager.service.factories;

import com.vnua.task_manager.dto.request.taskReq.PrivateTaskReq;
import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.entity.PrivateTaskOfGroup;
import com.vnua.task_manager.entity.Task;

import java.io.IOException;

public interface TaskFactory {
    Task createTask(TaskCreationRequest request) throws IOException;
}
