package com.andela.tms.controller;

import com.andela.tms.BaseTest;
import com.andela.tms.models.entity.Task;
import com.andela.tms.security.services.AuthUserService;
import com.andela.tms.service.tasks.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private AuthUserService authUserService;


    @BeforeEach
    void setUp() {
        when(authUserService.getAuthUser()).thenReturn(createMockUser());
    }


    @Test
    public void createTask_ShouldReturnCreatedTask() throws Exception {
        Task mockTask = createMockTask();
        when(taskService.saveTask(any(Task.class))).thenReturn(mockTask);
        long dueDateMillis = Instant.now().plus(Duration.ofDays(1)).getEpochSecond();
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "title": "Test Task",
                                    "description": "This is a test task",
                                    "status": "TODO",
                                    "priority": "LOW",
                                    "dueDate": "%d"
                                }
                                """, dueDateMillis)
                        )
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.taskId").value(mockTask.getId()));

        verify(taskService, times(1)).saveTask(any(Task.class));
    }

    @Test
    public void createTask_ShouldReturnBadRequestOnNoTitle() throws Exception {
        Task mockTask = createMockTask();
        when(taskService.saveTask(any(Task.class))).thenReturn(mockTask);
        long dueDateMillis = Instant.now().plus(Duration.ofDays(1)).toEpochMilli();
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "description": "This is a test task",
                                    "status": "TODO",
                                    "priority": "LOW",
                                    "dueDate": "%d"
                                }
                                """, dueDateMillis)
                        )
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].field").value("title"))
                .andExpect(jsonPath("$.errors[0].message").value("must not be blank"));
        ;

        verify(taskService, times(0)).saveTask(any(Task.class));
    }

    @Test
    public void createTask_ShouldReturnBadRequestOnPastDueDate() throws Exception {
        Task mockTask = createMockTask();
        when(taskService.saveTask(any(Task.class))).thenReturn(mockTask);
        long dueDateMillis = Instant.now().minus(Duration.ofDays(1)).getEpochSecond();
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "title": "Test Task",
                                    "description": "This is a test task",
                                    "status": "TODO",
                                    "priority": "LOW",
                                    "dueDate": "%d"
                                }
                                """, dueDateMillis)
                        )
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].field").value("dueDate"))
                .andExpect(jsonPath("$.errors[0].message").value("Due date must be in the future or present"));
        ;

        verify(taskService, times(0)).saveTask(any(Task.class));
    }

}