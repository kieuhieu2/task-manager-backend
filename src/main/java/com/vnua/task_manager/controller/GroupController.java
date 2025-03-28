package com.vnua.task_manager.controller;

import com.vnua.task_manager.dto.ApiResponse;
import com.vnua.task_manager.dto.request.groupReq.GroupCreateReq;
import com.vnua.task_manager.dto.response.groupRes.GroupCreateRes;
import com.vnua.task_manager.dto.response.groupRes.GroupGetResponse;
import com.vnua.task_manager.service.GroupService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<GroupCreateRes> createGroup(@Valid @RequestBody GroupCreateReq request) {
        var result = groupService.createGroup(request);
        return ApiResponse.<GroupCreateRes>builder()
                .result(result)
                .build();
    }

    @GetMapping
    ApiResponse<List<GroupGetResponse>> getAllGroup() {
        var result = groupService.getAllGroup();
        return ApiResponse.<List<GroupGetResponse>>builder()
                .result(result)
                .build();
    }
}
