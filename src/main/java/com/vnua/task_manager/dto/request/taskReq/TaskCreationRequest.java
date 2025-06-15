package com.vnua.task_manager.dto.request.taskReq;

import com.vnua.task_manager.entity.enumsOfEntity.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskCreationRequest implements Serializable {
    @NotNull(message = "Group ID is required")
    Integer groupId;

    @NotNull(message = "User Code is required")
    String userCode;

    @NotBlank(message = "Title is required")
    String title;

    @NotNull(message = "Task type is required")
    TaskType taskType;

    String description;
    Integer percentDone;
    List<String> assigneesUserCode;
    MultipartFile fileOfTask;
}
