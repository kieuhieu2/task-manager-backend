package com.vnua.task_manager.dto.request.authReq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetWithOtpRequest {
    private String userCode;
    private String otpCode;
    private String newPassword;
} 