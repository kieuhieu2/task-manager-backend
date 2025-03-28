package com.vnua.task_manager.dto.request.groupReq;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupCreateReq {

    private Set<String> memberCodes;  // Danh sách id của members
    private Set<String> leaderCodes;

    @NotBlank(message = "Name of group is required")
    String nameOfGroup;

    @NotBlank(message = "Faculty is required")
    String faculty;

    @NotBlank(message = "Department is required")
    String department;

    String descriptionOfGroup;
}
