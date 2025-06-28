package com.vnua.task_manager.service.implement;

import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.vnua.task_manager.client.MailerSendClient;
import com.vnua.task_manager.dto.request.emailReq.SendEmailRequest;
import com.vnua.task_manager.dto.response.emailRes.EmailResponse;
import com.vnua.task_manager.exception.AppException;
import com.vnua.task_manager.exception.ErrorCode;
import com.vnua.task_manager.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    MailerSendClient mailerSendClient;

    @Override
    public EmailResponse sendEmail(SendEmailRequest request) {
        try {
            // Extract recipient information
            String recipientEmail = request.getTo().getEmail();
            String recipientName = request.getTo().getName() != null ? request.getTo().getName() : "";
            
            // Send email using MailerSend
            MailerSendResponse response = mailerSendClient.sendEmail(
                    recipientEmail, 
                    recipientName,
                    request.getSubject(), 
                    request.getHtmlContent(),
                    null // We're only using HTML content
            );
            
            // Convert response to our internal EmailResponse
            return EmailResponse.builder()
                    .messageId(response.messageId)
                    .build();
            
        } catch (MailerSendException e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
} 