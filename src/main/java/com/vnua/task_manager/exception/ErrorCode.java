package com.vnua.task_manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Invalid password", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    FOLDER_CREATION_FAILED(1009,"Folder create failed" ,HttpStatus.CONFLICT ),
    GROUP_NOT_FOUND(1010, "Group not found" , HttpStatus.NOT_FOUND ),
    FOLDER_RENAME_FAILED(1011,"Folder rename failed" ,HttpStatus.CONFLICT ),
    INVALID_REQUEST(1012, "Invalid request" , HttpStatus.BAD_REQUEST ),
    COMMENT_CREATION_FAILED(1013,"Comment creation failed" , HttpStatus.BAD_REQUEST ),
    COMMENT_NOT_FOUND(1014, "Comment not found", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND(1015,"Notification not found", HttpStatus.NOT_FOUND ),
    USER_NOT_FOUND(1016, "User not found", HttpStatus.NOT_FOUND ),
    INVALID_OTP(1017, "Invalid OTP code", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED(1018, "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
