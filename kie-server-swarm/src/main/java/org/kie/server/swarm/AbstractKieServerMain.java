package org.kie.server.swarm;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.kie.server.api.KieServerConstants;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.services.impl.storage.KieServerState;
import org.kie.server.services.impl.storage.file.KieServerStateFileRepository;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.config.security.Flag;
import org.wildfly.swarm.config.security.SecurityDomain;
import org.wildfly.swarm.config.security.security_domain.ClassicAuthentication;
import org.wildfly.swarm.config.security.security_domain.authentication.LoginModule;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.security.SecurityFraction;
import org.wildfly.swarm.undertow.WARArchive;

public abstract class AbstractKieServerMain {
    
    private static String configFolder = System.getProperty("org.kie.server.swarm.conf", "src/main/config");
    private static String webFolder = "src/main/webapp";

    protected static void installKJars(String[] args) {
        
        if (args == null || args.length == 0) {
            return;
        }        
        String serverId = System.getProperty(KieServerConstants.KIE_SERVER_ID);
        String controller = System.getProperty(KieServerConstants.KIE_SERVER_CONTROLLER);
        
        if ( controller != null) {
            System.out.println("Controller is configured ("+controller+") - no local kjars can be installed");
            return;
        }
        
        // proceed only when kie server id is given and there is no controller
        if (serverId != null) {
            KieServerStateFileRepository repository = new KieServerStateFileRepository();
            KieServerState currentState = repository.load(serverId);
            
            Set<KieContainerResource> containers = new HashSet<KieContainerResource>();
            

            for (String gav : args) {
                String[] gavElements = gav.split(":");
                ReleaseId releaseId = new ReleaseId(gavElements[0], gavElements[1], gavElements[2]);
                
                
                KieContainerResource container = new KieContainerResource(releaseId.getArtifactId(), releaseId, KieContainerStatus.STARTED);
                containers.add(container);
            }
            
            currentState.setContainers(containers);
            
            repository.store(serverId, currentState);
        }
    }
    
    protected static JAXRSArchive createDeployment(Swarm container) throws Exception {
        System.out.println("\tConfiguration folder is " + configFolder);
        
        LoginModule<?> loginModule = new LoginModule<>("UsersRoles");
        loginModule.flag(Flag.REQUIRED)
        .code("UsersRoles")
        .moduleOption("usersProperties", configFolder + "/security/application-users.properties")
        .moduleOption("rolesProperties", configFolder + "/security/application-roles.properties");
        
        SecurityDomain<?> security = new SecurityDomain<>("other")
                .classicAuthentication(new ClassicAuthentication<>()
                        .loginModule(loginModule)); 
        container.fraction(new SecurityFraction().securityDomain(security));

        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "kie-server.war");
        deployment.staticContent();
        deployment.addAllDependencies();
        
        deployment.addAsWebInfResource(new File(configFolder + "/web/web.xml"), "web.xml");
        deployment.addAsWebInfResource(new File(configFolder + "/web/jboss-web.xml"), "jboss-web.xml");
        
                
        return deployment;
        
    }
}
