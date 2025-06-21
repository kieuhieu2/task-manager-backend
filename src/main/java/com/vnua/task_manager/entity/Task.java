package com.vnua.task_manager.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import com.vnua.task_manager.entity.enumsOfEntity.TaskType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer taskId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User whoCreated;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> commentsOfTask = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    TaskState state = TaskState.TODO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TaskType taskType;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    User assignee;

    String title;
    String description;
    String filePathOfTask;
    @Builder.Default
    Integer percentDone = 0;

    Date createdAt;
    Date updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate deadline;
    @Builder.Default
    Boolean wasDeleted = false;

    @PrePersist
    @PreUpdate
    private void validateAssignee() {
        if (this.state == TaskState.IN_PROGRESS && this.assignee == null) {
            throw new IllegalStateException("Task ở trạng thái 'IN_PROGRESS' cần có một assignee (người thực hiện)!");
        }
    }
}
