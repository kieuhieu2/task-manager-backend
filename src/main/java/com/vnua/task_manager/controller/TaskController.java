package com.vnua.task_manager.controller;

import com.vnua.task_manager.dto.ApiResponse;
import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.dto.response.taskRes.TaskResponse;
import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import com.vnua.task_manager.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.io.IOException;
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
        try {
            Resource fileResource = taskService.getFileByTaskId(taskId);

            if (fileResource == null) {
                return ResponseEntity.noContent().build(); // HTTP 204 nếu không có file
            }

            String fileName = fileResource.getFile().getName(); // Lấy tên file từ FileSystemResource
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(fileResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // HTTP 404 nếu task không tồn tại
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build(); // HTTP 404 nếu file không tồn tại
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // HTTP 403 nếu đường dẫn không hợp lệ
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("update-state/{taskId}/{newState}")
    public ApiResponse<String> updateStatusOfTask(@PathVariable Integer taskId, @PathVariable TaskState newState) {
        return ApiResponse.<String>builder()
                .result(taskService.updateStatusOfTask(taskId, newState))
                .build();
    }
}
