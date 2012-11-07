package org.jbpm.spring.domain;

import org.drools.runtime.KnowledgeRuntime;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.TaskService;

public class SelfRegisteringLocalHTHandler extends LocalHTWorkItemHandler {

    public SelfRegisteringLocalHTHandler(TaskService client,
            KnowledgeRuntime session) {
        super(client, session);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", this);
    }

}
