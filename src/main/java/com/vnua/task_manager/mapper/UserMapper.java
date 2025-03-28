package com.vnua.task_manager.mapper;

import com.vnua.task_manager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.vnua.task_manager.dto.request.userReq.UserCreationRequest;
import com.vnua.task_manager.dto.request.userReq.UserUpdateRequest;
import com.vnua.task_manager.dto.response.userRes.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "code", source = "request.code")
    @Mapping(target = "createdAt", expression = "java(new java.util.Date())")
    @Mapping(target = "wasDeleted", constant = "false")
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
