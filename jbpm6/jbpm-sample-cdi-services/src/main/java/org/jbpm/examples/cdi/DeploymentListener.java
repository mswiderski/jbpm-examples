package org.jbpm.examples.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jbpm.kie.services.impl.event.Deploy;
import org.jbpm.kie.services.impl.event.DeploymentEvent;
import org.jbpm.kie.services.impl.event.Undeploy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An example of how an application code could be notified about deployment and undeployments of units (e.g. kjar).
 * This in turn allows application to provide various options when being informed about deployments/undeployments such
 * as provide new process definition to the user.
 */
@ApplicationScoped
public class DeploymentListener {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentListener.class);
    
    public void onDeployment(@Observes@Deploy DeploymentEvent event) {
        logger.info("Unit {} has been successfully deployed ", event.getDeploymentId(), event.getDeployedUnit());
    }
    
    public void onUndeployment(@Observes@Undeploy DeploymentEvent event) {
        logger.info("Unit {} has been successfully undeployed", event.getDeploymentId());
    }
}
