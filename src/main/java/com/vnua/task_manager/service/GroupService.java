package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.request.groupReq.GroupUpdateReq;
import com.vnua.task_manager.dto.response.groupRes.GroupCreateRes;
import com.vnua.task_manager.dto.response.groupRes.GroupGetRes;
import com.vnua.task_manager.dto.response.groupRes.GroupUpdateRes;

import java.util.List;

public interface GroupService {
    public GroupCreateRes createGroup(GroupCreateReq request);
    public List<GroupGetRes> getAllGroup();
    public GroupUpdateRes updateGroup(GroupUpdateReq request);
    public String deleteGroup(Integer groupId);
}
