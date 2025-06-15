package com.vnua.task_manager.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`group`", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nameOfGroup")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer groupId;

    @ManyToMany(mappedBy = "groups")
    Set<User> members = new HashSet<>();

    @ManyToMany(mappedBy = "groupLeaders")
    Set<User> leadersOfGroup = new HashSet<>();

    @OneToMany(mappedBy = "group",cascade = CascadeType.ALL)
    Set<Task> tasks = new HashSet<>();

    @Column(name = "name_of_group", unique = true, nullable = false)
    String nameOfGroup;

    @Column(name = "description_of_group", columnDefinition = "TEXT")
    String descriptionOfGroup;

    String faculty;
    String department;
    String pathOfGroupFolder;
    Date createdAt;
    Date updatedAt;
    Boolean wasDeleted = false;
}
