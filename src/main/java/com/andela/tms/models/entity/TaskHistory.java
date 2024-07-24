package com.andela.tms.models.entity;

import com.andela.tms.models.enums.TaskPriority;
import com.andela.tms.models.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "tasks_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistory extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long taskId; // To store the ID of the task being audited

  private String title;

  @Column(length = 1000)
  private String description;

  private TaskStatus status;

  private TaskPriority priority;

  @Temporal(TemporalType.TIMESTAMP)
  private Date dueDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_to")
  private User assignedTo;

  private String actionType; // e.g., INSERT, UPDATE, DELETE


  public TaskHistory(Task task, String actionType) {
    this.taskId = task.getId();
    this.title = task.getTitle();
    this.description = task.getDescription();
    this.status = task.getStatus();
    this.priority = task.getPriority();
    this.dueDate = task.getDueDate();
    this.createdBy = task.getCreatedBy();
    this.assignedTo = task.getAssignedTo();
    this.createdDate = task.getCreatedDate();
    this.lastModifiedDate = task.getLastModifiedDate();
    this.lastModifiedBy =  task.lastModifiedBy;
    this.actionType = actionType;
  }
}