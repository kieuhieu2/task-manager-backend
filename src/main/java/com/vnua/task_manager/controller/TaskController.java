package com.vnua.task_manager.controller;

import com.vnua.task_manager.dto.ApiResponse;
import com.vnua.task_manager.dto.request.taskReq.FileOfTaskRequest;
import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.dto.request.taskReq.TaskDateRangeRequest;
import com.vnua.task_manager.dto.request.taskReq.TaskUpdateRequest;
import com.vnua.task_manager.dto.request.taskReq.UpdateTaskProgressRequest;
import com.vnua.task_manager.dto.request.taskReq.UpdateTaskStateRequest;
import com.vnua.task_manager.dto.response.taskRes.MemberWorkProgressResponse;
import com.vnua.task_manager.dto.response.taskRes.TaskResponse;
import com.vnua.task_manager.dto.response.taskRes.TaskUpdateResponse;
import com.vnua.task_manager.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {
    TaskService taskService;

    @PostMapping
    public ApiResponse<String> createTask(@Valid @ModelAttribute TaskCreationRequest request) {
        return ApiResponse.<String>builder()
                .result(taskService.createTask(request))
                .build();
    }

    @GetMapping("/{groupId}")
    public ApiResponse<List<TaskResponse>> getTaskByGroupId(@PathVariable Integer groupId) {
        return ApiResponse.<List<TaskResponse>>builder()
                .result(taskService.getTaskByGroupId(groupId))
                .build();
    }

    @GetMapping("/file/{taskId}")
    public ResponseEntity<Resource> getFileByTaskId(@PathVariable Integer taskId) {
        Resource fileResource = taskService.getFileByTaskId(taskId);
        if (fileResource == null) {
            return ResponseEntity.noContent().build();
        }
        String fileName = fileResource.getFilename();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileResource);
    }

    @PutMapping("update-task-state")
    public ApiResponse<String> updateStatusOfTask(@Valid @RequestBody UpdateTaskStateRequest request) {
        return ApiResponse.<String>builder()
                .result(taskService.updateStatusAndPosition(request))
                .build();
    }

    @DeleteMapping("/{taskId}")
    public ApiResponse<Boolean> deleteTask(@PathVariable Integer taskId) {
        return ApiResponse.<Boolean>builder()
                .result(taskService.deleteTask(taskId))
                .build();
    }

    @DeleteMapping("/file/{taskId}")
    public ApiResponse<Boolean> deleteFile(@PathVariable Integer taskId) {
        return ApiResponse.<Boolean>builder()
                .result(taskService.deleteFileOfTask(taskId))
                .build();
    }

    @PostMapping("/file/{taskId}")
    public ApiResponse<Boolean> addFileToTask(@PathVariable Integer taskId, @ModelAttribute FileOfTaskRequest request) {
        return ApiResponse.<Boolean>builder()
                .result(taskService.addFileToTask(taskId, request))
                .build();
    }

    @GetMapping("/work-progress/{taskId}")
    public ApiResponse<List<MemberWorkProgressResponse>> getWorkProcessOfMembersInGroup(@PathVariable Integer taskId) {
        return ApiResponse.<List<MemberWorkProgressResponse>>builder()
                .result(taskService.getWorkProcessOfMembersInGroup(taskId))
                .build();
    }

    @PutMapping("/percent-done/{taskId}")
    public ApiResponse<String> updateTaskProgress(
            @PathVariable Integer taskId,
            @Valid @RequestBody UpdateTaskProgressRequest request) {
        return ApiResponse.<String>builder()
                .result(taskService.updateTaskProgress(taskId, request))
                .build();
    }

    @PostMapping("/date-range")
    public ApiResponse<List<TaskResponse>> getTasksByDateRange(@Valid @RequestBody TaskDateRangeRequest request) {
        return ApiResponse.<List<TaskResponse>>builder()
                .result(taskService.getTasksByDateRangeAndUser(request))
                .build();
    }

    @PutMapping
    public ApiResponse<TaskUpdateResponse> updateTask(@Valid @RequestBody TaskUpdateRequest request) {
        return ApiResponse.<TaskUpdateResponse>builder()
                .result(taskService.updateTask(request))
                .build();
    }
}
