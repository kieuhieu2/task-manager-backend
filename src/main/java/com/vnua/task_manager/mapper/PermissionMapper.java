package com.vnua.task_manager.mapper;

import org.mapstruct.Mapper;

import com.vnua.task_manager.dto.request.authReq.PermissionRequest;
import com.vnua.task_manager.dto.response.authRes.PermissionResponse;
import com.vnua.task_manager.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
