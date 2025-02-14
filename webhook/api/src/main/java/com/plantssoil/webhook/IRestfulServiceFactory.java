package com.plantssoil.webhook;

import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * The Resultful service factory, in order to get restful service
 * implementations base on configuration
 * 
 * @author danialdy
 * @Date 14 Feb 2025 12:07:11 am
 */
public interface IRestfulServiceFactory extends IConfigurable {
    /**
     * Get restful service implementation instance
     * 
     * @param <T>     Restful service
     * @param service Restful service class
     * @return Restful service implementation instance
     */
    public <T> T getRestfulService(Class<T> service);

    /**
     * Get the service factory instance
     * 
     * @return The Restful service factory instance
     */
    public static IRestfulServiceFactory getFactoryInstance() {
        IConfigurable configurable = ConfigurableLoader.getInstance().createSingleton(LettuceConfiguration.WEBHOOK_ENGINE_API_FACTORY_CONFIGURABLE,
                com.plantssoil.webhook.resteasy.ResteasyServiceFactory.class.getName());
        if (configurable instanceof IRestfulServiceFactory) {
            return (IRestfulServiceFactory) configurable;
        } else {
            String err = String.format("The class %s don't implements %s!", configurable.getClass().getName(), IRestfulServiceFactory.class.getName());
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20001, err);
        }
    }
}
