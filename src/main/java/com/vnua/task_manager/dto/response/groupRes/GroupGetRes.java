package com.vnua.task_manager.dto.response.groupRes;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupGetRes {
    Integer groupId;
    String nameOfGroup;
    String descriptionOfGroup;
    Boolean isLeader;
}
