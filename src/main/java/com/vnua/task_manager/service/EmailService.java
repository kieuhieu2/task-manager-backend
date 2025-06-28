package com.vnua.task_manager.service;

import com.vnua.task_manager.dto.request.emailReq.SendEmailRequest;
import com.vnua.task_manager.dto.response.emailRes.EmailResponse;

public interface EmailService {
    EmailResponse sendEmail(SendEmailRequest request);
} 