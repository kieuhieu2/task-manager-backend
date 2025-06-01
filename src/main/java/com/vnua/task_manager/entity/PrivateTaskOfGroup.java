package com.vnua.task_manager.entity;

import com.vnua.task_manager.entity.enumsOfEntity.TaskState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateTaskOfGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long privateTaskId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User whoCreated;

    @Enumerated(EnumType.STRING)
    TaskState state = TaskState.TODO;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    User assignee;

    @OneToMany(mappedBy = "privateTask")
    List<Comment> commentsOfTask = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "private_task_assignees",
            joinColumns = @JoinColumn(name = "private_task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    List<User> assigneesUser = new ArrayList<>();

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
