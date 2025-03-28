package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.authReq.PermissionRequest;
import com.vnua.task_manager.dto.response.authRes.PermissionResponse;

import java.util.List;

public interface PermissionService {
    public PermissionResponse create(PermissionRequest request);
    public List<PermissionResponse> getAll();
    public void delete(String permission);
}
