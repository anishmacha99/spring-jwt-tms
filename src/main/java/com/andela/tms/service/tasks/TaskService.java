package com.andela.tms.service.tasks;

import com.andela.tms.models.dto.Pagination;
import com.andela.tms.models.dto.TaskFilters;
import com.andela.tms.payload.request.TaskRequest;
import com.andela.tms.models.entity.Task;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task saveTask(Task task);

    Task updateTask(Task task, TaskRequest taskRequest);

    void deleteTask(Long taskId);

    Optional<Task> getTaskById(Long taskId);

    List<Task> getAllTasks();

    public Page<Task> searchAndFilterTasks(String searchTerm, TaskFilters taskFilters, Pagination pagination);

}
