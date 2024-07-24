
package com.andela.tms.controller;

import com.andela.tms.exception.GlobalExceptionHandler;
import com.andela.tms.models.dto.Pagination;
import com.andela.tms.models.dto.TaskFilters;
import com.andela.tms.payload.request.TaskRequest;
import com.andela.tms.payload.response.TaskResponse;
import com.andela.tms.models.entity.Task;
import com.andela.tms.models.entity.User;
import com.andela.tms.models.enums.TaskPriority;
import com.andela.tms.models.enums.TaskStatus;
import com.andela.tms.payload.response.MessageResponse;
import com.andela.tms.security.services.AuthUserService;
import com.andela.tms.service.tasks.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    private final AuthUserService authUserService;

    @Autowired
    public TaskController(TaskService taskService, AuthUserService authUserService) {
        this.taskService = taskService;
        this.authUserService = authUserService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        Optional<Task> taskOptional = taskService.getTaskById(id);

        if (taskOptional.isPresent()) {
            TaskResponse taskResponse = new TaskResponse(taskOptional.get());
            return ResponseEntity.ok().body(taskResponse);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("field", "id");
            errorResponse.put("message", String.format("Error: Task: %d not found.", id));
            return GlobalExceptionHandler.getValidationErrorResponseEntity(
                    HttpStatus.NOT_FOUND, List.of(errorResponse));
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createTask(@Validated(TaskRequest.Create.class) @RequestBody TaskRequest taskRequest) {
        User user = authUserService.getAuthUser();
        Task task = taskRequest.convertToEntity();
        task.setCreatedBy(user);
        Task createdTask = taskService.saveTask(task);
        Map<String, Long> response = new HashMap<>();
        response.put("taskId", createdTask.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {

        Optional<Task> taskOptional = taskService.getTaskById(id);

        if (taskOptional.isPresent()) {
            taskService.updateTask(taskOptional.get(), taskRequest);
            return ResponseEntity.ok().body(new MessageResponse("Task updated successfully."));
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("field", "id");
            errorResponse.put("message", String.format("Error: Task: %d not found.", id));
            return GlobalExceptionHandler.getValidationErrorResponseEntity(
                    HttpStatus.NOT_FOUND, List.of(errorResponse));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<TaskResponse> searchAndFilterTasks(@RequestParam(required = false) String searchTerm,
                                                  @RequestParam(required = false) TaskStatus status,
                                                  @RequestParam(required = false) TaskPriority priority,
                                                  @RequestParam(required = false) Long dueDateFrom,
                                                  @RequestParam(required = false) Long dueDateTo,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) throws ParseException {

        User user = authUserService.getAuthUser();
        var taskFiltersBuilder = TaskFilters.builder().priority(priority).status(status);

        if(user.isUserAdmin()){
            taskFiltersBuilder.createdByIds(null);
        } else {
            taskFiltersBuilder.createdByIds(List.of(user.getId()));
        }
        if (dueDateFrom != null ) {
            taskFiltersBuilder.dueDateFrom(new Date(dueDateFrom));
        }
        if (dueDateTo != null ) {
            taskFiltersBuilder.dueDateTo(new Date(dueDateTo));
        }
        Page<Task> tasks = taskService.searchAndFilterTasks(searchTerm,
                                                            taskFiltersBuilder.build(),
                                                            new Pagination(page, size));
        return tasks.map(TaskResponse::new);
    }
}

