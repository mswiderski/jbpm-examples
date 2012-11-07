package org.jbpm.spring.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.drools.runtime.process.ProcessInstance;
import org.jbpm.spring.domain.HumanTaskEngine;
import org.jbpm.spring.domain.ProcessEngine;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.test.JBPMHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:jbpm-context.xml"})
public class ProcessEngineTest {

    @Autowired
    private ProcessEngine engine;
    @Autowired
    private HumanTaskEngine taskClient;
    
    @BeforeClass
    public static void setupOnce() {
        JBPMHelper.startH2Server();         
        JBPMHelper.setupDataSource();
    }
    
    @Test
    public void testStartScriptProcess() {
        engine.startProcess("script");
    }
    
    @Test
    public void testStartHumanProcess() throws Exception {
        
        long piId = engine.startProcess("humantask");
        List<TaskSummary> tasks = taskClient.getTaskForUser("john");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        long taskId = tasks.get(0).getId();
        taskClient.startTask(taskId, "john");
        
        taskClient.completeTask(taskId, "john");
        
        tasks = taskClient.getTaskForUser("john");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        taskId = tasks.get(0).getId();
        taskClient.startTask(taskId, "john");
        
        taskClient.completeTask(taskId, "john");
        
        tasks = taskClient.getTaskForUser("john");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        taskId = tasks.get(0).getId();
        taskClient.startTask(taskId, "john");
        
        taskClient.completeTask(taskId, "john");
        
        ProcessInstance pi = engine.getKsession().getProcessInstance(piId);
        assertNull(pi);
    }
}
