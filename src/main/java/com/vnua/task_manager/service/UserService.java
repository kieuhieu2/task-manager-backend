package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.userReq.UserCreationRequest;
import com.vnua.task_manager.dto.request.userReq.UserUpdateRequest;
import com.vnua.task_manager.dto.response.userRes.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);
    UserResponse getMyInfo();
    UserResponse updateUser(String userId, UserUpdateRequest request);
    List<UserResponse> getUsers();
    void deleteUser(String userId);
    UserResponse getUserByUserCode(String userCode);
    String getFullNameByUserCode(String userCode);
}
