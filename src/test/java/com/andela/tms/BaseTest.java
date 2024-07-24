package com.andela.tms;

import com.andela.tms.models.entity.Task;
import com.andela.tms.models.entity.User;

public class BaseTest {

    protected User createMockUser() {
        User user = new User("testUser", "password");
        user.setId(1L);
        return user;
    }

    protected Task createMockTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        return task;
    }
}
