package com.vnua.task_manager.service.factories;

import com.vnua.task_manager.dto.request.commentReq.CommentReq;
import com.vnua.task_manager.entity.Comment;

public interface CommentFactory {
    Comment createComment(CommentReq commentReq);
}
