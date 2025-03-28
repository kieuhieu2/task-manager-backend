package com.vnua.task_manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.vnua.task_manager.dto.ApiResponse;
import com.vnua.task_manager.dto.request.authReq.PermissionRequest;
import com.vnua.task_manager.dto.response.authRes.PermissionResponse;
import com.vnua.task_manager.service.implement.PermissionServiceImpl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
    PermissionServiceImpl permissionServiceImpl;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionServiceImpl.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionServiceImpl.getAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable String permission) {
        permissionServiceImpl.delete(permission);
        return ApiResponse.<Void>builder().build();
    }
}
