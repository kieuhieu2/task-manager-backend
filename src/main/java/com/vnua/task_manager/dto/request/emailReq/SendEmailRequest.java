package com.vnua.task_manager.dto.request.emailReq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailRequest {
    private Recipient to;
    private String subject;
    private String htmlContent;
} 