package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.authReq.RoleRequest;
import com.vnua.task_manager.dto.response.authRes.RoleResponse;

import java.util.List;

public interface RoleService {
    public RoleResponse create(RoleRequest request);
    public List<RoleResponse> getAll();
    public void delete(String role);
}
