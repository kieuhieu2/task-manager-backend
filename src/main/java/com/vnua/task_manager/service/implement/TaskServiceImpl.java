package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.taskReq.FileOfTaskRequest;
import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.dto.request.taskReq.UpdateTaskProgressRequest;
import com.vnua.task_manager.dto.request.taskReq.TaskDateRangeRequest;
import com.vnua.task_manager.dto.request.taskReq.UpdateTaskStateRequest;
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
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;

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
    @Transactional
    public String createTask(TaskCreationRequest request) {
        try {
            Task task = taskFactory.createTask(request);
            taskRepository.save(task);

            Group group = task.getGroup();
            
            switch (request.getTaskType()) {
                case PUBLIC_TASK:
                    Set<User> usersInGroup = group.getMembers();
                    for (User user : usersInGroup) {
                        mapAndSaveTask(task, user);
                    }
                    break;
                    
                case PRIVATE_TASK:
                    if (request.getAssigneesUserCode() == null || request.getAssigneesUserCode().isEmpty()) {
                        throw new IllegalArgumentException("Private tasks require assignees");
                    }
                    
                    for (String userCode : request.getAssigneesUserCode()) {
                        User user = userRepository.findByCode(userCode)
                                .orElseThrow(() -> new IllegalArgumentException("User not found with code: " + userCode));

                        mapAndSaveTask(task, user);
                    }
                    
                    break;
            }

            String message = "Người dùng " + task.getWhoCreated().getUsername() + " đã tạo công việc: " + task.getTitle();
            applicationEventPublisher.publishEvent(new TaskCreatedEvent(this, message, request.getGroupId()));
            return "successfully created task";
        } catch (Exception e) {
            throw new RuntimeException("Error while saving task: " + e.getMessage());
        }
    }

    private void mapAndSaveTask(Task task, User user) {
        Integer maxPosition = getMaxPositionInColumn(user.getUserId(), TaskState.TODO, task.getGroup().getGroupId());
        
        if (maxPosition > 0) {
            shiftTaskPositions(user.getUserId(), TaskState.TODO, task.getGroup().getGroupId(), 1);
        }
        
        UserTaskStatus status = new UserTaskStatus();
        status.setId(new UserTaskId(user.getUserId(), task.getTaskId()));
        status.setUser(user);
        status.setTask(task);
        status.setState(TaskState.TODO);
        status.setPercentDone(0);
        status.setPositionInColumn(1);
        status.setUpdatedAt(new Date());
        userTaskStatusRepository.save(status);
    }
    

    @Override
    public List<TaskResponse> getTaskByGroupId(Integer groupId) {
        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Task> tasks = taskRepository.findByGroup_GroupId(groupId);

        // Get non-DONE tasks and recent DONE tasks with separate queries (due to MySQL limitation)
        List<UserTaskStatus> nonDoneTasks = userTaskStatusRepository.findNonDoneTasksForUserAndGroup(
                user.getUserId(), groupId);
                
        List<UserTaskStatus> recentDoneTasks = userTaskStatusRepository.findRecentDoneTasksForUserAndGroup(
                user.getUserId(), groupId, 20);
        
        // Combine the results
        List<UserTaskStatus> userStatuses = new ArrayList<>();
        userStatuses.addAll(nonDoneTasks);
        userStatuses.addAll(recentDoneTasks);

        // Create maps for task state, position, and percentDone
        Map<Integer, TaskState> taskIdToStateMap = new HashMap<>();
        Map<Integer, Integer> taskIdToPositionMap = new HashMap<>();
        Map<Integer, Integer> taskIdToPercentDoneMap = new HashMap<>();
        
        // Populate maps with data from UserTaskStatus
        for (UserTaskStatus status : userStatuses) {
            Integer taskId = status.getTask().getTaskId();
            taskIdToStateMap.put(taskId, status.getState());
            taskIdToPositionMap.put(taskId, status.getPositionInColumn());
            taskIdToPercentDoneMap.put(taskId, status.getPercentDone());
        }
                
        return tasks.stream()
                .filter(task -> taskIdToStateMap.containsKey(task.getTaskId()))
                .map(task -> {
                    TaskState userState = taskIdToStateMap.get(task.getTaskId());
                    Integer position = taskIdToPositionMap.get(task.getTaskId());
                    Integer percentDone = taskIdToPercentDoneMap.get(task.getTaskId());
                    Boolean isCreator = user.getUserId().equals(task.getWhoCreated().getUserId());
                    return taskMapper.toTaskResponse(task, userState, isCreator, position, percentDone);
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
    
    @Override
    public String updateTaskProgress(Integer taskId, UpdateTaskProgressRequest request) {
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
                
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));
                
        UserTaskId taskIdKey = new UserTaskId(user.getUserId(), task.getTaskId());
        
        UserTaskStatus statusOfTask = userTaskStatusRepository.findById(taskIdKey)
                .orElseThrow(() -> new IllegalArgumentException("No task assignment for user and task"));
                
        statusOfTask.setPercentDone(request.getPercentDone());
        statusOfTask.setUpdatedAt(new java.util.Date());
        
        // Automatically update state based on percentDone
        if (request.getPercentDone() == 0) {
            statusOfTask.setState(TaskState.TODO);
        } else if (request.getPercentDone() == 100) {
            statusOfTask.setState(TaskState.DONE);
        } else if (statusOfTask.getState() == TaskState.TODO) {
            statusOfTask.setState(TaskState.IN_PROGRESS);
        }
        
        userTaskStatusRepository.save(statusOfTask);
        return "Update task progress successfully";
    }

    @Override
    public List<TaskResponse> getTasksByDateRangeAndUser(TaskDateRangeRequest request) {
        User user = userRepository.findByCode(request.getUserCode())
                .orElseThrow(() -> new IllegalArgumentException("User not found with code: " + request.getUserCode()));
        
        List<Task> tasks = taskRepository.findTasksByUserCodeAndDateRange(
                request.getUserCode(), 
                request.getFromDate(), 
                request.getToDate());
        
        return tasks.stream()
                .map(task -> {
                    UserTaskId taskIdKey = new UserTaskId(user.getUserId(), task.getTaskId());
                    UserTaskStatus statusOfTask = userTaskStatusRepository.findById(taskIdKey)
                            .orElse(null);
                    
                    TaskState userState = (statusOfTask != null) 
                            ? statusOfTask.getState() 
                            : task.getState();
                    
                    Integer positionInColumn = (statusOfTask != null)
                            ? statusOfTask.getPositionInColumn()
                            : null;
                    
                    Integer percentDone = (statusOfTask != null)
                            ? statusOfTask.getPercentDone()
                            : task.getPercentDone();
                    
                    Boolean isCreator = user.getUserId().equals(task.getWhoCreated().getUserId());
                    return taskMapper.toTaskResponse(task, userState, isCreator, positionInColumn, percentDone);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String updateStatusAndPosition(UpdateTaskStateRequest request) {
        var context = SecurityContextHolder.getContext();
        String userCodeOfContext = context.getAuthentication().getName();
        User user = userRepository.findByCode(userCodeOfContext)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userCodeOfContext));

        Integer taskId = request.getTaskId();
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        UserTaskId taskIdKey = new UserTaskId(user.getUserId(), task.getTaskId());
        UserTaskStatus statusAndPossitionInColumnOfTask = userTaskStatusRepository.findById(taskIdKey)
                .orElseThrow(() -> new IllegalArgumentException("No task assignment for user and task"));

        // Save the previous state before updating
        TaskState previousState = statusAndPossitionInColumnOfTask.getState();
        Integer previousPosition = statusAndPossitionInColumnOfTask.getPositionInColumn();
        TaskState newState = request.getNewState();
        Integer targetPosition = request.getPositionInColumn();
        
        // Update basic task information
        statusAndPossitionInColumnOfTask.setUpdatedAt(new Date());
        
        // Check if state has changed
        boolean stateChanged = !previousState.equals(newState);
        
        // Update state and percentage based on the new state
        statusAndPossitionInColumnOfTask.setState(newState);
        if (newState == TaskState.DONE) {
            statusAndPossitionInColumnOfTask.setPercentDone(100);
        } else if (newState == TaskState.TODO) {
            statusAndPossitionInColumnOfTask.setPercentDone(0);
        }
        
        // Handle position updates based on whether state changed or not
        if (stateChanged) {
            // CASE 1: State changed - need to handle positions in both states
            
            // First, make space in the target state at the requested position
            if (targetPosition > 0) {
                List<UserTaskStatus> tasksToShift = userTaskStatusRepository
                    .findByUser_UserIdAndStateAndTask_Group_GroupIdAndPositionInColumnGreaterThanEqual(
                        user.getUserId(), newState, task.getGroup().getGroupId(), targetPosition);
                        
                for (UserTaskStatus status : tasksToShift) {
                    status.setPositionInColumn(status.getPositionInColumn() + 1);
                    userTaskStatusRepository.save(status);
                }
            }
            
            // Set the position for the current task
            statusAndPossitionInColumnOfTask.setPositionInColumn(targetPosition);
            userTaskStatusRepository.save(statusAndPossitionInColumnOfTask);
            
            // Reorganize positions in the original state (removing gap left by moved task)
            reorganizeTaskPositions(user.getUserId(), previousState, task.getGroup().getGroupId(), previousPosition);
            
            log.info("Task {} moved from state {} (position {}) to state {} (position {})", 
                taskId, previousState, previousPosition, newState, targetPosition);
        } else {
            // CASE 2: State didn't change, just reordering within the same state
            
            // Set the position for the current task
            statusAndPossitionInColumnOfTask.setPositionInColumn(targetPosition);
            userTaskStatusRepository.save(statusAndPossitionInColumnOfTask);
            
            // Reorganize all tasks in this state to ensure proper sequential ordering
            reorganizeTaskPositions(user.getUserId(), newState, task.getGroup().getGroupId(), null);
            
            log.info("Task {} reordered within state {} from position {} to position {}", 
                taskId, newState, previousPosition, targetPosition);
        }
        
        return "Updated task status and position successfully";
    }

    /**
     * Reorganizes task positions in a column after a task has been moved out
     * This ensures that positions remain sequential without gaps
     * 
     * @param userId User ID
     * @param state Task state (column)
     * @param groupId Group ID
     * @param removedPosition Position that was removed (can be null when just reordering)
     */
    private void reorganizeTaskPositions(String userId, TaskState state, Integer groupId, Integer removedPosition) {
        // Get all tasks in the column
        List<UserTaskStatus> tasksToReorganize = userTaskStatusRepository
                .findByUser_UserIdAndStateAndTask_Group_GroupId(userId, state, groupId);
                
        if (tasksToReorganize.isEmpty()) {
            return;
        }
        
        // Sort tasks by position to ensure correct ordering
        tasksToReorganize.sort(Comparator.comparing(UserTaskStatus::getPositionInColumn));
        
        // Reassign positions sequentially, always starting from 1
        for (int i = 0; i < tasksToReorganize.size(); i++) {
            UserTaskStatus status = tasksToReorganize.get(i);
            int newPosition = i + 1; // Position starts from 1
            
            // Update if positions are different from the expected sequence
            if (newPosition != status.getPositionInColumn()) {
                status.setPositionInColumn(newPosition);
                userTaskStatusRepository.save(status);
            }
        }
        
        log.info("Reorganized {} tasks in column {} for user {} in group {}", 
                tasksToReorganize.size(), state, userId, groupId);
    }

    // for create task
    private Integer getMaxPositionInColumn(String userId, TaskState state, Integer groupId) {
        List<UserTaskStatus> statuses = userTaskStatusRepository.findByUser_UserIdAndStateAndTask_Group_GroupId(
                userId, state, groupId);
        return statuses.stream()
                .filter(s -> s.getPositionInColumn() != null)
                .map(UserTaskStatus::getPositionInColumn)
                .max(Integer::compare)
                .orElse(0);
    }
    
    private void shiftTaskPositions(String userId, TaskState state, Integer groupId, Integer startPosition) {
        List<UserTaskStatus> tasksToShift = userTaskStatusRepository
                .findByUser_UserIdAndStateAndTask_Group_GroupIdAndPositionInColumnGreaterThanEqual(
                        userId, state, groupId, startPosition);
    
        for (UserTaskStatus status : tasksToShift) {
            status.setPositionInColumn(status.getPositionInColumn() + 1);
            userTaskStatusRepository.save(status);
        }
    }
    
}