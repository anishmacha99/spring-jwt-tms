package com.andela.tms.payload.response;

import com.andela.tms.models.entity.Task;
import com.andela.tms.models.enums.TaskPriority;
import com.andela.tms.models.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private Instant createdDate;

    private Long createdById;

    private Instant dueDate;

    private Boolean isActive;


    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.priority = task.getPriority();
        this.dueDate = task.getDueDate().toInstant();
        this.createdById = task.getCreatedBy().getId();
        this.createdDate = task.getCreatedDate().toInstant();
        this.isActive = task.getIsActive();
    }

    public Long getDueDate() {
        return dueDate != null ? dueDate.toEpochMilli(): null;
    }

    public Long getCreatedDate() {
        return createdDate != null ? createdDate.toEpochMilli(): null;
    }
    // Getters and setters
}
