package com.vnua.task_manager.dto.request.emailReq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    private Sender sender;
    private List<Recipient> to;
    private String subject;
    private String htmlContent;
} 