package com.vnua.task_manager.entity;

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
@Table(name = "`group`")
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

    String faculty;
    String department;

}
