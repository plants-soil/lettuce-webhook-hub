package com.plantssoil.webhook.core;

import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * Factory for creating the webhook engine<br/>
 * Should have singleton IWebhookEngineFactory instance in one JVM<br/>
 * Could get the singleton instance just call the static factory method of this
 * class {@link IWebhookEngineFactory#getFactoryInstance()}<br/>
 * e.g:
 * 
 * <pre>
 * <code>
 *   IWebhookEngineFactory factory = IWebhookEngineFactory.getFactoryInstance();
 *   IWebhookEngine engine = factory.getWebhookEngine();
 *   ...
 * </code>
 * </pre>
 * 
 * @author danialdy
 * @Date 12 Nov 2024 1:09:34 pm
 */
public interface IWebhookEngineFactory {
    /**
     * Call this to create the webhook engine<br/>
     * Publisher could use this engine to publish webhook event<br/>
     * Subscriber could user this engine to subscribe webhook event from
     * publisher<br/>
     * 
     * @return Webhook engine instance
     */
    public IWebhookEngine getWebhookEngine();

    /**
     * Get the factory instance of IWebhookEngineFactory
     * 
     * @return factory instance
     */
    /**
     * Get the default implementation of this factory (which is singleton)<br/>
     * The default implementation is specified by configuration
     * {@link LettuceConfiguration#WEBHOOK_ENGINE_FACTORY_CONFIGURABLE}
     * 
     * @return Singleton instance of {@link IWebhookEngineFactory}
     */
    public static IWebhookEngineFactory getFactoryInstance() {
        IConfigurable configurable = ConfigurableLoader.getInstance().createSingleton(LettuceConfiguration.WEBHOOK_ENGINE_FACTORY_CONFIGURABLE);
        if (configurable instanceof IWebhookEngineFactory) {
            return (IWebhookEngineFactory) configurable;
        } else {
            String err = String.format("The class %s don't implements %s!", configurable.getClass().getName(), IWebhookEngineFactory.class.getName());
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20001, err);
        }
    }
}
