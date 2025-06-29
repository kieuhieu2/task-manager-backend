package com.vnua.task_manager.entity;

import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
public class UserTaskStatus {

    @EmbeddedId
    private UserTaskId id = new UserTaskId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Enumerated(EnumType.STRING)
    private TaskState state = TaskState.TODO;

    private Integer percentDone = 0;

    private Integer positionInColumn;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
}



