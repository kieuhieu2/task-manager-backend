package com.vnua.task_manager.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Integer commentId;

    String comment_text;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    Task whoCreatedCmt;

    Date createdAt;
    Date updatedAt;
    Boolean wasDeleted = false;
}
