package com.vnua.task_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vnua.task_manager.dto.request.authReq.RoleRequest;
import com.vnua.task_manager.dto.response.authRes.RoleResponse;
import com.vnua.task_manager.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
