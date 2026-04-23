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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User testUser;
    private Task testTask;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .name("Test User")
                .password("hashed_pwd")
                .build();

        testTask = Task.builder()
                .id(100L)
                .title("Test Mock Task")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .user(testUser)
                .build();

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Mock Task");
        taskRequest.setStatus(TaskStatus.TODO);
        taskRequest.setPriority(TaskPriority.HIGH);
    }

    @Test
    void createTask_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(modelMapper.map(taskRequest, Task.class)).thenReturn(testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse mockResponse = new TaskResponse();
        mockResponse.setId(100L);
        mockResponse.setTitle("Test Mock Task");
        when(modelMapper.map(testTask, TaskResponse.class)).thenReturn(mockResponse);

        TaskResponse response = taskService.createTask(taskRequest, testUser.getEmail());

        assertNotNull(response);
        assertEquals(100L, response.getId());
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void getTaskById_Success() {
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));

        TaskResponse mockResponse = new TaskResponse();
        mockResponse.setId(100L);
        when(modelMapper.map(testTask, TaskResponse.class)).thenReturn(mockResponse);

        TaskResponse response = taskService.getTaskById(100L, testUser.getEmail());

        assertNotNull(response);
        assertEquals(100L, response.getId());
    }

    @Test
    void getTaskById_Unauthorized() {
        User otherUser = User.builder().id(2L).email("other@gmail.com").build();
        testTask.setUser(otherUser);

        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));

        assertThrows(UnauthorizedException.class, () -> taskService.getTaskById(100L, testUser.getEmail()));
    }

    @Test
    void getTaskById_NotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(999L, testUser.getEmail()));
    }
}
