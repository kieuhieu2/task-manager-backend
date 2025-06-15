package com.vnua.task_manager.dto.response.taskRes;

import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberWorkProgressResponse {
    private String userCode;
    private TaskState state;
    private Integer percentDone;
    private Date updatedAt;
} 