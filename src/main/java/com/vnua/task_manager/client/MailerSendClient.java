package com.vnua.task_manager.client;

import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.exceptions.MailerSendException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MailerSendClient {
    
    @Value("${notification.email.mailersend-token}")
    private String apiToken;
    
    @Value("${notification.email.from-email}")
    private String fromEmail;
    
    @Value("${notification.email.from-name}")
    private String fromName;
    
    public MailerSendResponse sendEmail(String recipientEmail, String recipientName, String subject, String htmlContent, String plainContent) throws MailerSendException {
        Email email = new Email();
        
        email.setFrom(fromName, fromEmail);
        
        email.addRecipient(recipientName, recipientEmail);
        
        email.setSubject(subject);
        
        if (plainContent != null && !plainContent.isEmpty()) {
            email.setPlain(plainContent);
        }
        
        if (htmlContent != null && !htmlContent.isEmpty()) {
            email.setHtml(htmlContent);
        }
        
        MailerSend mailerSend = new MailerSend();
        mailerSend.setToken(apiToken);
        
        try {
            MailerSendResponse response = mailerSend.emails().send(email);
            log.info("Email sent successfully, message ID: {}", response.messageId);
            return response;
        } catch (MailerSendException e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw e;
        }
    }
} 