package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.taskReq.FileOfTaskRequest;
import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.dto.request.taskReq.TaskDateRangeRequest;
import com.vnua.task_manager.dto.request.taskReq.TaskUpdateRequest;
import com.vnua.task_manager.dto.request.taskReq.UpdateTaskProgressRequest;
import com.vnua.task_manager.dto.request.taskReq.UpdateTaskStateRequest;
import com.vnua.task_manager.dto.response.taskRes.MemberWorkProgressResponse;
import com.vnua.task_manager.dto.response.taskRes.TaskResponse;
import com.vnua.task_manager.dto.response.taskRes.TaskUpdateResponse;
import org.springframework.core.io.Resource;
import java.util.List;

public interface TaskService {
    String createTask(TaskCreationRequest request);
    List<TaskResponse> getTaskByGroupId(Integer groupId);
    Resource getFileByTaskId(Integer taskId);
    String updateStatusAndPosition(UpdateTaskStateRequest request);
    Boolean deleteTask(Integer taskId);
    Boolean deleteFileOfTask(Integer taskId);
    Boolean addFileToTask(Integer taskId, FileOfTaskRequest request);
    List<MemberWorkProgressResponse> getWorkProcessOfMembersInGroup(Integer taskId);
    String updateTaskProgress(Integer taskId, UpdateTaskProgressRequest request);
    List<TaskResponse> getTasksByDateRangeAndUser(TaskDateRangeRequest request);
    TaskUpdateResponse updateTask(TaskUpdateRequest request);
}
