package com.vnua.task_manager.dto.request.groupReq;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupUpdateReq {
    Integer groupId;
    String nameOfGroup;
    String descriptionOfGroup;
}
