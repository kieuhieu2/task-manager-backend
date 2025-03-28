package com.vnua.task_manager.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name ="user_id"),
            inverseJoinColumns = @JoinColumn(name = "name")
    )
    Set<Role> roles = new HashSet<>();

    @ManyToMany
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
    private Set<Task> tasksAssigned = new HashSet<>();

    Integer taskProgress;
    Date createdAt;
    Date updatedAt;
    Boolean wasDeleted = false;
    String pathOfUserFolder;
}
