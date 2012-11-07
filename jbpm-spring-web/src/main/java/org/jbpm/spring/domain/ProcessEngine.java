package org.jbpm.spring.domain;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ProcessEngine {

    private StatefulKnowledgeSession ksession;

    public StatefulKnowledgeSession getKsession() {
        return ksession;
    }

    public void setKsession(StatefulKnowledgeSession ksession) {
        this.ksession = ksession;
    }
    
    public long startProcess(String processId) {
        ProcessInstance pi = ksession.startProcess(processId);
        
        return pi.getId();
    }
}
