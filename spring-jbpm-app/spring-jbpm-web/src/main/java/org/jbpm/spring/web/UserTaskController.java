package org.jbpm.spring.web;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/task")
public class UserTaskController {

	@Autowired
	private RuntimeDataService runtimeDataService;
	
	@Autowired
	private UserTaskService userTaskService;
	
	@Autowired
	private DefinitionService definitionService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getTasks(ModelMap model) {		
	    String userId = getAuthUser();
	      
		Collection<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(userId, new QueryFilter(0, 100));

		model.addAttribute("tasks", tasks);
		return "taskList";
 
	}
	
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public String getTask(@RequestParam String id, ModelMap model) {
		
		Long taskId = Long.parseLong(id);

		UserTaskInstanceDesc task = runtimeDataService.getTaskById(taskId);
		
		Map<String, String> inputs = definitionService.getTaskInputMappings(task.getDeploymentId(), task.getProcessId(), task.getName());
		Map<String, String> outputs = definitionService.getTaskOutputMappings(task.getDeploymentId(), task.getProcessId(), task.getName());
		
		Map<String, Object> inputValues = userTaskService.getTaskInputContentByTaskId(taskId);
		Map<String, Object> outputValues = userTaskService.getTaskOutputContentByTaskId(taskId);

		model.addAttribute("task", task);
		model.addAttribute("inputs", inputs);
		model.addAttribute("outputs", outputs);
		model.addAttribute("inputValues", inputValues);
		model.addAttribute("outputValues", outputValues);
		return "task";
 
	}
	
	@RequestMapping(value = "/complete", method = RequestMethod.POST)
	public String completeTask(@RequestParam String id, @RequestParam Map<String,String> allRequestParams, ModelMap model) {
		model.addAttribute("taskId", id);
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
			model.addAttribute("message", "Task " + id + " completed successfully");
		} catch (Exception e) {
			model.addAttribute("message", "Task " + id + " complete failed due to " + e.getMessage());
		}
		return "taskMessage";
 
	}
	
	@RequestMapping(value = "/claim", method = RequestMethod.POST)
	public String claimTask(@RequestParam String id, ModelMap model) {
		model.addAttribute("taskId", id);
		String userId = getAuthUser();
		try {
			userTaskService.claim(Long.parseLong(id), userId);
			model.addAttribute("message", "Task " + id + " claimed successfully");
		} catch (Exception e) {
			model.addAttribute("message", "Task " + id + " claim failed due to " + e.getMessage());
		}
		return "taskMessage";
 
	}
	
	@RequestMapping(value = "/release", method = RequestMethod.POST)
	public String releaseTask(@RequestParam String id, ModelMap model) {
		model.addAttribute("taskId", id);
		String userId = getAuthUser();
		try {
			userTaskService.release(Long.parseLong(id), userId);
			model.addAttribute("message", "Task " + id + " released successfully");
		} catch (Exception e) {
			model.addAttribute("message", "Task " + id + " release failed due to " + e.getMessage());
		}
		return "taskMessage";
 
	}
	
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public String startTask(@RequestParam String id, ModelMap model) {
		model.addAttribute("taskId", id);
		String userId = getAuthUser();
		try {
			userTaskService.start(Long.parseLong(id), userId);
			model.addAttribute("message", "Task " + id + " started successfully");
		} catch (Exception e) {
			model.addAttribute("message", "Task " + id + " start failed due to " + e.getMessage());
		}
		return "taskMessage";
 
	}
	
	protected String getAuthUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String userId = auth.getName();
	    
	    return userId;
	}
}
