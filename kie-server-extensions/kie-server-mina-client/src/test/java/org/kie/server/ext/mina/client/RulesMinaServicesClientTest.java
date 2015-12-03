package org.kie.server.ext.mina.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.jbpm.test.Person;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.ServiceResponse.ResponseType;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
/**
 * make sure your KIE Server has deployed container named 'demo' that is 
 * built from https://github.com/mswiderski/bpm-projects/tree/master/kie-server-demo
 */
@Ignore("Requires running server")
public class RulesMinaServicesClientTest {

    protected static KieCommands commandsFactory;
    protected static Set<Class<?>> extraClasses = new HashSet<Class<?>>();
    private String containerId = "demo";
    private String jsonContent = "{\"lookup\":\"defaultKieSession\",\"commands\":[{\"insert\":{\"out-identifier\" : \"person\", \"object\":{\"org.jbpm.test.Person\":{\"name\":\"mary\",\"age\":25}}}},{\"fire-all-rules\":\"\"}]}";

    @BeforeClass
    public static void setupFactory() throws Exception {
        commandsFactory = KieServices.Factory.get().getCommands();
        extraClasses.add(Person.class);
    }
    
    protected RulesMinaServicesClient buildClient() {
        KieServicesConfiguration configuration = KieServicesFactory.newRestConfiguration("localhost:9123", null, null);
        List<String> capabilities = new ArrayList<String>();
        // we need to add explicitly capabilities as the mina client does not respond to get server info requests.
        capabilities.add("BRM-Mina");
        
        configuration.setCapabilities(capabilities);
        configuration.setMarshallingFormat(MarshallingFormat.JSON);
        
        configuration.addJaxbClasses(extraClasses);
        
        KieServicesClient kieServicesClient =  KieServicesFactory.newKieServicesClient(configuration);
        
        RulesMinaServicesClient rulesClient = kieServicesClient.getServicesClient(RulesMinaServicesClient.class);
        
        return rulesClient;
    }
    @Test
    public void testClientCallContainerString() {
        RulesMinaServicesClient rulesClient = buildClient();
        
        ServiceResponse<String> response = rulesClient.executeCommands(containerId, jsonContent);
        Assert.assertNotNull(response);
        
        Assert.assertEquals(ResponseType.SUCCESS, response.getType());
        
        String data = response.getResult();
        
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.JSON, this.getClass().getClassLoader());
        
        ExecutionResultImpl results = marshaller.unmarshall(data, ExecutionResultImpl.class);
        Assert.assertNotNull(results);
        
        Object personResult = results.getValue("person");
        Assert.assertTrue(personResult instanceof Person);
        
        Assert.assertEquals("mary", ((Person) personResult).getName());
        Assert.assertEquals("JBoss Community", ((Person) personResult).getAddress());
        Assert.assertEquals(true, ((Person) personResult).isRegistered());
    }
    
    @Test
    public void testClientCallContainerObject() {

        RulesMinaServicesClient rulesClient = buildClient();
        
        List<Command<?>> commands = new ArrayList<Command<?>>();
        BatchExecutionCommand executionCommand = commandsFactory.newBatchExecution(commands, "defaultKieSession");

        Person person = new Person();
        person.setName("mary");
        commands.add(commandsFactory.newInsert(person, "person"));
        commands.add(commandsFactory.newFireAllRules("fired"));
        
        ServiceResponse<String> response = rulesClient.executeCommands(containerId, executionCommand);
        Assert.assertNotNull(response);
        
        Assert.assertEquals(ResponseType.SUCCESS, response.getType());
        
        String data = response.getResult();
        
        Marshaller marshaller = MarshallerFactory.getMarshaller(extraClasses, MarshallingFormat.JSON, this.getClass().getClassLoader());
        
        ExecutionResultImpl results = marshaller.unmarshall(data, ExecutionResultImpl.class);
        Assert.assertNotNull(results);
        
        Object personResult = results.getValue("person");
        Assert.assertTrue(personResult instanceof Person);
        
        Assert.assertEquals("mary", ((Person) personResult).getName());
        Assert.assertEquals("JBoss Community", ((Person) personResult).getAddress());
        Assert.assertEquals(true, ((Person) personResult).isRegistered());
        
    }
}
