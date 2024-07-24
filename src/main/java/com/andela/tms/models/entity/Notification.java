package com.andela.tms.models.entity;

import com.andela.tms.models.enums.NotificationType;
import jakarta.persistence.*;


public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private NotificationType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id")
  private Task task;
}