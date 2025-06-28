package com.vnua.task_manager.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PasswordResetEvent extends ApplicationEvent {
    private final String email;
    private final String otpCode;
    private final String username;

    public PasswordResetEvent(Object source, String email, String otpCode, String username) {
        super(source);
        this.email = email;
        this.otpCode = otpCode;
        this.username = username;
    }
} 