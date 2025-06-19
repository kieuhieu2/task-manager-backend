package com.vnua.task_manager.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageOfUserResponse<T> {
    List<T> content;
    int totalPages;
    long totalElements;
    int currentPage;
    int pageSize;
    boolean hasNext;
    boolean hasPrevious;
} 