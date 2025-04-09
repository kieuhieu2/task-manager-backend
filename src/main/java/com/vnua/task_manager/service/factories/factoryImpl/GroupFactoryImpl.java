package com.vnua.task_manager.service.factories.factoryImpl;

import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.request.groupReq.GroupUpdateReq;
import com.vnua.task_manager.entity.Group;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.exception.ErrorCode;
import com.vnua.task_manager.mapper.GroupMapper;
import com.vnua.task_manager.repository.GroupRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.factories.GroupFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.vnua.task_manager.utils.StringCustomUtils.convertToSnakeCase;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class GroupFactoryImpl implements GroupFactory {
    GroupMapper groupMapper;
    UserRepository userRepository;
    GroupRepository groupRepository;

    @Override
    public Group createGroup(GroupCreateReq request) throws IOException {
        Group group = groupMapper.toEntity(request, userRepository);

        var context = SecurityContextHolder.getContext();
        String code = context.getAuthentication().getName();

        User creator = userRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Creator not found with code: {}", code);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });

        String nameOfGroup = convertToSnakeCase(group.getNameOfGroup());
        String folderPathOfGroup = "FileOfGroup/" + nameOfGroup;
        File groupFolder = new File(folderPathOfGroup);

        if(!groupFolder.exists()) {
            boolean folderCreated = groupFolder.mkdirs();
            if(!folderCreated) {
                throw new AppException(ErrorCode.FOLDER_CREATION_FAILED);
            }
        }

        group.setPathOfGroupFolder(folderPathOfGroup);

        group.getLeadersOfGroup().add(creator);
        creator.getGroupLeaders().add(group);

        if (request.getLeaderCodes() != null) {
            for (String leaderCode : request.getLeaderCodes()) {
                User user = userRepository.findByCode(leaderCode)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                user.getGroupLeaders().add(group);
                group.getLeadersOfGroup().add(user);
            }
        }

        group.getMembers().forEach(user -> {
            user.getGroups().add(group);
            log.debug("Added group {} to user {} groups", group.getGroupId(), user.getUserId());
        });
        return group;
    }

    @Override
    public Group updateGroup(GroupUpdateReq request) throws IOException {
        if (request == null || request.getGroupId() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        Group group = groupRepository.findByGroupId(request.getGroupId())
                .orElseThrow(() -> {
                    log.error("Group not found with ID: {}", request.getGroupId());
                    return new AppException(ErrorCode.GROUP_NOT_FOUND);
                });

        String oldNameOfGroup = group.getNameOfGroup();
        String oldFolderPath = group.getPathOfGroupFolder();

        groupMapper.updateGroup(group, request);

        if (request.getNameOfGroup() != null && !request.getNameOfGroup().equals(oldNameOfGroup)) {
            String newNameOfGroup = convertToSnakeCase(request.getNameOfGroup());
            String newFolderPath = "FileOfGroup/" + newNameOfGroup;

            File oldFolder = new File(oldFolderPath);
            File newFolder = new File(newFolderPath);

            if (oldFolder.exists()) {
                try {
                    Files.move(Paths.get(oldFolderPath), Paths.get(newFolderPath));
                    group.setPathOfGroupFolder(newFolderPath);
                } catch (IOException e) {
                    log.error("Failed to rename folder from {} to {}: {}", oldFolderPath, newFolderPath, e.getMessage());
                    throw new AppException(ErrorCode.FOLDER_RENAME_FAILED);
                }
            } else {
                if (!newFolder.exists()) {
                    boolean folderCreated = newFolder.mkdirs();
                    if (!folderCreated) {
                        log.error("Failed to create new folder: {}", newFolderPath);
                        throw new AppException(ErrorCode.FOLDER_CREATION_FAILED);
                    }
                }
                group.setPathOfGroupFolder(newFolderPath);
            }
        }

        return group;
    }
}
