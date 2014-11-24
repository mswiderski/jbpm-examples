package org.jbpm.spring.boot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
public class UserTaskController {

	@Autowired
	private RuntimeDataService runtimeDataService;
	
	@Autowired
	private UserTaskService userTaskService;
	
	@Autowired
	private DefinitionService definitionService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Collection<TaskSummary> getTasks() {		
	    String userId = getAuthUser();
	      
		Collection<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(userId, new QueryFilter(0, 100));

		return tasks;
 
	}
	
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public UserTaskInstanceDesc getTask(@RequestParam String id) {
		
		Long taskId = Long.parseLong(id);

		UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);

		return task;
 
	}
	
	@RequestMapping(value = "/complete", method = RequestMethod.POST)
	public String completeTask(@RequestParam String id, @RequestParam Map<String,String> allRequestParams) {		
		String userId = getAuthUser();
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		for (Entry<String, String> entry : allRequestParams.entrySet()) {
			Object value = entry.getValue();
			// just a simple type conversion
			// integer
			try {
				value = Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				// ignore
			}
			// boolean
			if (value.toString().equalsIgnoreCase("true") || value.toString().equalsIgnoreCase("false")) {
				value = Boolean.parseBoolean(value.toString());
			}
			data.put(entry.getKey(), value);
		}
		
		try {
			userTaskService.complete(Long.parseLong(id), userId, data);
			return "Task " + id + " completed successfully";
		} catch (Exception e) {
			return "Task " + id + " complete failed due to " + e.getMessage();
		}

 
	}
	
	@RequestMapping(value = "/claim", method = RequestMethod.POST)
	public String claimTask(@RequestParam String id) {
		String userId = getAuthUser();
		try {
			userTaskService.claim(Long.parseLong(id), userId);
			return "Task " + id + " claimed successfully";
		} catch (Exception e) {
			return "Task " + id + " claim failed due to " + e.getMessage();
		}
 
	}
	
	@RequestMapping(value = "/release", method = RequestMethod.POST)
	public String releaseTask(@RequestParam String id) {
		String userId = getAuthUser();
		try {
			userTaskService.release(Long.parseLong(id), userId);
			return "Task " + id + " released successfully";
		} catch (Exception e) {
			return "Task " + id + " release failed due to " + e.getMessage();
		}
 
	}
	
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public String startTask(@RequestParam String id) {
		String userId = getAuthUser();
		try {
			userTaskService.start(Long.parseLong(id), userId);
			return "Task " + id + " started successfully";
		} catch (Exception e) {
			return "Task " + id + " start failed due to " + e.getMessage();
		}
 
	}
	
	protected String getAuthUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String userId = auth.getName();
	    
	    return userId;
	}
}
