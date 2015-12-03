package org.kie.server.ext.mina.client;

import java.util.HashMap;
import java.util.Map;

import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.helper.KieServicesClientBuilder;


public class MinaClientBuilderImpl implements KieServicesClientBuilder {

    public String getImplementedCapability() {
        return "BRM-Mina";
    }

    public Map<Class<?>, Object> build(KieServicesConfiguration configuration, ClassLoader classLoader) {
        Map<Class<?>, Object> services = new HashMap<Class<?>, Object>();

        services.put(RulesMinaServicesClient.class, new RulesMinaServicesClientImpl(configuration, classLoader));

        return services;
    }

}
