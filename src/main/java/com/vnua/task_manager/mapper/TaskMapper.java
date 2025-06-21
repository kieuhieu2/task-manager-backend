package com.vnua.task_manager.mapper;

import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.dto.response.taskRes.TaskResponse;

import com.vnua.task_manager.entity.Task;
import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target="title", source="title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "percentDone", source = "percentDone")
    @Mapping(target = "taskType", source = "taskType")
    @Mapping(target = "deadline", source = "deadline")
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "whoCreated", ignore = true)
    @Mapping(target = "commentsOfTask", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "filePathOfTask", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "wasDeleted", ignore = true)
    Task toTask(TaskCreationRequest request);

    @Mapping(target = "taskId", source = "task.taskId")
    @Mapping(target = "title", source = "task.title")
    @Mapping(target = "description", source = "task.description")
    @Mapping(target = "percentDone", source = "task.percentDone")
    @Mapping(target = "userId", source = "task.whoCreated.userId")
    @Mapping(target = "groupId", source = "task.group.groupId")
    @Mapping(target = "state", source = "userState")
    @Mapping(target = "taskType", source = "task.taskType")
    @Mapping(target = "deadline", source = "task.deadline")
    @Mapping(target = "isCreator", source = "isCreator")
    TaskResponse toTaskResponse(Task task, TaskState userState, Boolean isCreator);

    default List<TaskResponse> convertTasksToTaskResponses(List<Task> tasks) {
        return tasks.stream()
                .map(task -> toTaskResponse(task, TaskState.TODO, false))
                .collect(Collectors.toList());
    }
}
