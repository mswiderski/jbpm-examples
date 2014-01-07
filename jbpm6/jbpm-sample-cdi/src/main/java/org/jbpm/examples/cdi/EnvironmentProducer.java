package org.jbpm.examples.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.cdi.InjectableRegisterableItemsFactory;
import org.jbpm.services.task.HumanTaskConfigurator;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.identity.DefaultUserInfo;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.services.task.impl.command.CommandBasedTaskService;
import org.kie.api.io.ResourceType;
import org.kie.api.task.TaskService;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.task.api.UserGroupCallback;

/**
 * CDI producer that provides all required beans for the execution.
 * 
 * IMPORTANT: this is for JavaSE environment and not for JavaEE. 
 * JavaEE environment should rely on RequestScoped EntityManager and some TransactionInterceptor 
 * to manage transactions.
 * <br/>
 * Here complete <code>RuntimeEnvironment</code> is built for selected strategy of RuntimeManager.
 */
@ApplicationScoped
public class EnvironmentProducer {

    @Inject
    private BeanManager beanManager;
    private TaskService taskService;
    private EntityManagerFactory emf;
    
    @Produces
    @Singleton
    @PerRequest
    @PerProcessInstance
    public RuntimeEnvironment produceEnvironment(EntityManagerFactory emf) {
        
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
                .entityManagerFactory(emf)
                .registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, null))
                .addAsset(ResourceFactory.newClassPathResource("customtask.bpmn"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("humanTask.bpmn"), ResourceType.BPMN2)
                .get();
        return environment;
    }
    
    @Produces    
    public UserGroupCallback produceSelectedUserGroupCalback() {
        return new JBossUserGroupCallbackImpl("classpath:/usergroup.properties");
    }
    
    @PersistenceUnit(unitName = "org.jbpm.sample")
    @ApplicationScoped
    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if (this.emf == null) {
            this.emf = Persistence.createEntityManagerFactory("org.jbpm.sample");
        }
        return this.emf;
    }   
    
    @Produces
	public CommandBasedTaskService produceTaskService(EntityManagerFactory emf) {
		if (taskService == null) {
			HumanTaskConfigurator configurator = HumanTaskServiceFactory.newTaskServiceConfigurator()
					.entityManagerFactory(emf)
					.userGroupCallback(produceSelectedUserGroupCalback())
					.userInfo(new DefaultUserInfo(true))
					.listener(new JPATaskLifeCycleEventListener());

			
			this.taskService = (CommandBasedTaskService) configurator.getTaskService();	
		}
		
		return (CommandBasedTaskService)taskService;
	}
}
