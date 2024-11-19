package com.plantssoil.webhook.core.logging;

import java.lang.reflect.Method;

/**
 * Webhook Publish Logging
 * 
 * @author danialdy
 * @Date 18 Nov 2024 2:07:37 pm
 */
public class WebhookPublishLogging implements IWebhookLogging {

    @Override
    public void logBefore(Object proxy, Method method, Object[] args) {
    }

    @Override
    public void logAfter(Object proxy, Method method, Object[] args) {
        // TODO Auto-generated method stub

    }

}
