package com.vnua.task_manager.controller;

import com.vnua.task_manager.dto.ApiResponse;
import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.request.groupReq.GroupUpdateReq;
import com.vnua.task_manager.dto.response.groupRes.GroupCreateRes;
import com.vnua.task_manager.dto.response.groupRes.GroupGetRes;
import com.vnua.task_manager.dto.response.groupRes.GroupMemberRes;
import com.vnua.task_manager.dto.response.groupRes.GroupUpdateRes;
import com.vnua.task_manager.service.GroupService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    ApiResponse<GroupCreateRes> createGroup(@Valid @RequestBody GroupCreateReq request) {
        var result = groupService.createGroup(request);
        return ApiResponse.<GroupCreateRes>builder()
                .result(result)
                .build();
    }

    @GetMapping("/my-groups/{userCode}")
    ApiResponse<List<GroupGetRes>> getMyGroups(@PathVariable String userCode) {
        return ApiResponse.<List<GroupGetRes>>builder()
                .result(groupService.getMyGroups(userCode))
                .build();
    }

    @GetMapping
    ApiResponse<List<GroupGetRes>> getAllGroup() {
        var result = groupService.getAllGroup();
        return ApiResponse.<List<GroupGetRes>>builder()
                .result(result)
                .build();
    }

    @PutMapping
    ApiResponse<GroupUpdateRes> updateGroup(@Valid @RequestBody GroupUpdateReq request) {
        return ApiResponse.<GroupUpdateRes>builder()
                .result(groupService.updateGroup(request))
                .build();
    }

    @DeleteMapping("/{groupId}")
    ApiResponse<String> deleteGroup(@PathVariable Integer groupId) {
        return ApiResponse.<String>builder()
                .result(groupService.deleteGroup(groupId))
                .build();
    }

    @PostMapping("/add-user/{groupId}/{userCode}")
    ApiResponse<Boolean> addUserToGroup(@PathVariable Integer groupId, @PathVariable String userCode) {
        return ApiResponse.<Boolean>builder()
                .result(groupService.addUserToGroup(groupId, userCode))
                .build();
    }
    
    @DeleteMapping("/remove-user/{groupId}/{userCode}")
    ApiResponse<Boolean> removeUserFromGroup(@PathVariable Integer groupId, @PathVariable String userCode) {
        return ApiResponse.<Boolean>builder()
                .result(groupService.removeUserFromGroup(groupId, userCode))
                .build();
    }
    
    @GetMapping("/get-member/{groupId}")
    ApiResponse<List<GroupMemberRes>> getGroupMembers(@PathVariable Integer groupId) {
        return ApiResponse.<List<GroupMemberRes>>builder()
                .result(groupService.getGroupMembers(groupId))
                .build();
    }
}
