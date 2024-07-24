package com.andela.tms.service.notifications;

import com.andela.tms.models.entity.Task;
import com.andela.tms.repository.TaskRepository;
import com.andela.tms.service.tasks.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TaskNotificationScheduler {

    @Autowired
    private TaskRepository taskRepository;

    private List<Task> getTasksForNotification() {
        // TODO : This is simple logic to get tasks reached dueDate,
        //  implement logic to get tasks pending for notification and create new entry in
        //  Notification table and use queues to all necessory notifications.
        Date now = new Date();
        Date threshold = new Date(now.getTime() + (60 * 1000)); // Adding 1 minute to current time
        return taskRepository.findByDueDateBetween(now, threshold);
        // Get all tasks that are nearing their due date
    }
    // Cron expression for running every day at 8 AM
    @Scheduled(cron = "0 * * * * *")
    public void checkUpcomingTaskDeadlines() {
        //TODO: Handle getting lock when multiple container instances are running.
        System.out.println("Checking upcoming task deadlines...");

        List<Task> tasksList = getTasksForNotification();
        for (Task task : tasksList) {
            System.out.println("Task with id: " + task.getId() + " is due soon.");
        }
    }
}
