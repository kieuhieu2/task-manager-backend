package com.vnua.task_manager.dto.response.groupRes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberRes {
    private String userCode;
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isLeader;
} 