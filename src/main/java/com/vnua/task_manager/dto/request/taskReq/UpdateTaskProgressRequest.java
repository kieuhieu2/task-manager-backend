package com.vnua.task_manager.dto.request.taskReq;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskProgressRequest {
    
    @NotNull(message = "User ID cannot be null")
    private String userId;
    
    @NotNull(message = "Percent done cannot be null")
    @Min(value = 0, message = "Percent done must be between 0 and 100")
    @Max(value = 100, message = "Percent done must be between 0 and 100")
    private Integer percentDone;
} 