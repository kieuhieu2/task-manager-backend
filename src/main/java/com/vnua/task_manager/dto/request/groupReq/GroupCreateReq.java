package com.vnua.task_manager.dto.request.groupReq;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupCreateReq {

    Set<String> memberCodes;
    Set<String> leaderCodes;

    @NotBlank(message = "Name of group is required")
    String nameOfGroup;

    @NotBlank(message = "Faculty is required")
    String faculty;

    @NotBlank(message = "Department is required")
    String department;

    String descriptionOfGroup;
}
