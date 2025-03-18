package com.vnua.task_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.vnua.task_manager.dto.request.UserCreationRequest;
import com.vnua.task_manager.dto.request.UserUpdateRequest;
import com.vnua.task_manager.dto.response.UserResponse;
import com.vnua.task_manager.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
