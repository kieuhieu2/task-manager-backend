package com.vnua.task_manager.service.implement;

import java.util.List;

import com.vnua.task_manager.service.PermissionService;
import org.springframework.stereotype.Service;

import com.vnua.task_manager.dto.request.authReq.PermissionRequest;
import com.vnua.task_manager.dto.response.authRes.PermissionResponse;
import com.vnua.task_manager.entity.Permission;
import com.vnua.task_manager.mapper.PermissionMapper;
import com.vnua.task_manager.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
