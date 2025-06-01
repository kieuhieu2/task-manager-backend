package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.commentReq.CommentReq;
import com.vnua.task_manager.dto.response.commentRes.CommentRes;
import com.vnua.task_manager.entity.Comment;

import java.util.List;

public interface CommentService {
    boolean createComment(CommentReq commentReq);
    List<CommentRes> findCommentsByTaskId(Integer taskId);
    Boolean updateComment(Integer commentId, CommentReq commentReq);
    Boolean deleteComment(Integer commentId);
}
