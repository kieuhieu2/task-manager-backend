package com.vnua.task_manager.dto.request.commentReq;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentReq {
    Integer taskId;
    String commentText;
}
