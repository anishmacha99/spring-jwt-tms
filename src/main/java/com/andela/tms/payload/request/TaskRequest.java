package com.andela.tms.payload.request;

import com.andela.tms.models.entity.Task;
import com.andela.tms.models.enums.TaskPriority;
import com.andela.tms.models.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
@AllArgsConstructor
public class TaskRequest {

    public interface Create {}


    @NotBlank(groups = {Create.class})
    private String title;

    @NotBlank(groups = {Create.class})
    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private Instant createdDate;

    private Long createdById;

    @NotNull(groups = {Create.class})
    @FutureOrPresent(groups = {Create.class},message = "Due date must be in the future or present")
    private Instant dueDate;

    // Constructors, getters, and setters

    // Constructors
    public TaskRequest() {
    }


    public Task convertToEntity() {
        Task task = new Task();
        task.setTitle(this.getTitle());
        task.setDescription(this.getDescription());
        task.setStatus(this.getStatus());
        task.setPriority(this.getPriority());
        task.setDueDate(new Date(this.dueDate.toEpochMilli()));
        return task;
    }

    public TaskRequest(Task task) {
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.priority = task.getPriority();
        this.dueDate = task.getDueDate().toInstant();
        this.createdById = task.getCreatedBy().getId();
        this.createdDate = task.getCreatedDate().toInstant();
    }

    public Long getDueDate() {
        return dueDate != null ? dueDate.toEpochMilli(): null;
    }

    public Long getCreatedDate() {
        return createdDate != null ? createdDate.toEpochMilli(): null;
    }

    public TaskStatus getStatus() {
        return status != null ? status: TaskStatus.TODO;
    }

    public TaskPriority getPriority() {
        return priority != null ? priority: TaskPriority.LOW;
    }

}
