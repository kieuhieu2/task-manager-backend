package com.vnua.task_manager.service.eventListeners;

import com.mailersend.sdk.exceptions.MailerSendException;
import com.vnua.task_manager.client.MailerSendClient;
import com.vnua.task_manager.event.PasswordResetEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PasswordResetListener {
    
    MailerSendClient mailerSendClient;

    @Async
    @EventListener
    public void handlePasswordResetEvent(PasswordResetEvent event) {
        log.info("Received password reset request for email: {}", event.getEmail());
        
        // Build email content
        String emailSubject = "Task Manager - Password Reset Request";
        String htmlContent = String.format(
                "<html><body>" +
                "<h1>Password Reset Request</h1>" +
                "<p>Hello %s,</p>" +
                "<p>You have requested to reset your password. Please use the following OTP code:</p>" +
                "<h2 style='background-color: #f0f0f0; padding: 10px; text-align: center;'>%s</h2>" +
                "<p>This code will expire soon, so please use it immediately.</p>" +
                "<p>If you did not request this password reset, please ignore this email or contact support.</p>" +
                "<p>Best regards,<br>Task Manager Team</p>" +
                "</body></html>",
                event.getUsername(),
                event.getOtpCode()
        );

        try {
            mailerSendClient.sendEmail(
                event.getEmail(), 
                event.getUsername(), 
                emailSubject, 
                htmlContent, 
                null
            );
            log.info("Password reset email sent successfully to: {}", event.getEmail());
        } catch (MailerSendException e) {
            log.error("Failed to send password reset email to: {}", event.getEmail(), e);
        }
    }
} 