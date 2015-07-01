package org.jbpm.spring.boot;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan
@EnableAutoConfiguration(exclude=HibernateJpaAutoConfiguration.class)
@ImportResource(value= {"classpath:config/jee-tx-context.xml",
		"classpath:config/jpa-context.xml", "classpath:config/jbpm-context.xml", "classpath:config/security-context.xml",})
public class Application {
	
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        if (args.length > 1) {
        	
        	try {
	        	System.out.println("Params available trying to deploy " + args);
		        DeploymentService deploymentService = (DeploymentService) ctx.getBean("deploymentService");
		        
		        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(args[0], args[1], args[2]);
		        deploymentService.deploy(unit);
        	} catch (Throwable e) {
        		System.out.println("Error when deploying = " + e.getMessage());
        	}
        }
    }
}
