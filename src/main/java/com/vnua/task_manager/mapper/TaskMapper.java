package com.vnua.task_manager.mapper;

import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.dto.response.taskRes.TaskResponse;
import com.vnua.task_manager.entity.PrivateTaskOfGroup;
import com.vnua.task_manager.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target="title", source="title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "percentDone", source = "percentDone")
    Task toTask(TaskCreationRequest request);

    @Mapping(target = "taskId", source = "taskId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "percentDone", source = "percentDone")
    @Mapping(target = "userId", source = "whoCreated.userId")
    @Mapping(target = "groupId", source = "group.groupId")
    @Mapping(target = "state", source = "state")
    TaskResponse toTaskResponse(Task task);

    @Mapping(target = "taskId", source = "taskId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "percentDone", source = "percentDone")
    @Mapping(target = "userId", source = "whoCreated.userId")
    @Mapping(target = "groupId", source = "group.groupId")
    @Mapping(target = "state", source = "state")
    List<TaskResponse> toListTaskResponse(List<Task> tasks);

    PrivateTaskOfGroup toPrivateTaskOfGroup(Task task);
}
