package org.jbpm.spring.boot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.internal.query.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/processdef")
public class ProcessDefController {
	
	@Autowired
	private RuntimeDataService runtimeDataService;
	
	@Autowired
	private ProcessService processService;
	
	@Autowired
	private DefinitionService definitionService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Collection<ProcessDefinition> getProcessDef() {
		
		Collection<ProcessDefinition> processDefinitions = runtimeDataService.getProcesses(new QueryContext(0, 100));

		return processDefinitions;
 
	}
	
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ProcessDefinition getProcessDefinition(@RequestParam String deployment, @RequestParam String id) {
		
		ProcessDefinition definition = runtimeDataService.getProcessesByDeploymentIdProcessId(deployment, id);
		
		return definition;
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public Long newProcessInstance(@RequestParam String deploymentId, @RequestParam String processId,
			@RequestParam Map<String,String> allRequestParams) {
		
		long processInstanceId = processService.startProcess(deploymentId, processId, new HashMap<String, Object>(allRequestParams));
		
		return processInstanceId;
 
	}
}
