package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.userReq.UserCreationRequest;
import com.vnua.task_manager.dto.request.userReq.UserUpdateRequest;
import com.vnua.task_manager.dto.response.userRes.UserResponse;

import java.util.List;

public interface UserService {
    public UserResponse createUser(UserCreationRequest request);
    public UserResponse getMyInfo();
    public UserResponse updateUser(String userId, UserUpdateRequest request);
    public List<UserResponse> getUsers();
    public void deleteUser(String userId);
    public UserResponse getUser(String id);
}
