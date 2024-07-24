package com.andela.tms.models.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;


@Data
@MappedSuperclass
public class BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false)
  User createdBy;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_date")
  Date createdDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "last_modified_by")
  User lastModifiedBy;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_modified_date")
  Date lastModifiedDate;


  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (User) authentication.getPrincipal();
  }

  @PrePersist
  protected void onCreate() {
    createdDate = new Date();
    lastModifiedDate = createdDate;
    createdBy = getCurrentUser();
    lastModifiedBy = createdBy;
  }

  @PreUpdate
  protected void onUpdate() {
    lastModifiedDate = new Date();
    lastModifiedBy = getCurrentUser();
  }
}