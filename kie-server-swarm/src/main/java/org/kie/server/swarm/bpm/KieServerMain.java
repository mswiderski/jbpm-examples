package org.kie.server.swarm.bpm;


import java.util.Arrays;

import org.kie.server.swarm.AbstractKieServerMain;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.transactions.TransactionsFraction;
import org.wildfly.swarm.undertow.WARArchive;

public class KieServerMain extends AbstractKieServerMain {
    
    public static void main(String[] args) throws Exception {

        Swarm container = new Swarm();
        // Configure the Datasources subsystem with a driver and a datasource
        container.fraction(new DatasourcesFraction()
                        .jdbcDriver("h2", (d) -> {
                            d.driverClassName("org.h2.Driver");
                            d.xaDatasourceClass("org.h2.jdbcx.JdbcDataSource");
                            d.driverModuleName("com.h2database.h2");
                        })
                        .dataSource("ExampleDS", (ds) -> {
                            ds.driverName("h2");
                            ds.connectionUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
                            ds.userName("sa");
                            ds.password("sa");
                        })                        
        );
        // configure transactions
        container.fraction(TransactionsFraction.createDefaultFraction());        
       
        System.out.println("\tBuilding kie server deployable...");
        JAXRSArchive deployment = createDeployment(container);       
        
        System.out.println("\tStarting Wildfly Swarm....");
        container.start();   
        
        System.out.println("\tConfiguring kjars to be auto deployed to server " + Arrays.toString(args));
        installKJars(args);
        
        System.out.println("\tDeploying kie server ....");
        container.deploy(deployment);
    }
}
