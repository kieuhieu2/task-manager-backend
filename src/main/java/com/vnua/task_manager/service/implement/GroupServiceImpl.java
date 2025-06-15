package com.vnua.task_manager.service.implement;

import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.request.groupReq.GroupUpdateReq;
import com.vnua.task_manager.dto.response.groupRes.GroupCreateRes;
import com.vnua.task_manager.dto.response.groupRes.GroupGetRes;
import com.vnua.task_manager.dto.response.groupRes.GroupMemberRes;
import com.vnua.task_manager.dto.response.groupRes.GroupUpdateRes;
import com.vnua.task_manager.entity.Group;
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
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Builder
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

            GroupCreateRes groupCreateRes = groupMapper.toResponse(savedGroup);
            if (groupCreateRes.getNameOfGroup() == null) {
                log.warn("Group name is null after creation, using manual mapping");
                groupCreateRes = manuallyMapFromGroupToGroupCreateRes(savedGroup);
            }
            return groupCreateRes;

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

        // fix bug when mapper not work
        for (int i = 0; i < groupGetResList.size(); i++) {
            GroupGetRes res = groupGetResList.get(i);
            Group group = groups.get(i);

            if (res.getNameOfGroup() == null && group.getNameOfGroup() != null) {
                res.setNameOfGroup(group.getNameOfGroup());
                res.setDescriptionOfGroup(group.getDescriptionOfGroup());
            }
        }

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
    
    @Override
    @Transactional
    @PreAuthorize("@securityServiceImpl.isGroupLeader(#groupId)")
    public Boolean removeUserFromGroup(Integer groupId, String userCode) {
        User user = userRepository.findByCode(userCode)
                .orElseThrow(() -> new IllegalArgumentException("User not found with code: " + userCode));
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));

        group.getMembers().remove(user);
        user.getGroups().remove(group);

        groupRepository.save(group);
        userRepository.save(user);
        
        return true;
    }

    public GroupCreateRes manuallyMapFromGroupToGroupCreateRes(Group group) {
        Set<String> memberCodes = group.getMembers() != null
                ? group.getMembers().stream()
                .map(User::getCode)
                .collect(Collectors.toSet())
                : Collections.emptySet();

        Set<String> leaderCodes = group.getLeadersOfGroup() != null
                ? group.getLeadersOfGroup().stream()
                .map(User::getCode)
                .collect(Collectors.toSet())
                : Collections.emptySet();

        return GroupCreateRes.builder()
                .groupId(group.getGroupId())
                .nameOfGroup(group.getNameOfGroup())
                .faculty(group.getFaculty())
                .department(group.getDepartment())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .memberCodes(memberCodes)
                .leaderCodes(leaderCodes)
                .build();
    }

    @Override
    public List<GroupMemberRes> getGroupMembers(Integer groupId) {
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));
        
        Set<String> leaderIds = group.getLeadersOfGroup().stream()
                .map(User::getUserId)
                .collect(Collectors.toSet());

        return group.getMembers().stream()
                .map(user -> GroupMemberRes.builder()
                        .userCode(user.getCode())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .isLeader(leaderIds.contains(user.getUserId()))
                        .build())
                .collect(Collectors.toList());
    }

}
