package com.vnua.task_manager.dto.request.taskReq;

import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskStateRequest {
    
    @NotNull(message = "Task ID cannot be null")
    private Integer taskId;
    
    @NotNull(message = "New state cannot be null")
    private TaskState newState;
    
    @NotNull(message = "Position in column cannot be null")
    private Integer positionInColumn;
} 