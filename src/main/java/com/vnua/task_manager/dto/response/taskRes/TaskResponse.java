package com.vnua.task_manager.dto.response.taskRes;

import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    Boolean isCreator;
}