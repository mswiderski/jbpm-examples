package org.jbpm.integration.cmis.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;

public class ManageVariablesProcessEventListener implements
		ProcessEventListener {

	public void beforeProcessStarted(ProcessStartedEvent event) {
		// TODO Auto-generated method stub

	}

	public void afterProcessStarted(ProcessStartedEvent event) {
		// TODO Auto-generated method stub

	}

	public void beforeProcessCompleted(ProcessCompletedEvent event) {
		// TODO Auto-generated method stub

	}

	public void afterProcessCompleted(ProcessCompletedEvent event) {
		ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) event.getKieRuntime().getEnvironment().get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);
		
		VariableScopeInstance variableScope = 
        (VariableScopeInstance) ((WorkflowProcessInstance)event.getProcessInstance()).getContextInstance(VariableScope.VARIABLE_SCOPE);

		Collection<Object> variables = variableScope.getVariables().values();
		
		Iterator<Object> it = variables.iterator();
		
		while (it.hasNext()) {
			Object variable = (Object) it.next();
			
			for (ObjectMarshallingStrategy strategy : strategies) {
				if (strategy.accept(variable)) {
					try {
						strategy.marshal(null, new ObjectOutputStream(new ByteArrayOutputStream()), variable);
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
		// TODO Auto-generated method stub

	}

	public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
		// TODO Auto-generated method stub

	}

	public void beforeNodeLeft(ProcessNodeLeftEvent event) {
		// TODO Auto-generated method stub

	}

	public void afterNodeLeft(ProcessNodeLeftEvent event) {
		// TODO Auto-generated method stub

	}

	public void beforeVariableChanged(ProcessVariableChangedEvent event) {
		// TODO Auto-generated method stub

	}

	public void afterVariableChanged(ProcessVariableChangedEvent event) {
		// TODO Auto-generated method stub

	}

}
