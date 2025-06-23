package com.vnua.task_manager.dto.request.userReq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAvatarUpdateRequest {
    private String userCode;
} 