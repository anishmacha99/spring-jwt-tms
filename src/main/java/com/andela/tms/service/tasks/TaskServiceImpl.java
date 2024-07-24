package com.andela.tms.service.tasks;

import com.andela.tms.models.dto.Pagination;
import com.andela.tms.models.dto.TaskFilters;
import com.andela.tms.payload.request.TaskRequest;
import com.andela.tms.models.entity.Task;
import com.andela.tms.models.entity.TaskHistory;
import com.andela.tms.repository.TaskHistoryRepository;
import com.andela.tms.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    public Task saveTask(Task task) {
        Task savedTask = taskRepository.save(task);

        // Log the change to history
        TaskHistory taskHistory = new TaskHistory(savedTask, "INSERT");
        taskHistoryRepository.save(taskHistory);

        return savedTask;
    }

    @Transactional
    public Task updateTask(Task task, TaskRequest taskRequest) {

            if (taskRequest.getTitle() != null) {
                task.setTitle(taskRequest.getTitle());
            }
            if (taskRequest.getDescription() != null) {
                task.setDescription(taskRequest.getDescription());
            }
            if (taskRequest.getStatus() != null) {
                task.setStatus(taskRequest.getStatus());
            }
            if (taskRequest.getPriority() != null) {
                task.setPriority(taskRequest.getPriority());
            }
            if (taskRequest.getDueDate() != null) {
                task.setDueDate(new Date(taskRequest.getDueDate()));
            }


            // Log the change to history
            Task updatedTask = taskRepository.save(task);
            TaskHistory taskHistory = new TaskHistory(updatedTask, "UPDATE");
            taskHistoryRepository.save(taskHistory);
            return updatedTask;

    }
    public Task updateTask(Task task) {
        Task updatedTask = taskRepository.save(task);

        // Log the change to history
        TaskHistory taskHistory = new TaskHistory(updatedTask, "UPDATE");
        taskHistoryRepository.save(taskHistory);

        return updatedTask;
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            // Log the change to history before deletion
            TaskHistory taskHistory = new TaskHistory(task, "DELETE");
            taskHistoryRepository.save(taskHistory);
            task.setIsActive(false);
            taskRepository.save(task);
        }
    }

    @Override
    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Page<Task> searchAndFilterTasks(String searchTerm, TaskFilters taskFilters, Pagination pagination) {
        Specification<Task> spec = Specification.where(null);
        //TODO: isActive task has to be handled as required.
        // Add search term criteria if provided
        if (searchTerm != null && !searchTerm.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("title")), "%" + searchTerm.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + searchTerm.toLowerCase() + "%")
                    )
            );
        }

        // Add created by criteria if provided
        if(taskFilters.getCreatedByIds() != null && !taskFilters.getCreatedByIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("createdBy").get("id").in(taskFilters.getCreatedByIds())
            );
        }

        // Add status criteria if provided
        if (taskFilters.getStatus() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), taskFilters.getStatus())
            );
        }

        // Add priority criteria if provided
        if (taskFilters.getPriority() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("priority"), taskFilters.getPriority())
            );
        }

        // Add due date range criteria if provided
        if (taskFilters.getDueDateFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("dueDate"), taskFilters.getDueDateFrom())
            );
        }

        if (taskFilters.getDueDateTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("dueDate"), taskFilters.getDueDateTo())
            );
        }

        // Create pageable object for pagination
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize());

        // Perform the query using Spring Data JPA repository
        return taskRepository.findAll(spec, pageable);
    }
}
