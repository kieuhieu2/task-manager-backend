package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.taskReq.FileOfTaskRequest;
import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.dto.response.taskRes.TaskResponse;
import com.vnua.task_manager.entity.Group;
import com.vnua.task_manager.entity.Task;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import com.vnua.task_manager.event.TaskCreatedEvent;
import com.vnua.task_manager.mapper.TaskMapper;
import com.vnua.task_manager.repository.TaskRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.TaskService;
import com.vnua.task_manager.service.factories.TaskFactory;
import com.vnua.task_manager.utils.FileUtils;
import com.vnua.task_manager.utils.StringCustomUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.List;
import com.vnua.task_manager.utils.FileUtils;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TaskServiceImpl implements TaskService {
    TaskFactory taskFactory;
    TaskRepository taskRepository;
    TaskMapper taskMapper;
    UserRepository userRepository;
    ApplicationEventPublisher applicationEventPublisher;

    @Value("${file.upload-dir:FileOfGroup/}")
    String UPLOAD_DIR = "FileOfGroup/";

    @Override
    public String createTask(TaskCreationRequest request) {
        try {
            Task task = taskFactory.createTask(request);
            taskRepository.save(task);

            String message = "User " + task.getWhoCreated().getUsername() + " created a new task: " + task.getTitle()
                    + " at " + task.getCreatedAt();
            applicationEventPublisher.publishEvent(new TaskCreatedEvent(this, message, request.getGroupId()));
        } catch (IOException e) {
            throw new RuntimeException("Error while saving task: " + e.getMessage());
        }

        return "successfully created task";
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

        File file = new File(task.getFilePathOfTask());
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

    @Override
    public Boolean deleteTask(Integer taskId) {
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        String filePath = task.getFilePathOfTask();

        try {
                if (filePath != null && !filePath.isEmpty()) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        if (!file.delete()) {
                            System.err.println("An error occurred during the file deletion process. : " + filePath);
                        }
                    } else {
                        System.err.println("File does not exits: " + filePath);
                    }
                }
            taskRepository.delete(task);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean deleteFileOfTask(Integer taskId) {
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        String filePath = task.getFilePathOfTask();

        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if (file.exists()) {
                if (!file.delete()) {
                    System.err.println("An error occurred during the file deletion process. : " + filePath);
                }
            } else {
                System.err.println("File does not exits: " + filePath);
            }
            task.setFilePathOfTask(null);
            taskRepository.save(task);
            return true;
        }

        return false;
    }

    @Override
    public Boolean addFileToTask(Integer taskId, FileOfTaskRequest request) {
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        Group group = task.getGroup();

        try {
            String nameOfGroup = StringCustomUtils.convertToSnakeCase(group.getNameOfGroup());
            String path = "FileOfGroup/" + nameOfGroup;
            String filePathSaved = FileUtils.saveFileToPath(path, request.getFileOfTask());
            task.setFilePathOfTask(filePathSaved);
            taskRepository.save(task);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
