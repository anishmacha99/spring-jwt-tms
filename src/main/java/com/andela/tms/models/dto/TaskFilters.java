package com.andela.tms.models.dto;

import com.andela.tms.models.enums.TaskPriority;
import com.andela.tms.models.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TaskFilters {
    private List<Long> createdByIds;
    private TaskStatus status;
    private TaskPriority priority;
    private Date dueDateFrom;
    private Date dueDateTo;

}
