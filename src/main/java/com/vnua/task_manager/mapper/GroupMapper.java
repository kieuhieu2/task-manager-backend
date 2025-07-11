package com.vnua.task_manager.mapper;

import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.response.groupRes.GroupCreateRes;
import com.vnua.task_manager.dto.response.groupRes.GroupGetRes;
import com.vnua.task_manager.dto.response.groupRes.GroupUpdateRes;
import com.vnua.task_manager.entity.Group;
import com.vnua.task_manager.entity.User;
import com.vnua.task_manager.repository.UserRepository;
import org.mapstruct.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserRepository.class})
public interface GroupMapper {
    @Mapping(target = "members", source = "memberCodes", qualifiedByName = "mapUsersByCodes")
    @Mapping(target = "leadersOfGroup", source = "leaderCodes", qualifiedByName = "mapUsersByCodes")
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", expression = "java(new java.util.Date())")
    @Mapping(target = "updatedAt", expression = "java(new java.util.Date())")
    @Mapping(target = "descriptionOfGroup", source = "descriptionOfGroup")
    @Mapping(target = "faculty", source = "faculty")
    @Mapping(target = "department", source = "department")
    @Mapping(target = "nameOfGroup", source = "nameOfGroup")
    @Mapping(target = "groupId", ignore = true)
    @Mapping(target = "pathOfGroupFolder", ignore = true)
    Group toEntity(GroupCreateReq request, @Context UserRepository userRepository);

    @Named("mapUsersByCodes")
    default Set<User> mapUsersByCodes(Set<String> codes, @Context UserRepository userRepository) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptySet();
        }

        List<User> users = userRepository.findByCodeIn(codes);

        if (users.size() != codes.size()) {
            Set<String> foundCodes = users.stream().map(User::getCode).collect(Collectors.toSet());
            codes.stream()
                    .filter(code -> !foundCodes.contains(code))
                    .forEach(code -> System.err.println("User not found with code: " + code));
        }
        return new HashSet<>(users);
    }

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "nameOfGroup", source = "nameOfGroup")
    @Mapping(target = "faculty", source = "faculty")
    @Mapping(target = "department", source = "department")
    @Mapping(target = "memberCodes", source = "members", qualifiedByName = "mapUserIds")
    @Mapping(target = "leaderCodes", source = "leadersOfGroup", qualifiedByName = "mapUserIds")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    GroupCreateRes toResponse(Group group);

    @Named("mapUserIds")
    default Set<String> mapUserIds(Set<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptySet();
        }
        return users.stream().map(User::getUserId).collect(Collectors.toSet());
    }

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "nameOfGroup", source = "nameOfGroup")
    @Mapping(target = "descriptionOfGroup", source = "descriptionOfGroup")
    List<GroupGetRes> toGroupGetResponse(List<Group> groups);

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "nameOfGroup", source = "nameOfGroup")
    @Mapping(target = "descriptionOfGroup", source = "descriptionOfGroup")
    @Mapping(target = "isLeader", ignore = true)
    GroupGetRes toGroupGetRes(Group group);

//    @Mapping(target = "groupId", ignore = true)
//    @Mapping(target = "members", ignore = true)
//    @Mapping(target = "leadersOfGroup", ignore = true)
//    @Mapping(target = "tasks", ignore = true)
//    @Mapping(target = "faculty", ignore = true)
//    @Mapping(target = "department", ignore = true)
//    @Mapping(target = "pathOfGroupFolder", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", expression = "java(new java.util.Date())")
//    @Mapping(target = "wasDeleted", ignore = true)
//    @Mapping(target = "isLeader", ignore = true)
//    Group updateGroup(@MappingTarget Group group, GroupUpdateReq request);

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "nameOfGroup", source = "nameOfGroup")
    @Mapping(target = "descriptionOfGroup", source = "descriptionOfGroup")
    GroupUpdateRes toGroupUpdateRes(Group group);
}