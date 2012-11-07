package org.jbpm.spring.domain;

import java.util.List;

import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.local.LocalTaskService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HumanTaskEngine {

    private LocalTaskService taskService;
    
    public List<TaskSummary> getTaskForUser(String user) {
        return taskService.getTasksAssignedAsPotentialOwner(user, "en-UK");
    }
    
    public void startTask(long taskId, String user) {
        taskService.start(taskId, user);
    }
    
    public void completeTask(long taskId, String user) {
        taskService.complete(taskId, user, null);
    }

    public LocalTaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(LocalTaskService taskService) {
        this.taskService = taskService;
    }
}
