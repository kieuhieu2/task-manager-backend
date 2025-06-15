package com.vnua.task_manager.dto.response.groupRes;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupCreateRes {
    Integer groupId;
    String nameOfGroup;
    String faculty;
    String department;
    Set<String> memberCodes;
    Set<String> leaderCodes;
    Date createdAt;
    Date updatedAt;
}
