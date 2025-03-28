package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.dto.response.taskRes.TaskResponse;
import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import org.springframework.core.io.Resource;
import java.util.List;

public interface TaskService {
    public String createTask(TaskCreationRequest request);
    public List<TaskResponse> getTaskByGroupId(Integer groupId);
    public Resource getFileByTaskId(Integer taskId);
    public String updateStatusOfTask(Integer taskId, TaskState newState);
}
