package org.jbpm.spring.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.internal.query.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/processdef")
public class ProcessDefController {
	
	@Autowired
	private RuntimeDataService runtimeDataService;
	
	@Autowired
	private ProcessService processService;
	
	@Autowired
	private DefinitionService definitionService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getProcessDef(ModelMap model) {
		
		Collection<ProcessDefinition> processDefinitions = runtimeDataService.getProcesses(new QueryContext(0, 100));

		
		model.addAttribute("processDefinitions", processDefinitions);
		return "processDefList";
 
	}
	
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public String getProcessDefinition(@RequestParam String deployment, @RequestParam String id, ModelMap model) {
		
		ProcessDefinition definition = runtimeDataService.getProcessesByDeploymentIdProcessId(deployment, id);
		
		model.addAttribute("processDefinition", definition);
		return "processDef";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newProcessInstance(@RequestParam String deploymentId, @RequestParam String processId,
			@RequestParam Map<String,String> allRequestParams, ModelMap model) {
		
		long processInstanceId = processService.startProcess(deploymentId, processId, new HashMap<String, Object>(allRequestParams));
		model.addAttribute("processInstanceId", processInstanceId);
		return "newProcessInstance";
 
	}
}
