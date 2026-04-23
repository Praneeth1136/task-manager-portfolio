package com.praneeth.taskmanager.service;

import com.praneeth.taskmanager.dto.request.TaskRequest;
import com.praneeth.taskmanager.dto.response.TaskResponse;
import com.praneeth.taskmanager.model.TaskPriority;
import com.praneeth.taskmanager.model.TaskStatus;

import java.util.List;

/**
 * Interface defining task management logic.
 */
public interface TaskService {
    TaskResponse createTask(TaskRequest request, String userEmail);
    List<TaskResponse> getTasks(String status, String priority, String sortBy, String userEmail);
    TaskResponse getTaskById(Long id, String userEmail);
    TaskResponse updateTask(Long id, TaskRequest request, String userEmail);
    void deleteTask(Long id, String userEmail);
}
