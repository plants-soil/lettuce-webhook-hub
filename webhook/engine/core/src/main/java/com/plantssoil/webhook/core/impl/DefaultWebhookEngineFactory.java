package com.plantssoil.webhook.core.impl;

import com.plantssoil.webhook.core.IWebhookEngine;
import com.plantssoil.webhook.core.IWebhookEngineFactory;
import com.plantssoil.webhook.core.logging.WebhookLoggingHandler;

/**
 * The default implementation of Webhook Engine Factory.<br/>
 * Should avoid create this factory by it's constructor directly, if you can't
 * ensure it's the unique instance in the JVM<br/>
 * 
 * @see IWebhookEngineFactory#getFactoryInstance()
 * 
 * @author danialdy
 * @Date 15 Nov 2024 3:26:33 pm
 */
public class DefaultWebhookEngineFactory implements IWebhookEngineFactory {
    private IWebhookEngine engine;

    public DefaultWebhookEngineFactory() {
        DefaultWebhookEngine engineImpl = new DefaultWebhookEngine();
        this.engine = (IWebhookEngine) WebhookLoggingHandler.createProxy(engineImpl);
    }

    @Override
    public IWebhookEngine getWebhookEngine() {
        return this.engine;
    }

}
