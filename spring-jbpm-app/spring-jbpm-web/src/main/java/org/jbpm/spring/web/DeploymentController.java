package org.jbpm.spring.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/deployment")
public class DeploymentController {
	
	@Autowired
	private DeploymentService deploymentService;
	
	@RequestMapping(value = "/deploy", method = RequestMethod.POST)
	public String deployUnit(@RequestParam String id, ModelMap model) {
		try {
			String[] gav = id.split(":");
			
			KModuleDeploymentUnit unit = new KModuleDeploymentUnit(gav[0], gav[1], gav[2]);
			
			deploymentService.deploy(unit);
			model.addAttribute("message", "deployed " + unit.getIdentifier() + " successfully");
		} catch (Exception e) {
			model.addAttribute("message", "deployment " + id + " successfully due to " + e.getMessage());
		}
		return "deployment";
 
	}
	
	@RequestMapping(value = "/undeploy", method = RequestMethod.POST)
	public String undeployUnit(@RequestParam String id, ModelMap model) {
		try {
			String[] gav = id.split(":");
			
			KModuleDeploymentUnit unit = new KModuleDeploymentUnit(gav[0], gav[1], gav[2]);
			
			deploymentService.undeploy(unit);
			model.addAttribute("message", "deployed " + unit.getIdentifier() + " successfully");
		} catch (Exception e) {
			model.addAttribute("message", "deployment " + id + " successfully due to " + e.getMessage());
		}
		return "deployment";
 
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public String getDeploymentUnit(@RequestParam String id, ModelMap model) {
		DeployedUnit unit = deploymentService.getDeployedUnit(id);
		model.addAttribute("unit", unit.getDeploymentUnit().getIdentifier());
		model.addAttribute("strategy", unit.getDeploymentUnit().getStrategy());
		model.addAttribute("classes", unit.getDeployedClasses());
		model.addAttribute("assets", unit.getDeployedAssets());
		return "deploymentUnit";
 
	}
 
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getDeployments(ModelMap model) {
		
		Collection<DeployedUnit> deployedUnits = deploymentService.getDeployedUnits();
 
		List<String> units = new ArrayList<String>();
		
		for (DeployedUnit unit : deployedUnits) {
			units.add(unit.getDeploymentUnit().getIdentifier());
		}
		
		model.addAttribute("deployedUnits", units);
		return "deploymentList";
 
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newDeploymentForm(ModelMap model) {

		return "newDeploymentUnit";
 
	}
}
