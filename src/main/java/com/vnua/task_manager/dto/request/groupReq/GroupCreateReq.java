package com.vnua.task_manager.dto.request.groupReq;

import com.vnua.task_manager.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
public class GroupCreateReq {

    private Set<String> memberCodes;  // Danh sách id của members
    private Set<String> leaderCodes;

    @NotBlank(message = "Name of group is required")
    private String nameOfGroup;

    @NotBlank(message = "Faculty is required")
    private String faculty;

    @NotBlank(message = "Department is required")
    private String department;
}
