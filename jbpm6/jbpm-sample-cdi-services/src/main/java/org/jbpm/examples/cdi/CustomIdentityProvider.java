package org.jbpm.examples.cdi;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.kie.services.api.IdentityProvider;

/**
 * Dummy <code>IdentityProvider</code> implementation that allows to provide user identity
 * and his/her roles to the services to be able to capture process instance initiator for example.
 * This needs to be replaced with more proper implementation that is bound to actual security mechanism used.
 *
 */
@ApplicationScoped
public class CustomIdentityProvider implements IdentityProvider {

    public String getName() {
        return "dummy";
    }

    public List<String> getRoles() {

        return Collections.emptyList();
    }

}
