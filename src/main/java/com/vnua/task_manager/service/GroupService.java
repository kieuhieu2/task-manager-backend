package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.response.groupRes.GroupCreateRes;
import com.vnua.task_manager.dto.response.groupRes.GroupGetResponse;

import java.util.List;

public interface GroupService {
    public GroupCreateRes createGroup(GroupCreateReq request);
    public List<GroupGetResponse> getAllGroup();
}
