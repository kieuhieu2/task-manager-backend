package com.vnua.task_manager.dto.request.taskReq;

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
public class PrivateTaskReq implements Serializable {
    @NotNull(message = "Group ID is required")
    Integer groupId;

    @NotNull(message = "User Id is required")
    String userId;

    @NotBlank(message = "Title is required")
    String title;

    String description;
    Integer percentDone;
    MultipartFile fileOfTask;
    List<String> assigneesUserCode;
}
