package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.response.groupRes.GroupCreateRes;

public interface GroupService {
    public GroupCreateRes createGroup(GroupCreateReq request);
}
