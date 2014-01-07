package org.jbpm.examples.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

/**
 * CDI producer that provides all required beans for the execution.
 * 
 * IMPORTANT: this is for JavaSE environment and not for JavaEE. 
 * JavaEE environment should rely on RequestScoped EntityManager and some TransactionInterceptor 
 * to manage transactions.
 *
 */
@ApplicationScoped
public class EnvironmentProducer {

    private EntityManagerFactory emf;
   
    
    @PersistenceUnit(unitName = "org.jbpm.sample")
    @ApplicationScoped
    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if (this.emf == null) {
            this.emf = Persistence.createEntityManagerFactory("org.jbpm.sample");
        }
        return this.emf;
    }
   
}
