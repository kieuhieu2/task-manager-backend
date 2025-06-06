package com.vnua.task_manager.dto.response.commentRes;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentRes {
    Integer commentId;
    String commentText;
    String userName;
    String userCode;
}