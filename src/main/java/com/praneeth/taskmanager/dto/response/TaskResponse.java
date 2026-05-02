package com.praneeth.taskmanager.dto.response;

import com.praneeth.taskmanager.model.TaskPriority;
import com.praneeth.taskmanager.model.TaskStatus;
import lombok.Data; 

import java.time.LocalDate; 
import java.time.LocalDateTime;
 
/**
 * Display DTO for viewing Tasks without exposing the entity structure.
 */
@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
