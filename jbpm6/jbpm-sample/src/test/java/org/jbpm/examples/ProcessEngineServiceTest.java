package org.jbpm.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.List;

import org.jbpm.examples.ProcessEngineService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.EmptyContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * This test case illustrates how to use jBPM RuntimeManager.
 * <ul>
 *  <li>ProcessEngineService - this is the primary entry point for application business logic</li>
 * </ul>
 * 
 * Test case has regular JUnit life cycle phases
 * <ul>
 *  <li>BeforeClass - configures data source to processes/tasks can be persisted</li>
 *  <li>Before - cleans up singleton session id as it is singleton so it must persist session id that was used - not relevant in tests</li>
 *  <li>After - disposes process engine instance</li>
 *  <li>AfterClass - shuts down data source</li>
 * </ul>
 * Test itself is very simple as it aims at presenting:
 * <ul>
 *  <li>hot to list processes available</li>
 *  <li>how to get hold of RuntimeManager and RuntimeEngine</li>
 *  <li>how to start process</li>
 *  <li>work with task (start, complete, etc)</li>
 * </ul>
 */

public class ProcessEngineServiceTest {

    private static PoolingDataSource pds;   
    
    @BeforeClass
    public static void setupOnce() {
        pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(5);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", "sa");
        pds.getDriverProperties().put("password", "");
        pds.getDriverProperties().put("url", "jdbc:h2:mem:jbpm-db;MVCC=true");
        pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
        pds.init();        
    }
    
    @AfterClass
    public static void cleanup() {
        if (pds != null) {
            pds.close();
        }
    }

    @Before
    public void prepare() {
        cleanupSingletonSessionId();
        processService = new ProcessEngineService();
        processService.init();
    }
    
    @After
    public void dispose() {
        processService.dispose();
    }

    
    private ProcessEngineService processService;
    
    
    @Test
    public void testStartSimpleProcess() {
       
        assertNotNull(processService);
        
        
        Collection<org.kie.api.definition.process.Process> processes = processService.getProcesses();
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        RuntimeManager manager = processService.getRuntimeManager();
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        KieSession ksession = engine.getKieSession();
        assertNotNull(ksession);
        
        ksession.startProcess("customtask");        
    }
    
    @Test
    public void testStartUserTaskProcess() {
        
        assertNotNull(processService);
        
        
        Collection<org.kie.api.definition.process.Process> processes = processService.getProcesses();
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        RuntimeManager manager = processService.getRuntimeManager();
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        KieSession ksession = engine.getKieSession();
        assertNotNull(ksession);
        
        ProcessInstance processInstance = ksession.startProcess("org.jbpm.writedocument");
        
        // check the state of process instance
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        TaskService taskService = engine.getTaskService();
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "salaboy");
        taskService.complete(taskId, "salaboy", null);
        
        // check the state of process instance
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
             
        tasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "translator");
        taskService.complete(taskId, "translator", null);
        
        // check the state of process instance
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("reviewer", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "reviewer");
        taskService.complete(taskId, "reviewer", null);
        
        // check the state of process instance
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
      
    }
    
    
    /*
     * helper methods
     */    
    protected void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {            
            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {                
                public boolean accept(File dir, String name) {                    
                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                
                new File(tempDir, file).delete();
            }
        }
    }
}
