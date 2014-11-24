package org.jbpm.spring.boot;

import java.util.ArrayList;
import java.util.Collection;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deployment")
public class DeployController {
	
	@Autowired
	private DeploymentService deploymentService;

    @RequestMapping("/")
    public Collection<String> index() {
    	Collection<DeployedUnit> deployed = deploymentService.getDeployedUnits();
    	Collection<String> units = new ArrayList<String>();
    	
    	for (DeployedUnit dUnit : deployed) {
    		units.add(dUnit.getDeploymentUnit().getIdentifier());
    	}
    	
        return units;
    }
    
    @RequestMapping(value="/deploy", method=RequestMethod.POST)
    public String deploy(@RequestParam("id")String id, @RequestParam(value="strategy", defaultValue="SINGLETON") String strategy) {
    	String outcome = "Deployment " + id + " deployed successfully";
    	
    	String[] gav = id.split(":");
    	
    	KModuleDeploymentUnit unit = new KModuleDeploymentUnit(gav[0], gav[1], gav[2], null, null, strategy);
        deploymentService.deploy(unit);        
    	
    	return outcome;
    }

    @RequestMapping(value="/undeploy", method=RequestMethod.POST)
    public String undeploy(@RequestParam("id")String id) {
    	String outcome = "";
    	DeployedUnit deployed = deploymentService.getDeployedUnit(id);
    	if (deployed != null) {
    		deploymentService.undeploy(deployed.getDeploymentUnit());
    		outcome = "Deployment " + id + " undeployed successfully";
    	} else {
    		outcome = "No deployment " + id + " found";
    	}
    	return outcome;
    }
}
