package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.dto.response.taskRes.TaskResponse;
import com.vnua.task_manager.entity.Task;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import com.vnua.task_manager.mapper.TaskMapper;
import com.vnua.task_manager.repository.TaskRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.TaskService;
import com.vnua.task_manager.service.factories.TaskFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TaskServiceImpl implements TaskService {
    TaskFactory taskFactory;
    TaskRepository taskRepository;
    TaskMapper taskMapper;
    UserRepository userRepository;

    @Value("${file.upload-dir:FileOfGroup/}")
    String UPLOAD_DIR = "FileOfGroup/";

    @Override
    public String createTask(TaskCreationRequest request) {
        try {
            taskRepository.save(taskFactory.createTask(request));
        } catch (IOException e) {
            throw new RuntimeException("Error while saving task: " + e.getMessage());
        }

        return "thanh cong";
    }

    @Override
    public List<TaskResponse> getTaskByGroupId(Integer groupId) {
        List<Task> tasks = taskRepository.findByGroup_GroupId(groupId);
        return taskMapper.toListTaskResponse(tasks);
    }

    @Override
    public Resource getFileByTaskId(Integer taskId) {
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        if (task.getFilePathOfTask() == null) {
            return null;
        }

        File file = new File(task.getFilePathOfTask());
//        if (!file.getAbsolutePath().startsWith(UPLOAD_DIR)) {
//            throw new SecurityException("Invalid file path: " + task.getFilePathOfTask());
//        }

        if (!file.exists() || !file.isFile()) {
            throw new IllegalStateException("File not found or invalid: " + task.getFilePathOfTask());
        }

        return new FileSystemResource(file);
    }

    @Override
    public String updateStatusOfTask(Integer taskId, TaskState newState) {
        var context = SecurityContextHolder.getContext();
        String userCodeOfContext = context.getAuthentication().getName();
        User user = userRepository.findByCode(userCodeOfContext)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userCodeOfContext));

        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        if (newState == TaskState.TODO) {
            task.setAssignee(null);
        }
        else if (task.getAssignee() == null) {
            task.setAssignee(user);
        }

        task.setState(newState);
        task.setUpdatedAt(new java.util.Date());
        taskRepository.save(task);
        return "Update task status successfully";
    }
}
