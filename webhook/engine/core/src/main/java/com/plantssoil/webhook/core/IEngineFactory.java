package com.plantssoil.webhook.core;

import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.webhook.core.exception.EngineException;
import com.plantssoil.webhook.core.impl.InMemoryEngineFactory;

/**
 * Factory for creating the webhook engine<br/>
 * Should have singleton IEngineFactory instance in one JVM<br/>
 * Could get the singleton instance just call the static factory method of this
 * class {@link IEngineFactory#getFactoryInstance()}<br/>
 * e.g:
 * 
 * <pre>
 * <code>
 *   IEngineFactory factory = IEngineFactory.getFactoryInstance();
 *   IEngine engine = factory.getEngine();
 *   ...
 * </code>
 * </pre>
 * 
 * @author danialdy
 * @Date 12 Nov 2024 1:09:34 pm
 */
public interface IEngineFactory {
    /**
     * Call this to create the webhook engine<br/>
     * Publisher could use this engine to trigger events<br/>
     * Subscriber could use this engine to subscribe webhook event from
     * publisher<br/>
     * 
     * @return Webhook engine instance
     */
    public IEngine getEngine();

    /**
     * Get the factory instance of IEngineFactory
     * 
     * @return factory instance
     */
    /**
     * Get the default implementation of this factory (which is singleton)<br/>
     * The default implementation is specified by configuration
     * {@link LettuceConfiguration#WEBHOOK_ENGINE_FACTORY_CONFIGURABLE}
     * 
     * @return Singleton instance of {@link IEngineFactory}
     */
    public static IEngineFactory getFactoryInstance() {
        IConfigurable configurable = ConfigurableLoader.getInstance().createSingleton(LettuceConfiguration.WEBHOOK_ENGINE_FACTORY_CONFIGURABLE,
                InMemoryEngineFactory.class.getName());
        if (configurable instanceof IEngineFactory) {
            return (IEngineFactory) configurable;
        } else {
            String err = String.format("The class %s don't implements %s!", configurable.getClass().getName(), IEngineFactory.class.getName());
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20001, err);
        }
    }
}
