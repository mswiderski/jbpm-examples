package org.jbpm.examples.cdi;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;

/**
 * Main entry point for the application to interact with ProcessEngine.
 * It's maintaining only single instance of <code>RuntimeManager</code> of selected strategy.
 * CDI container will be responsible for injecting the <code>RuntimeManager</code> based on 
 * environment produced by <code>EnvironmentProducer</code>.
 *
 * @see EnvironmentProducer
 */
@ApplicationScoped
public class ProcessEngineService {
   
    @Inject
    @Singleton
    private RuntimeManager runtimeManager;
   
    /**
     * Returns all available process definitions for this process engine
     * @return
     */
    public Collection<org.kie.api.definition.process.Process> getProcesses() {
                
        return ((InternalRuntimeManager)runtimeManager).getEnvironment().getKieBase().getProcesses();
    }
       
    /**
     * Returns <code>RuntimeManager</code> for this process engine
     * @return
     */
    public RuntimeManager getRuntimeManager() {
        
        return runtimeManager;
    }

}
