package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.taskReq.FileOfTaskRequest;
import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.dto.response.taskRes.MemberWorkProgressResponse;
import com.vnua.task_manager.dto.response.taskRes.TaskResponse;
import com.vnua.task_manager.entity.*;
import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import com.vnua.task_manager.event.TaskCreatedEvent;
import com.vnua.task_manager.mapper.TaskMapper;
import com.vnua.task_manager.repository.TaskRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.repository.UserTaskStatusRepository;
import com.vnua.task_manager.service.TaskService;
import com.vnua.task_manager.service.factories.TaskFactory;
import com.vnua.task_manager.utils.FileUtils;
import com.vnua.task_manager.utils.StringCustomUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;

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
    UserTaskStatusRepository userTaskStatusRepository;

    @Override
    public String createTask(TaskCreationRequest request) {
        try {
            Task task = taskFactory.createTask(request);
            taskRepository.save(task);

            Group group = task.getGroup();
            Set<User> usersInGroup = group.getMembers();

            for (User user : usersInGroup) {
                UserTaskStatus status = new UserTaskStatus();
                status.setId(new UserTaskId(user.getUserId(), task.getTaskId()));
                status.setUser(user);
                status.setTask(task);
                status.setState(TaskState.TODO);
                status.setPercentDone(0);
                status.setUpdatedAt(new Date());

                userTaskStatusRepository.save(status);
            }

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
        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Task> tasks = taskRepository.findByGroup_GroupId(groupId);

        List<UserTaskStatus> userStatuses = userTaskStatusRepository.findAllByUser_UserIdAndTask_Group_GroupId(user.getUserId(), groupId);
        Map<Integer, TaskState> taskIdToStateMap = userStatuses.stream()
                .collect(Collectors.toMap(
                        uts -> uts.getTask().getTaskId(),
                        UserTaskStatus::getState
                ));

        return tasks.stream()
                .map(task -> {
                    TaskState userState = taskIdToStateMap.getOrDefault(task.getTaskId(), TaskState.TODO);
                    Boolean isCreator = user.getUserId().equals(task.getWhoCreated().getUserId());
                    return taskMapper.toTaskResponse(task, userState, isCreator);
                })
                .collect(Collectors.toList());
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

        UserTaskId taskIdKey = new UserTaskId(user.getUserId(), task.getTaskId());

        UserTaskStatus statusOfTask = userTaskStatusRepository.findById(taskIdKey)
                .orElseThrow(() -> new IllegalArgumentException("No task assignment for user and task"));

        statusOfTask.setState(newState);
        statusOfTask.setUpdatedAt(new java.util.Date());

        if (newState == TaskState.DONE) {
            statusOfTask.setPercentDone(100);
        } else if (newState == TaskState.TODO) {
            statusOfTask.setPercentDone(0);
        }

        userTaskStatusRepository.save(statusOfTask);
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

    @Override
    public List<MemberWorkProgressResponse> getWorkProcessOfMembersInGroup(Integer taskId) {
        
        List<UserTaskStatus> statuses = userTaskStatusRepository.findAllByTask_TaskId(taskId);
        List<MemberWorkProgressResponse> responses = new ArrayList<>();
        
        for (UserTaskStatus status : statuses) {
            User user = status.getUser();
            MemberWorkProgressResponse response = MemberWorkProgressResponse.builder()
                    .userCode(user.getCode())
                    .state(status.getState())
                    .percentDone(status.getPercentDone())
                    .updatedAt(status.getUpdatedAt())
                    .build();
            responses.add(response);
        }
        
        return responses;
    }
}