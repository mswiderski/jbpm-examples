package org.jbpm.spring.web;

import java.util.Collection;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.VariableDesc;
import org.kie.internal.query.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/processinstance")
public class ProcessInstanceController {
	
	@Autowired
	private RuntimeDataService runtimeDataService;
	
	@Autowired
	private ProcessService processService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getProcessInstances(ModelMap model) {
		
		Collection<ProcessInstanceDesc> processInstances = runtimeDataService.getProcessInstances(new QueryContext(0, 100, "status", true));

		model.addAttribute("processInstances", processInstances);
		return "processInstanceList";
 
	}
	
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public String getProcessInstance(@RequestParam String id, ModelMap model) {
		long processInstanceId = Long.parseLong(id);
		ProcessInstanceDesc processInstance = runtimeDataService.getProcessInstanceById(processInstanceId);
		
		Collection<VariableDesc> variables = runtimeDataService.getVariablesCurrentState(processInstanceId);

		model.addAttribute("processInstance", processInstance);
		model.addAttribute("variables", variables);
		return "processInstance";
 
	}
	
	@RequestMapping(value = "/abort", method = RequestMethod.POST)
	public String abortProcessInstance(@RequestParam String id, ModelMap model) {
		
		processService.abortProcessInstance(Long.parseLong(id));
		
		model.addAttribute("message", "Instance (" + id + ") aborted successfully");
		return "processInstanceStatus";
	}
	
	@RequestMapping(value = "/signal", method = RequestMethod.POST)
	public String signalProcessInstance(@RequestParam String id, @RequestParam String signal,
			@RequestParam String data, ModelMap model) {
		
		processService.signalProcessInstance(Long.parseLong(id), signal, data);
		
		model.addAttribute("message", "Signal sent to instance (" + id + ") successfully");
		return "processInstanceStatus";
	}
}
