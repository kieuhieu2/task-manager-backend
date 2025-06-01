package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.taskReq.FileOfTaskRequest;
import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.dto.response.taskRes.TaskResponse;
import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import org.springframework.core.io.Resource;
import java.util.List;

public interface TaskService {
    String createTask(TaskCreationRequest request);
    List<TaskResponse> getTaskByGroupId(Integer groupId);
    Resource getFileByTaskId(Integer taskId);
    String updateStatusOfTask(Integer taskId, TaskState newState);
    Boolean deleteTask(Integer taskId);
    Boolean deleteFileOfTask(Integer taskId);
    Boolean addFileToTask(Integer taskId, FileOfTaskRequest request);
}
