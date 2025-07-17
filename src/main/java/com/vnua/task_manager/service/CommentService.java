package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.commentReq.CommentReq;
import com.vnua.task_manager.dto.response.commentRes.CommentRes;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommentService {
    boolean createComment(CommentReq commentReq, MultipartFile commentFile);
    List<CommentRes> findCommentsByTaskId(Integer taskId);
    Boolean updateComment(Integer commentId, CommentReq commentReq);
    Boolean deleteComment(Integer commentId);
    ResponseEntity<Resource> getCommentFile(Integer taskId, Integer commentId);
}
