package com.praneeth.taskmanager.service.impl;

import com.praneeth.taskmanager.dto.request.TaskRequest;
import com.praneeth.taskmanager.dto.response.TaskResponse;
import com.praneeth.taskmanager.exception.ResourceNotFoundException;
import com.praneeth.taskmanager.exception.UnauthorizedException;
import com.praneeth.taskmanager.model.Task;
import com.praneeth.taskmanager.model.TaskPriority;
import com.praneeth.taskmanager.model.TaskStatus;
import com.praneeth.taskmanager.model.User;
import com.praneeth.taskmanager.repository.TaskRepository;
import com.praneeth.taskmanager.repository.UserRepository;
import com.praneeth.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TaskService handling task-related business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public TaskResponse createTask(TaskRequest request, String userEmail) {
        log.info("Creating task for user: {}", userEmail);
        User user = getUserByEmail(userEmail);

        Task task = modelMapper.map(request, Task.class);
        task.setUser(user);

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with id: {}", savedTask.getId());
        return modelMapper.map(savedTask, TaskResponse.class);
    }

    @Override
    public List<TaskResponse> getTasks(String status, String priority, String sortBy, String userEmail) {
        log.info("Fetching tasks for user: {} with parameters: status={}, priority={}, sortBy={}", 
                userEmail, status, priority, sortBy);
        
        User user = getUserByEmail(userEmail);
        
        TaskStatus parsedStatus = parseStatus(status);
        TaskPriority parsedPriority = parsePriority(priority);
        Sort sort = determineSort(sortBy);

        List<Task> tasks;
        if (parsedStatus != null && parsedPriority != null) {
            tasks = taskRepository.findByUserIdAndStatusAndPriority(user.getId(), parsedStatus, parsedPriority, sort);
        } else if (parsedStatus != null) {
            tasks = taskRepository.findByUserIdAndStatus(user.getId(), parsedStatus, sort);
        } else if (parsedPriority != null) {
            tasks = taskRepository.findByUserIdAndPriority(user.getId(), parsedPriority, sort);
        } else {
            tasks = taskRepository.findByUserId(user.getId(), sort);
        }

        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse getTaskById(Long id, String userEmail) {
        log.info("Fetching task id: {} for user: {}", id, userEmail);
        Task task = getTaskAndVerifyOwnership(id, userEmail);
        return modelMapper.map(task, TaskResponse.class);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskRequest request, String userEmail) {
        log.info("Updating task id: {} for user: {}", id, userEmail);
        Task existingTask = getTaskAndVerifyOwnership(id, userEmail);

        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setStatus(request.getStatus());
        existingTask.setPriority(request.getPriority());
        existingTask.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(existingTask);
        log.info("Task id: {} updated successfully.", id);
        return modelMapper.map(updatedTask, TaskResponse.class);
    }

    @Override
    public void deleteTask(Long id, String userEmail) {
        log.info("Deleting task id: {} for user: {}", id, userEmail);
        Task task = getTaskAndVerifyOwnership(id, userEmail);
        taskRepository.delete(task);
        log.info("Task id: {} deleted successfully.", id);
    }

    private User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
    }

    private Task getTaskAndVerifyOwnership(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getUser().getEmail().equals(userEmail)) {
            log.error("Unauthorized access. User {} attempted to access task {}", userEmail, id);
            throw new UnauthorizedException("You are not authorized to access this task.");
        }
        return task;
    }

    private TaskStatus parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) return null;
        try {
            return TaskStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Or throw a specific exception if invalid format is strict
        }
    }

    private TaskPriority parsePriority(String priority) {
        if (priority == null || priority.trim().isEmpty()) return null;
        try {
            return TaskPriority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Sort determineSort(String sortBy) {
        if ("priority".equalsIgnoreCase(sortBy)) {
            return Sort.by(Sort.Direction.DESC, "priority"); // Assuming higher order is more important, or map ordinal correctly
        } else if ("dueDate".equalsIgnoreCase(sortBy)) {
            return Sort.by(Sort.Direction.ASC, "dueDate");
        }
        // Default sort by created_at descending
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}
