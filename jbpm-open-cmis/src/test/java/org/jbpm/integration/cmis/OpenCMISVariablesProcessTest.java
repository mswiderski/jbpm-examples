/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.integration.cmis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.io.IOUtils;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.jbpm.document.Document;
import org.jbpm.document.service.impl.DocumentImpl;
import org.jbpm.integration.cmis.helper.ManageVariablesProcessEventListener;
import org.jbpm.integration.cmis.impl.OpenCMISPlaceholderResolverStrategy;
import org.jbpm.integration.cmis.impl.OpenCMISSupport;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.InternalTaskService;

import bitronix.tm.resource.jdbc.PoolingDataSource;
// tests are ignored as they rely on external service and are here to illustrate the usage
@Ignore
public class OpenCMISVariablesProcessTest extends OpenCMISSupport {

    private static PoolingDataSource pds;  
    
    private String user = "admin";
	private String password = "admin";
	private String url = "http://cmis.alfresco.com/cmisatom";
	private String repository = "e993fdbb-f417-4c34-911a-21af532c04fc";
    
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.sample");

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/usergroup.properties")) 
                .addEnvironmentEntry(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{
                				new OpenCMISPlaceholderResolverStrategy(user, password, url, repository, UpdateMode.OVERRIDE),
                                new SerializablePlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT  )
                                 })  
                 .registerableItemsFactory(new DefaultRegisterableItemsFactory(){
						@Override
						public List<ProcessEventListener> getProcessEventListeners(
								RuntimeEngine runtime) {
							List<ProcessEventListener> listenrs = super.getProcessEventListeners(runtime);	
							// register special listener to update process variables on process completion
							listenrs.add(new ManageVariablesProcessEventListener());							
							return listenrs;
						}
                    	
                    })
                .addAsset(ResourceFactory.newClassPathResource("cmis-store.bpmn"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("cmis-fetch.bpmn"), ResourceType.BPMN2)
                .get();
         manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        
    }
    
    @After
    public void dispose() {
        manager.close();
    }

    
    private RuntimeManager manager;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testStartUserTaskProcess() throws Exception {
        
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        KieSession ksession = engine.getKieSession();
        assertNotNull(ksession);
        Map<String, Object> params = new HashMap<String, Object>();
        Document doc = new DocumentImpl();
        doc.setAttributes(new HashMap<String, String>());
        doc.setName("simple"+System.currentTimeMillis()+".txt");
		doc.addAttribute("type", "text/plain");
		doc.addAttribute("location", "/jbpm-test");
		String contents = "Initial text";
	    byte[] buf = contents.getBytes();
		doc.setContent(buf);
	
		
		params.put("document", doc);
        ProcessInstance processInstance = ksession.startProcess("cmisintegration-store", params);
        
        // check the state of process instance
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        TaskService taskService = engine.getTaskService();
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        Map<String, Object> taskData = ((InternalTaskService)taskService).getTaskContent(taskId);
        
        Document document = (Document) taskData.get("doc_in");
        
        System.out.println("At first task " + new String(document.getContent()));
        
        contents = "This is some updated test content for our renamed second document.";
	    buf = contents.getBytes();
	    
        document.setContent(buf);
        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", (Map)Collections.singletonMap("doc_out", document));
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        taskId = tasks.get(0).getId();
        taskData = ((InternalTaskService)taskService).getTaskContent(taskId);
        
        document = (Document) taskData.get("doc_in");
        System.out.println("At second task " + new String(document.getContent()));
        contents = "This is some updated test content for our renamed second document. again and again...";
	    buf = contents.getBytes();

        document.setContent(buf);
        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", (Map)Collections.singletonMap("doc_out", document));
        
        // check the state of process instance
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);        
    }
    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
    public void testStartUserTaskProcessFetchDoc() throws Exception {
    	
    	Session session = getRepositorySession(user, password, url, repository);
    	org.apache.chemistry.opencmis.client.api.Document cmisDoc = 
    			createDocument(session, null, "doc_to_load"+System.currentTimeMillis()+".txt", "text/plain", "simple content".getBytes());
        
        try {
	        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
	        assertNotNull(engine);
	        
	        KieSession ksession = engine.getKieSession();
	        assertNotNull(ksession);
	        Map<String, Object> params = new HashMap<String, Object>();
	        Document doc = new DocumentImpl();
	        doc.setIdentifier(cmisDoc.getId());
	        	
			params.put("document", doc);
	        ProcessInstance processInstance = ksession.startProcess("cmisintegrationfetch", params);
	        
	        // check the state of process instance
	        processInstance = ksession.getProcessInstance(processInstance.getId());
	        assertNotNull(processInstance);
	        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
	        
	        TaskService taskService = engine.getTaskService();
	        
	        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
	        assertNotNull(tasks);
	        assertEquals(1, tasks.size());
	        
	        long taskId = tasks.get(0).getId();
	        
	        Map<String, Object> taskData = ((InternalTaskService)taskService).getTaskContent(taskId);
	        
	        Document document = (Document) taskData.get("doc_in");
	        String currentContent = new String(document.getContent());
	        System.out.println("At first task " + currentContent);
	        
	        assertEquals("simple content", currentContent);
	        
	        String contents = currentContent + "\nThis is some updated test content for our renamed second document.";
		    byte[] buf = contents.getBytes();
		    
	        document.setContent(buf);
	        document.addAttribute("updated","true");
	        taskService.start(taskId, "john");
	        taskService.complete(taskId, "john", (Map)Collections.singletonMap("doc_out", document));
	        
	        
	        // check the state of process instance
	        processInstance = ksession.getProcessInstance(processInstance.getId());
	        assertNull(processInstance);   
	        
	        String finalContent = getDocumentContent(session, document.getIdentifier());
	        System.out.println("Final content " + finalContent);
	        assertEquals(contents, finalContent);
        } finally {
        	deleteDocument(session, cmisDoc.getId());
        }
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
    
	private String getDocumentContent(Session session, String documentId) throws IOException {
		
		org.apache.chemistry.opencmis.client.api.Document doc = null;
		try {
			doc = (org.apache.chemistry.opencmis.client.api.Document) findObjectForId(session, documentId);
		} catch (CmisObjectNotFoundException e) {
			return null;
		}
		return IOUtils.toString(doc.getContentStream().getStream());
	}
}
