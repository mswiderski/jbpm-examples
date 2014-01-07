package org.jbpm.examples.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.deployment.DeploymentUnit.RuntimeStrategy;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.scanner.MavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * This test case illustrates how to use the CDI services that was built to bring the power of jBPM.
 * <ul>
 *  <li>ProcessEngineService - this is the primary entry point for application business logic</li>
 * </ul>
 * 
 * Test case has regular JUnit life cycle phases
 * <ul>
 *  <li>BeforeClass - configures data source to processes/tasks can be persisted</li>
 *  <li>Before - ensures that sample kjar is deployed to maven repository to it can be deployed to the runtime</li>
 *  <li>AfterClass - shuts down data source</li>
 * </ul>
 * Additionally since this is Arquillian based test there is a Deployment section that is responsible for 
 * setting up the CDI container.
 * <br/>
 * Test itself is very simple as it aims at presenting:
 * <ul>
 *  <li>how to deploy kjar to the runtime</li>
 *  <li>hot to list processes available</li>
 *  <li>how to get hold of RuntimeManager and RuntimeEngine</li>
 *  <li>how to start process</li>
 *  <li>hot to undeploy kjar from the runtime</li>
 * </ul>
 */
@RunWith(Arquillian.class)
public class ProcessEngineServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessEngineServiceTest.class);
    
    @Deployment()
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "jbpm-cdi-sample.jar")
        		.addPackage("org.jbpm.services.task")
                .addPackage("org.jbpm.services.task.wih") // work items org.jbpm.services.task.wih
                .addPackage("org.jbpm.services.task.annotations")
                .addPackage("org.jbpm.services.task.api")
                .addPackage("org.jbpm.services.task.impl")
                .addPackage("org.jbpm.services.task.events")
                .addPackage("org.jbpm.services.task.exception")
                .addPackage("org.jbpm.services.task.identity")
                .addPackage("org.jbpm.services.task.factories")
                .addPackage("org.jbpm.services.task.internals")
                .addPackage("org.jbpm.services.task.internals.lifecycle")
                .addPackage("org.jbpm.services.task.lifecycle.listeners")
                .addPackage("org.jbpm.services.task.query")
                .addPackage("org.jbpm.services.task.util")
                .addPackage("org.jbpm.services.task.commands") // This should not be required here
                .addPackage("org.jbpm.services.task.deadlines") // deadlines
                .addPackage("org.jbpm.services.task.deadlines.notifications.impl")
                .addPackage("org.jbpm.services.task.subtask")
                .addPackage("org.jbpm.services.task.rule")
                .addPackage("org.jbpm.services.task.rule.impl")

                .addPackage("org.kie.api.runtime.manager")
                .addPackage("org.kie.internal.runtime.manager")
                .addPackage("org.kie.internal.runtime.manager.context")
                .addPackage("org.kie.internal.runtime.manager.cdi.qualifier")
                
                .addPackage("org.jbpm.runtime.manager.impl")
                .addPackage("org.jbpm.runtime.manager.impl.cdi")                               
                .addPackage("org.jbpm.runtime.manager.impl.factory")
                .addPackage("org.jbpm.runtime.manager.impl.jpa")
                .addPackage("org.jbpm.runtime.manager.impl.manager")
                .addPackage("org.jbpm.runtime.manager.impl.task")
                .addPackage("org.jbpm.runtime.manager.impl.tx")
                
                .addPackage("org.jbpm.shared.services.api")
                .addPackage("org.jbpm.shared.services.impl")
                .addPackage("org.jbpm.shared.services.impl.tx")
                
                .addPackage("org.jbpm.kie.services.api")
                .addPackage("org.jbpm.kie.services.impl")
                .addPackage("org.jbpm.kie.services.cdi.producer")
                .addPackage("org.jbpm.kie.services.api.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.bpmn2")
                .addPackage("org.jbpm.kie.services.impl.event.listeners")
                .addPackage("org.jbpm.kie.services.impl.audit")
                
                .addPackage("org.jbpm.kie.services.impl.example")
                .addPackage("org.kie.commons.java.nio.fs.jgit")
                .addPackage("org.jbpm.examples.cdi.helper") 
                .addPackage("org.jbpm.examples.cdi")
                .addAsResource("jndi.properties", "jndi.properties")
                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

    }
    
    private static PoolingDataSource pds;
    
    private static final String ARTIFACT_ID = "jbpm-module";
    private static final String GROUP_ID = "org.jbpm.test";
    private static final String VERSION = "1.0.0";
    
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
        
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        File kjar = new File("src/main/resources/kjar/jbpm-module.jar");
        File pom = new File("src/main/resources/kjar/pom.xml");
        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId, kjar, pom);
    }

    @Inject
    private ProcessEngineService processService;
    
    
    @Test
    public void testDeployAndStartSimpleProcess() {
        assertNotNull(processService);
        
        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        processService.deployUnit(unit);
        logger.info("TEST:Unit {} has been deployed", unit);
        Collection<ProcessAssetDesc> processes = processService.getProcesses();
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        RuntimeManager manager = processService.getRuntimeManager(unit.getIdentifier());
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        KieSession ksession = engine.getKieSession();
        assertNotNull(ksession);
        
        ksession.startProcess("customtask");
        
        processService.undeployUnit(unit);
        logger.info("TEST:Unit {} has been undeployed", unit);
        
        processes = processService.getProcesses();
        assertNotNull(processes);
        assertEquals(0, processes.size());
    }
    
    @Test
    public void testDeployAndStartSimpleProcessProcessInstanceStrategy() {
        assertNotNull(processService);
        
        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        unit.setStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);
        processService.deployUnit(unit);
        logger.info("TEST:Unit {} has been deployed", unit);
        Collection<ProcessAssetDesc> processes = processService.getProcesses();
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        RuntimeManager manager = processService.getRuntimeManager(unit.getIdentifier());
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        assertNotNull(engine);
        
        KieSession ksession = engine.getKieSession();
        assertNotNull(ksession);
        
        ksession.startProcess("customtask");
        
        processService.undeployUnit(unit);
        logger.info("TEST:Unit {} has been undeployed", unit);
        
        processes = processService.getProcesses();
        assertNotNull(processes);
        assertEquals(0, processes.size());
    }
    
    @Test
    public void testDeployAndStartUserTaskProcess() {
        assertNotNull(processService);
        
        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        processService.deployUnit(unit);
        logger.info("TEST:Unit {} has been deployed", unit);
        Collection<ProcessAssetDesc> processes = processService.getProcesses();
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        RuntimeManager manager = processService.getRuntimeManager(unit.getIdentifier());
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
        
        try {
            processService.undeployUnit(unit);
            fail("It's not possible to undeploy when there are active process instances");
        } catch (Exception e) {
            // do nothing it's expected
        }
        
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
        
        // now since the process is completed it's safe to undeploy the kjar
        processService.undeployUnit(unit);
        logger.info("TEST:Unit {} has been undeployed", unit);
        
        processes = processService.getProcesses();
        assertNotNull(processes);
        assertEquals(0, processes.size());
    }
    
    @Test
    public void testDeployAndStartUserTaskProcessProcessInstanceStrategy() {
        assertNotNull(processService);
        
        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        processService.deployUnit(unit);
        unit.setStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);
        logger.info("TEST:Unit {} has been deployed", unit);
        Collection<ProcessAssetDesc> processes = processService.getProcesses();
        assertNotNull(processes);
        assertEquals(2, processes.size());
        
        RuntimeManager manager = processService.getRuntimeManager(unit.getIdentifier());
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        assertNotNull(engine);
        
        KieSession ksession = engine.getKieSession();
        assertNotNull(ksession);
        
        ProcessInstance processInstance = ksession.startProcess("org.jbpm.writedocument");
        manager.disposeRuntimeEngine(engine);
        
        engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        assertNotNull(engine);
        
        ksession = engine.getKieSession();
        assertNotNull(ksession);
        
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
        
        manager.disposeRuntimeEngine(engine);
        
        engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        assertNotNull(engine);
        
        ksession = engine.getKieSession();
        assertNotNull(ksession);
        
        taskService = engine.getTaskService();
        
        // check the state of process instance
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        try {
            processService.undeployUnit(unit);
            fail("It's not possible to undeploy when there are active process instances");
        } catch (Exception e) {
            // do nothing it's expected
        }
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "translator");
        taskService.complete(taskId, "translator", null);
        
        manager.disposeRuntimeEngine(engine);
        
        engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        assertNotNull(engine);
        
        ksession = engine.getKieSession();
        assertNotNull(ksession);
        
        taskService = engine.getTaskService();
        
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
        
        // now since the process is completed it's safe to undeploy the kjar
        processService.undeployUnit(unit);
        logger.info("TEST:Unit {} has been undeployed", unit);
        
        processes = processService.getProcesses();
        assertNotNull(processes);
        assertEquals(0, processes.size());
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
