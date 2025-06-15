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
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "assignedPrivateTasks", ignore = true)
    @Mapping(target = "userNotifications", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "groupLeaders", ignore = true)
    @Mapping(target = "createdTasks", ignore = true)
    @Mapping(target = "tasksAssigned", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "taskProgress", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "pathOfUserFolder", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "assignedPrivateTasks", ignore = true)
    @Mapping(target = "userNotifications", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "groupLeaders", ignore = true)
    @Mapping(target = "createdTasks", ignore = true)
    @Mapping(target = "tasksAssigned", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "taskProgress", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "wasDeleted", ignore = true)
    @Mapping(target = "pathOfUserFolder", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
