package com.plantssoil.webhook.core.impl;

import com.plantssoil.webhook.core.IWebhookEngine;
import com.plantssoil.webhook.core.IWebhookEngineFactory;

/**
 * The default implementation of Webhook Engine Factory.
 * 
 * @author danialdy
 * @Date 15 Nov 2024 3:26:33 pm
 */
public class DefaultWebhookEngineFactory implements IWebhookEngineFactory {
    private IWebhookEngine engine;

    public DefaultWebhookEngineFactory() {
        // TODO Logger???
        // TODO initialize and prepare persistence if needed
        // TODO initialize and prepare message queue if needed
        // TODO AOP log
        // TODO load all defined & persisted webhooks
        // TODO load all consumer on the webhooks
        // TODO start all subscription on existing webhooks
        this.engine = new DefaultWebhookEngine();
    }

    @Override
    public IWebhookEngine getWebhookEngine() {
        return this.engine;
    }

}
