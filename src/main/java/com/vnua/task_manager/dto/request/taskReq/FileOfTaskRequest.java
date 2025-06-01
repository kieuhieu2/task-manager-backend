package com.vnua.task_manager.dto.request.taskReq;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileOfTaskRequest implements Serializable {
    MultipartFile fileOfTask;
}
