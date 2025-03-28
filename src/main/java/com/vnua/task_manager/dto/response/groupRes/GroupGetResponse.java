package com.vnua.task_manager.dto.response.groupRes;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupGetResponse {
    Integer groupId;
    String nameOfGroup;
    String descriptionOfGroup;
}
