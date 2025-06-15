package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.entity.PrivateTaskOfGroup;
import com.vnua.task_manager.entity.Task;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.event.TaskCreatedEvent;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.mapper.TaskMapper;
import com.vnua.task_manager.repository.PrivateTaskOfGroupRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.PrivateTaskService;
import com.vnua.task_manager.service.factories.TaskFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class PrivateTaskServiceImpl implements PrivateTaskService {
    PrivateTaskOfGroupRepository privateTaskOfGroupRepository;
    TaskFactory taskFactory;
    TaskMapper taskMapper;
    ApplicationEventPublisher applicationEventPublisher;
    UserRepository userRepository;

    @Override
    public Boolean createPrivateTask(TaskCreationRequest request) {
        if (request.getAssigneesUserCode() == null || request.getAssigneesUserCode().isEmpty()) {
            return false;
        } else
        {
            try {
                Task task = taskFactory.createTask(request);
                PrivateTaskOfGroup privateTaskOfGroup = taskMapper.toPrivateTaskOfGroup(task);

                List<User> users = new ArrayList<>();

                for (String userCode : request.getAssigneesUserCode()) {
                    User user = userRepository.findByCode(userCode)
                            .orElseThrow(() -> new RuntimeException("User not found with code: " + userCode));
                    users.add(user);
                }
                privateTaskOfGroup.setAssigneesUser(users);
                privateTaskOfGroupRepository.save(privateTaskOfGroup);

                String message = "User " + task.getWhoCreated().getUsername() + " created a new task: " + task.getTitle()
                        + " at " + task.getCreatedAt();
                applicationEventPublisher.publishEvent(new TaskCreatedEvent(this, message, request.getGroupId()));

                return true;

            } catch (IOException e) {
                throw new AppException("Error while saving task: " + e.getMessage());
            }
        }
    }
}
