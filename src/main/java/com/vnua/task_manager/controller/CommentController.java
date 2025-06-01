package com.vnua.task_manager.controller;

import com.vnua.task_manager.dto.ApiResponse;
import com.vnua.task_manager.dto.request.commentReq.CommentReq;
import com.vnua.task_manager.dto.response.commentRes.CommentRes;
import com.vnua.task_manager.service.CommentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping
    public ApiResponse<Boolean> createComment(@RequestBody CommentReq commentReq) {
        return ApiResponse.<Boolean>builder()
                .result(commentService.createComment(commentReq))
                .build();
    }

    @GetMapping("/{taskId}")
    public ApiResponse<List<CommentRes>> findCommentsByTaskId(@PathVariable Integer taskId) {
        return ApiResponse.<List<CommentRes>>builder()
                .result(commentService.findCommentsByTaskId(taskId))
                .build();
    }

    @PutMapping("/{commentId}")
    public ApiResponse<Boolean> updateComment(@PathVariable Integer commentId, @RequestBody CommentReq commentReq) {
        return ApiResponse.<Boolean>builder()
                .result(commentService.updateComment(commentId, commentReq))
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Boolean> deleteComment(@PathVariable Integer commentId) {
        return ApiResponse.<Boolean>builder()
                .result(commentService.deleteComment(commentId))
                .build();
    }
}
