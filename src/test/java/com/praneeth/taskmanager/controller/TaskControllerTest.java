package com.praneeth.taskmanager.controller;

import com.praneeth.taskmanager.dto.request.TaskRequest;
import com.praneeth.taskmanager.dto.response.TaskResponse;
import com.praneeth.taskmanager.model.TaskPriority;
import com.praneeth.taskmanager.model.TaskStatus;
import com.praneeth.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Test context for Task Controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private TaskResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new TaskResponse();
        mockResponse.setId(1L);
        mockResponse.setTitle("Test Task");
        mockResponse.setStatus(TaskStatus.TODO);
        mockResponse.setPriority(TaskPriority.HIGH);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void createTask_ValidInput_Returns201() throws Exception {
        when(taskService.createTask(any(TaskRequest.class), eq("test@gmail.com"))).thenReturn(mockResponse);

        String jsonRequest = """
                {
                    "title": "Test Task",
                    "status": "TODO",
                    "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void createTask_InvalidInput_Returns400() throws Exception {
        // Missing title makes it invalid based on @NotBlank
        String jsonRequest = """
                {
                    "status": "TODO",
                    "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
