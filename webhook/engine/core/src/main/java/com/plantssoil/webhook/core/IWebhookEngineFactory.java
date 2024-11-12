package com.plantssoil.webhook.core;

/**
 * Factory for creating the wehook engine<br/>
 * Should have singleton IWebhookEngineFactory instance in one JVM<br/>
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
}
