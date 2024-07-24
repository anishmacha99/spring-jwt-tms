package com.andela.tms.models.entity;

import com.andela.tms.models.enums.TaskPriority;
import com.andela.tms.models.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;


@Entity
@Table(name = "tasks")
@Data
public class Task extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false)
  @NotBlank
  private String title;

  @Column(length = 1000)
  @NotBlank
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private TaskStatus status = TaskStatus.TODO;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private TaskPriority priority = TaskPriority.LOW;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "due_date")
  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
  private Date dueDate;

  @Column(length = 20)
  private Boolean isActive = true;



  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_to")
  private User assignedTo;


  // Constructors, getters, and setters

  public Task() {
  }

  public Task(String title, String description, TaskStatus status, TaskPriority priority, Date dueDate) {
    this.title = title;
    this.description = description;
    this.status = status;
    this.priority = priority;
    this.dueDate = dueDate;
  }



  // Getters and setters
}