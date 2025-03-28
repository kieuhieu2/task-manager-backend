package com.vnua.task_manager.service.factories.factoryImpl;

import com.vnua.task_manager.dto.request.taskReq.TaskCreationRequest;
import com.vnua.task_manager.entity.Group;
import com.vnua.task_manager.entity.Task;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import com.vnua.task_manager.mapper.TaskMapper;
import com.vnua.task_manager.repository.GroupRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.factories.TaskFactory;
import com.vnua.task_manager.utils.FileUtils;
import com.vnua.task_manager.utils.StringCustomUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskFactoryImpl implements TaskFactory {
     TaskMapper taskMapper;
     UserRepository userRepository;
     GroupRepository groupRepository;

    @Override
    public Task createTask(TaskCreationRequest request) throws IOException {
        Task task = taskMapper.toTask(request);
        task.setCreatedAt(new java.util.Date());

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setWhoCreated(user);

        Group group = groupRepository.findByGroupId(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        task.setGroup(group);

        task.setState(TaskState.TODO);

        if (request.getFileOfTask() != null && !request.getFileOfTask().isEmpty()) {
            String nameOfGroup = StringCustomUtils.convertToSnakeCase(group.getNameOfGroup());
            String path = "FileOfGroup/" + nameOfGroup;
            String filePathSaved = FileUtils.saveFileToPath(path, request.getFileOfTask());
            task.setFilePathOfTask(filePathSaved);
        }

        task.setWasDeleted(false);

        return task;
    }
}

