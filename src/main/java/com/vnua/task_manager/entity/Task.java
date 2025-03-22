package com.vnua.task_manager.entity;

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
    Integer id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User whoCreated;

    @OneToMany(mappedBy = "whoCreatedCmt", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments;

    String title;
    String description;
    String filePath;

    Date createdAt;
    Date updatedAt;
    Boolean wasDeleted = false;

}
