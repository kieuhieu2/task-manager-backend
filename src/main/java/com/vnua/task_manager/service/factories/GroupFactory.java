package com.vnua.task_manager.service.factories;

import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.request.groupReq.GroupUpdateReq;
import com.vnua.task_manager.entity.Group;

import java.io.IOException;

public interface GroupFactory {
    Group createGroup(GroupCreateReq request) throws IOException;
    Group updateGroup(GroupUpdateReq request) throws IOException;
}
