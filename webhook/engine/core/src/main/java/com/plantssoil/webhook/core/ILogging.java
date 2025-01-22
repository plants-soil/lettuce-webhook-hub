package com.plantssoil.webhook.core;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * The logger for webhook trigger, and consumer dispatcher
 * 
 * @author danialdy
 * @Date 22 Jan 2025 3:31:17 pm
 */
public interface ILogging {
    /**
     * Do logging when publisher trigger message
     * 
     * @param message The message triggered
     */
    public void triggerMessage(Message message);

    /**
     * Do logging when consumer dispatch message
     * 
     * @param message The message is dispatched
     * @param webhook The webhook which message is dispatched to
     * @param tryTime How many time try to dispatch the message
     */
    public void dispatchMessage(Message message, IWebhook webhook, int tryTime);

    /**
     * Do logging when webhook response message to consumer
     * 
     * @param message      The message is dispatched
     * @param webhook      The webhook which responses message
     * @param responseType The response type, could be success, fail, exception, etc
     * @param information  The response information or exception message
     */
    public void responseMessage(Message message, IWebhook webhook, String responseType, String information);

    /**
     * Get the implementation singleton logging instance base on the lettuce
     * configuration, will be null if not configured, means no need logging
     * 
     * @return The implementation singleton loggin instance
     */
    public static ILogging getInstance() {
        String loggingConfigurable = ConfigFactory.getInstance().getConfiguration().getString(LettuceConfiguration.WEBHOOK_ENGINE_LOGGING_CONFIGURABLE);
        if (loggingConfigurable == null) {
            return null;
        }

        IConfigurable configurable = ConfigurableLoader.getInstance().createSingleton(LettuceConfiguration.WEBHOOK_ENGINE_LOGGING_CONFIGURABLE);
        if (configurable instanceof ILogging) {
            return (ILogging) configurable;
        } else {
            String err = String.format("The class %s don't implements %s!", configurable.getClass().getName(), ILogging.class.getName());
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20001, err);
        }
    }
}
