package org.jbpm.examples.cdi;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.kie.api.runtime.process.WorkItemHandler;

/**
 * Allows to register custom work item handlers for every session that will be build by the 
 * RuntimeManager.
 *
 */
@ApplicationScoped
public class CustomWorkItemHandlerProducer implements WorkItemHandlerProducer {

    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier,  Map<String, Object> params) {
        Map<String, WorkItemHandler> handlers = new HashMap<String, WorkItemHandler>();
        
        handlers.put("Log", new SystemOutWorkItemHandler());
        return handlers;
    }

}
