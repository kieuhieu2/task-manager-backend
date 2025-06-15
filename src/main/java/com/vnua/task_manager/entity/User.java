package com.vnua.task_manager.entity;

import java.time.LocalDate;
import java.util.*;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String userId;

    @Column(unique = true, nullable = false)
    String code;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;

    String password;
    String firstName;
    LocalDate dob;
    String lastName;

    @ManyToMany(mappedBy = "assigneesUser")
    List<PrivateTaskOfGroup> assignedPrivateTasks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserNotification> userNotifications = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name ="user_id"),
            inverseJoinColumns = @JoinColumn(name = "name")
    )
    Set<Role> roles = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "users_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    Set<Group> groups = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name="users_group_leader",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    Set<Group> groupLeaders = new HashSet<>();

    @OneToMany(mappedBy = "whoCreated", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Task> createdTasks;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Task> tasksAssigned = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    List<Comment> comments = new ArrayList<>();

    Integer taskProgress;
    Date createdAt;
    Date updatedAt;
    @Builder.Default
    Boolean wasDeleted = false;
    String pathOfUserFolder;
}
