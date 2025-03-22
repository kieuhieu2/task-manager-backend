package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.response.groupRes.GroupCreateRes;
import com.vnua.task_manager.entity.Group;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.exception.ErrorCode;
import com.vnua.task_manager.mapper.GroupMapper;
import com.vnua.task_manager.repository.GroupRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.GroupService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        log.info("Creating group with request: {}", request);

        // Tạo Group từ request
        Group group = groupMapper.toEntity(request, userRepository);

        // Lấy thông tin leader từ token
        var context = SecurityContextHolder.getContext();
        String code = context.getAuthentication().getName();

        User creator = userRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Creator not found with code: {}", code);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });

        group.getLeadersOfGroup().add(creator);
        creator.getGroupLeaders().add(group);

        // Cập nhật mối quan hệ @ManyToMany cho members
        group.getMembers().forEach(user -> {
            user.getGroups().add(group);
            log.debug("Added group {} to user {} groups", group.getGroupId(), user.getUserId());
        });

        // Lưu group
        try {
            Group savedGroup = groupRepository.save(group);
            log.info("Group created successfully with id: {}", savedGroup.getGroupId());
            return groupMapper.toResponse(savedGroup);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create group due to data integrity violation: {}", e.getMessage());
            throw new RuntimeException("Group creation failed: duplicate or invalid data");
        }
    }
}
