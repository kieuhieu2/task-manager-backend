package com.vnua.task_manager.entity;

import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @OneToMany(mappedBy = "whoCreatedCmt", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments;

    @Enumerated(EnumType.STRING)
    TaskState state = TaskState.TODO;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    User assignee;

    String title;
    String description;
    String filePathOfTask;
    Integer percentDone = 0;

    Date createdAt;
    Date updatedAt;
    Boolean wasDeleted = false;

    @PrePersist
    @PreUpdate
    private void validateAssignee() {
        if (this.state == TaskState.IN_PROGRESS && this.assignee == null) {
            throw new IllegalStateException("Task ở trạng thái 'IN_PROGRESS' cần có một assignee (người thực hiện)!");
        }
    }
}
