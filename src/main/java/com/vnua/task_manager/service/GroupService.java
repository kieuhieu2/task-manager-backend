package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.request.groupReq.GroupUpdateReq;
import com.vnua.task_manager.dto.response.groupRes.GroupCreateRes;
import com.vnua.task_manager.dto.response.groupRes.GroupGetRes;
import com.vnua.task_manager.dto.response.groupRes.GroupUpdateRes;

import java.util.List;

public interface GroupService {
    GroupCreateRes createGroup(GroupCreateReq request);
    List<GroupGetRes> getAllGroup();
    GroupUpdateRes updateGroup(GroupUpdateReq request);
    String deleteGroup(Integer groupId);
    List<GroupGetRes> getMyGroups(String userCode);

    Boolean addUserToGroup(Integer groupId, String userCode);
}
