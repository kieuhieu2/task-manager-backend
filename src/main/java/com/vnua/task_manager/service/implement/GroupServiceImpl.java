package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.response.groupRes.GroupCreateRes;
import com.vnua.task_manager.dto.response.groupRes.GroupGetResponse;
import com.vnua.task_manager.entity.Group;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.exception.ErrorCode;
import com.vnua.task_manager.mapper.GroupMapper;
import com.vnua.task_manager.repository.GroupRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.GroupService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.List;

import static com.vnua.task_manager.utils.StringCustomUtils.convertToSnakeCase;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GroupServiceImpl implements GroupService {
    GroupRepository groupRepository;
    UserRepository userRepository;
    GroupMapper groupMapper;

    @Override
    public GroupCreateRes createGroup(GroupCreateReq request) {

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

        group.getMembers().forEach(user -> {
            user.getGroups().add(group);
            log.debug("Added group {} to user {} groups", group.getGroupId(), user.getUserId());
        });

        try {
            Group savedGroup = groupRepository.save(group);
            return groupMapper.toResponse(savedGroup);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create group due to data integrity violation: {}", e.getMessage());
            throw new AppException("Lỗi: " + e.getMessage());
        }
    }

    @Override
    public List<GroupGetResponse> getAllGroup() {
        List<Group> groups = groupRepository.findAll();
        return groupMapper.toGroupGetResponse(groups);
    }
}
