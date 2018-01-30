package org.kie.server.swarm.brm;


import java.util.Arrays;

import org.kie.server.swarm.AbstractKieServerMain;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

public class KieServerMain extends AbstractKieServerMain {
    
    public static void main(String[] args) throws Exception {
        Swarm container = new Swarm();

        System.out.println("\tBuilding kie server deployable...");
        JAXRSArchive deployment = createDeployment(container);
        
        System.out.println("\tStaring Wildfly Swarm....");
        container.start();    
        
        System.out.println("\tConfiguring kjars to be auto deployed to server " + Arrays.toString(args));
        installKJars(args);
        
        System.out.println("\tDeploying kie server ....");
        container.deploy(deployment);
    }
}
