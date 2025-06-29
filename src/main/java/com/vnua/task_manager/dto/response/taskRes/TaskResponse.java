package com.vnua.task_manager.dto.response.taskRes;

import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import com.vnua.task_manager.entity.enumsOfEntity.TaskType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskResponse {
    Integer taskId;
    String title;
    String description;
    Integer percentDone;
    String userId;
    Integer groupId;
    TaskState state;
    TaskType taskType;
    Date deadline;
    Boolean isCreator;
    Integer positionInColumn;
}