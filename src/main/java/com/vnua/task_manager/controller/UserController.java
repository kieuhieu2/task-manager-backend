package com.vnua.task_manager.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import com.vnua.task_manager.dto.ApiResponse;
import com.vnua.task_manager.dto.PageOfUserResponse;
import com.vnua.task_manager.dto.request.userReq.UserCreationRequest;
import com.vnua.task_manager.dto.request.userReq.UserUpdateRequest;
import com.vnua.task_manager.dto.response.userRes.UserResponse;
import com.vnua.task_manager.service.implement.UserServiceImpl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserServiceImpl userServiceImpl;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userServiceImpl.createUser(request))
                .build();
    }

    @GetMapping
    ApiResponse<PageOfUserResponse<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> userPage = userServiceImpl.getUsersWithPagination(page, size);
        
        PageOfUserResponse<UserResponse> pageResponse = PageOfUserResponse.<UserResponse>builder()
                .content(userPage.getContent())
                .currentPage(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
                
        return ApiResponse.<PageOfUserResponse<UserResponse>>builder()
                .result(pageResponse)
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userServiceImpl.getUsers())
                .build();
    }

    @GetMapping("/{userCode}")
    ApiResponse<UserResponse> getUserByUserCode(@PathVariable("userCode") String userCode) {
        return ApiResponse.<UserResponse>builder()
                .result(userServiceImpl.getUserByUserCode(userCode))
                .build();
    }

    @GetMapping("/find-full-name/{userCode}")
    ApiResponse<String> getFullNameByUserCode(@PathVariable("userCode") String userCode) {
        return ApiResponse.<String>builder()
                .result(userServiceImpl.getFullNameByUserCode(userCode))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userServiceImpl.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<Boolean> deleteUser(@PathVariable String userId) {
        return ApiResponse.<Boolean>builder()
                .result(userServiceImpl.deleteUser(userId))
                .build();
    }

    @PutMapping("/{userCode}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userCode, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userServiceImpl.updateUser(userCode, request))
                .build();
    }
}
