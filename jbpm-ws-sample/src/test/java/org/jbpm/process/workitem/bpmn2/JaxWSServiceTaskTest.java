package org.jbpm.process.workitem.bpmn2;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.ws.Endpoint;

import org.drools.SystemEventListenerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.process.workitem.wsht.AsyncHornetQHTWorkItemHandler;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.hornetq.AsyncHornetQTaskClient;
import org.jbpm.task.service.hornetq.HornetQTaskServer;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JaxWSServiceTaskTest extends JbpmJUnitTestCase {
    private Endpoint endpoint;
    private SimpleService service;

    public JaxWSServiceTaskTest() {
    	super(true);
    	setPersistence(true);
    }
    
    @Before
    public void setUp() throws Exception {
    	super.setUp();
        startWebService();
    }

    @After
    public void tearDown() throws Exception {
    	super.tearDown();
        stopWebService();
    }

    @Test
    public void testServiceInvocation() throws Exception {
        StatefulKnowledgeSession ksession = createKnowledgeSession("MultiInstanceWSServiceProcess.bpmn2");
        
        TestWorkItemHandler htHandler = new TestWorkItemHandler();
        
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(ksession));
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", htHandler);
        Map<String, Object> params = new HashMap<String, Object>();
 
        // change this parameter to sync to see elements executes sequentially
        params.put("mode", "async");
 
        
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("MultiInstanceWSServiceProcess", params);
        params = new HashMap<String, Object>();
        // puts as many as needed elements in the map to create for each of them a ws call
        params.put("r1", "mary");
        params.put("r2", "john");
        params.put("r3", "krisv");
        HashMap<String, Object> results = new HashMap<String, Object>();
        results.put("Result", params);
        ksession.getWorkItemManager().completeWorkItem(htHandler.getWorkItem().getId(), results);
        // wait 5 seconds as ws will hold the request for 3 seconds
        Thread.sleep(5000);
        
        ksession.getWorkItemManager().completeWorkItem(htHandler.getWorkItem().getId(), null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
    
    @Test
    public void testPublicServiceInvocation() throws Exception {
        StatefulKnowledgeSession ksession = createKnowledgeSession("WeatherWSServiceProcess.bpmn2");
        
        TestWorkItemHandler htHandler = new TestWorkItemHandler();
        
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(ksession));
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", htHandler);
        Map<String, Object> params = new HashMap<String, Object>();
 
        // change this parameter to sync to see elements executes sequentially
        params.put("mode", "async");
 
        
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("WeatherWSServiceProcess", params);
        params = new HashMap<String, Object>();
        // puts as many as needed elements in the map to create for each of them a ws call
        params.put("r1", "14025");
        params.put("r2", "14513");
        params.put("r3", "10172");
        HashMap<String, Object> results = new HashMap<String, Object>();
        results.put("Result", params);
        ksession.getWorkItemManager().completeWorkItem(htHandler.getWorkItem().getId(), results);
        // wait 5 seconds as ws will hold the request for 3 seconds
        Thread.sleep(5000);
        
        ksession.getWorkItemManager().completeWorkItem(htHandler.getWorkItem().getId(), null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
   
    
    private void startWebService() {
        this.service = new SimpleService();
        this.endpoint = Endpoint.publish("http://127.0.0.1:9876/HelloService/greeting", service);
    }

    private void stopWebService() {
        this.endpoint.stop();
    }
    
    static class TestWorkItemHandler implements WorkItemHandler {

		private static TestWorkItemHandler INSTANCE = new TestWorkItemHandler();
		
		private WorkItem workItem;
		private WorkItem aborted;
		
		public static TestWorkItemHandler getInstance() {
			return INSTANCE;
		}
		
		public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
			this.workItem = workItem;
		}

		public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
			this.aborted = workItem;
		}
		
		public WorkItem getWorkItem() {
			WorkItem result = workItem;
			workItem = null;
			return result;
		}

		public WorkItem getAbortedWorkItem() {
			WorkItem result = aborted;
			aborted = null;
			return result;
		}

	}
}
