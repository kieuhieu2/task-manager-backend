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
import java.util.Date;
import java.util.HashSet;

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
        log.info("Creating group from request: nameOfGroup={}, faculty={}, department={}", 
                request.getNameOfGroup(), request.getFaculty(), request.getDepartment());
        
        Group group = groupMapper.toEntity(request, userRepository);
        
        log.info("After mapping to entity: group={}, nameOfGroup={}", 
                group, group != null ? group.getNameOfGroup() : "null");

        // If nameOfGroup is null, use manual mapping
        if (group == null || group.getNameOfGroup() == null) {
            log.warn("MapStruct mapping failed or nameOfGroup is null, using manual mapping");
            group = manuallyCreateGroup(request);
        }

        final Group finalGroup = group; // Create a final reference for use in lambdas
        
        var context = SecurityContextHolder.getContext();
        String code = context.getAuthentication().getName();

        User creator = userRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Creator not found with code: {}", code);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });

        String nameOfGroup = convertToSnakeCase(finalGroup.getNameOfGroup());
        String folderPathOfGroup = "FileOfGroup/" + nameOfGroup;
        File groupFolder = new File(folderPathOfGroup);

        if(!groupFolder.exists()) {
            boolean folderCreated = groupFolder.mkdirs();
            if(!folderCreated) {
                throw new AppException(ErrorCode.FOLDER_CREATION_FAILED);
            }
        }

        finalGroup.setPathOfGroupFolder(folderPathOfGroup);

        finalGroup.getLeadersOfGroup().add(creator);
        creator.getGroupLeaders().add(finalGroup);

        if (request.getLeaderCodes() != null) {
            for (String leaderCode : request.getLeaderCodes()) {
                User user = userRepository.findByCode(leaderCode)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                user.getGroupLeaders().add(finalGroup);
                finalGroup.getLeadersOfGroup().add(user);
            }
        }

        finalGroup.getMembers().forEach(user -> {
            user.getGroups().add(finalGroup);
            log.debug("Added group {} to user {} groups", finalGroup.getGroupId(), user.getUserId());
        });
        
        log.info("Returning group with nameOfGroup={}", finalGroup.getNameOfGroup());
        return finalGroup;
    }
    
    private Group manuallyCreateGroup(GroupCreateReq request) {
        log.info("Creating group manually from request");
        Group group = getGroup(request);

        // Add members if available
        if (request.getMemberCodes() != null && !request.getMemberCodes().isEmpty()) {
            final Group finalGroup = group; // Create a final reference for use in lambda
            for (String memberCode : request.getMemberCodes()) {
                userRepository.findByCode(memberCode).ifPresent(user -> {
                    finalGroup.getMembers().add(user);
                });
            }
        }
        
        return group;
    }

    private static Group getGroup(GroupCreateReq request) {
        Group group = new Group();
        group.setNameOfGroup(request.getNameOfGroup());
        group.setFaculty(request.getFaculty());
        group.setDepartment(request.getDepartment());
        group.setDescriptionOfGroup(request.getDescriptionOfGroup());
        group.setCreatedAt(new Date());
        group.setUpdatedAt(new Date());
        group.setWasDeleted(false);

        // Initialize collections
        group.setMembers(new HashSet<>());
        group.setLeadersOfGroup(new HashSet<>());
        group.setTasks(new HashSet<>());
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

//        groupMapper.updateGroup(group, request);

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
