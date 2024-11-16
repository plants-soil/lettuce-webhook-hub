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
        this.engine = new DefaultWebhookEngine();
    }

    @Override
    public IWebhookEngine getWebhookEngine() {
        return this.engine;
    }

}
