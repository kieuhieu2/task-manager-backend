package com.vnua.task_manager.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskCreatedEvent extends ApplicationEvent {
    private final String message;
    private final Integer groupId;

    public TaskCreatedEvent(Object source, String message, Integer groupId) {
        super(source);
        this.message = message;
        this.groupId = groupId;
    }

}
