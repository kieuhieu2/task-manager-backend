package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.request.groupReq.GroupUpdateReq;
import com.vnua.task_manager.dto.response.groupRes.GroupCreateRes;
import com.vnua.task_manager.dto.response.groupRes.GroupGetRes;
import com.vnua.task_manager.dto.response.groupRes.GroupUpdateRes;
import com.vnua.task_manager.entity.Group;
import com.vnua.task_manager.entity.Task;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.mapper.GroupMapper;
import com.vnua.task_manager.repository.GroupRepository;
import com.vnua.task_manager.repository.TaskRepository;
import com.vnua.task_manager.repository.UserRepository;
import com.vnua.task_manager.service.GroupService;
import com.vnua.task_manager.service.factories.GroupFactory;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GroupServiceImpl implements GroupService {
    GroupRepository groupRepository;
    UserRepository userRepository;
    GroupMapper groupMapper;
    SecurityServiceImpl securityServiceImpl;
    GroupFactory groupFactory;
    TaskRepository taskRepository;

    @Override
    public GroupCreateRes createGroup(GroupCreateReq request) {

        try {
            Group savedGroup = groupRepository.save(groupFactory.createGroup(request));
            return groupMapper.toResponse(savedGroup);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create group due to data integrity violation: {}", e.getMessage());
            throw new AppException("Lỗi: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public List<GroupGetRes> getAllGroup() {
        List<Group> groups = groupRepository.findAll();
        List<GroupGetRes> groupGetResList = groupMapper.toGroupGetResponse(groups);

        for (GroupGetRes groupGetRes : groupGetResList) {
            groupGetRes.setIsLeader(securityServiceImpl.isGroupLeader(groupGetRes.getGroupId()));
        }

        return groupGetResList;
    }

    @Override
    @Transactional
    @PreAuthorize("@securityServiceImpl.isGroupLeader(#request.groupId)")
    public GroupUpdateRes updateGroup(GroupUpdateReq request) {
        try {
            Group updatedGroup = groupFactory.updateGroup(request);
            groupRepository.save(updatedGroup);
            return groupMapper.toGroupUpdateRes(updatedGroup);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional
    @PreAuthorize("@securityServiceImpl.isGroupLeader(#groupId)")
    public String deleteGroup(Integer groupId) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));

        Set<User> members = new HashSet<>(group.getMembers()); // Sao chép để tránh ConcurrentModificationException
        for (User user : members) {
            user.getGroups().remove(group); // Xóa Group khỏi danh sách groups của User
            userRepository.save(user); // Lưu để cập nhật bảng users_groups
        }

        // 2. Xóa liên kết trong users_group_leaders (quan hệ leadersOfGroup)
        Set<User> leaders = new HashSet<>(group.getLeadersOfGroup());
        for (User leader : leaders) {
            leader.getGroupLeaders().remove(group);
            userRepository.save(leader);
        }

        taskRepository.deleteByGroupId(groupId);

        groupRepository.delete(group);

        return "success";
    }

    @Override
    public List<GroupGetRes> getMyGroups(String userCode) {
        User user = userRepository.findByCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("User not found with code: " + userCode));

        List<Group> groups = groupRepository.getMyGroups(user.getUserId());
        List<GroupGetRes> groupGetResList = groupMapper.toGroupGetResponse(groups);

        for (GroupGetRes groupGetRes : groupGetResList) {
            groupGetRes.setIsLeader(securityServiceImpl.isGroupLeader(groupGetRes.getGroupId()));
        }

        return groupGetResList;
    }

    @Override
    public Boolean addUserToGroup(Integer groupId, String userCode) {
        User user = userRepository.findByCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("User not found with code: " + userCode));
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));

        group.getMembers().add(user);
        user.getGroups().add(group);

        groupRepository.save(group);
        userRepository.save(user);

        return true;
    }
}
