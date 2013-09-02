package org.jbpm.examples;

import java.util.Collection;

import javax.persistence.Persistence;

import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.SimpleRegisterableItemsFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;

/**
 * Main entry point for the application to interact with process engine.
 * It maintains single <code>RuntimeManager</code> instance that is the actual
 * Process Engine with all assets deployed to it.
 *
 */
public class ProcessEngineService {

    private RuntimeManager runtimeManager;
    
    /**
     * Initializes process engine by creating <code>RuntimeEngine</code> instance will all assets deployed.
     */
    public void init() {
        if (runtimeManager == null) {
            RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
                    .entityManagerFactory(Persistence.createEntityManagerFactory("org.jbpm.sample"))
                    .userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/usergroup.properties"))                
                    .addAsset(ResourceFactory.newClassPathResource("customtask.bpmn"), ResourceType.BPMN2)
                    .addAsset(ResourceFactory.newClassPathResource("humanTask.bpmn"), ResourceType.BPMN2)
                    .get();
            // this way you can add mode work item handlers to the default registerable items factory
            // Alternatively you can add your own implementation of RegisterableItemsFactory
            ((SimpleRegisterableItemsFactory)environment.getRegisterableItemsFactory()).addWorkItemHandler("Log", SystemOutWorkItemHandler.class);
            runtimeManager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        }
    }
    
    /**
     * Disposes ProcessEngine by closing RuntimeManager instance.
     */
    public void dispose() {
        if (runtimeManager != null) {
            runtimeManager.close();
            runtimeManager = null;
        }
    }
   
    /**
     * Returns all available process definitions for this process engine.
     * @return
     */
    public Collection<org.kie.api.definition.process.Process> getProcesses() {
        if (runtimeManager == null) {
            throw new IllegalStateException("RuntimeManager not initialized, did you forget to call init?");
        }
        return ((InternalRuntimeManager)runtimeManager).getEnvironment().getKieBase().getProcesses();
    }
       
    /**
     * Returns <code>RuntimeManager</code> for this process engine
     * @return
     */
    public RuntimeManager getRuntimeManager() {
        if (runtimeManager == null) {
            throw new IllegalStateException("RuntimeManager not initialized, did you forget to call init?");
        }
        return runtimeManager;
    }

}
