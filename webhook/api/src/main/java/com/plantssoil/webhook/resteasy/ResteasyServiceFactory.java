package com.plantssoil.webhook.resteasy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.webhook.IRestfulServiceFactory;
import com.plantssoil.webhook.api.EngineApiService;
import com.plantssoil.webhook.api.OrganizationApiService;
import com.plantssoil.webhook.api.PublisherApiService;
import com.plantssoil.webhook.api.SubscriberApiService;
import com.plantssoil.webhook.api.WebhookApiService;

/**
 * The restful service factory implementation base on resteasy
 * 
 * @author danialdy
 * @Date 14 Feb 2025 12:26:32 am
 */
public class ResteasyServiceFactory implements IRestfulServiceFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(ResteasyServiceFactory.class.getName());
    private Map<Class<?>, Object> restfulServices = new ConcurrentHashMap<>();

    public ResteasyServiceFactory() {
        LOGGER.info("Restful API service loaded.");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getRestfulService(Class<T> service) {
        Object impl = this.restfulServices.get(service);
        if (impl == null) {
            synchronized (service) {
                impl = this.restfulServices.get(service);
                if (impl == null) {
                    impl = createImplementation(service);
                    if (impl != null) {
                        this.restfulServices.put(service, impl);
                    }
                }
            }
        }
        return (T) impl;
    }

    @SuppressWarnings("unchecked")
    private <T> T createImplementation(Class<T> service) {
        if (service == EngineApiService.class) {
            return (T) new EngineApiServiceImpl();
        } else if (service == OrganizationApiService.class) {
            return (T) new OrganizationApiServiceImpl();
        } else if (service == PublisherApiService.class) {
            return (T) new PublisherApiServiceImpl();
        } else if (service == SubscriberApiService.class) {
            return (T) new SubscriberApiServiceImpl();
        } else if (service == WebhookApiService.class) {
            return (T) new WebhookApiServiceImpl();
        }
        return null;
    }

}
