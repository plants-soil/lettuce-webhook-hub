package com.plantssoil.webhook.persists.logging;

import java.lang.reflect.Method;

import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IWebhookPoster;

/**
 * Webhook event logging
 * 
 * @author danialdy
 * @Date 18 Nov 2024 2:11:06 pm
 */
public interface IWebhookLogging {
    /**
     * Do logging of the webhook event and payload before event happen
     * 
     * @param proxy  the proxy instance that the method was invoked on
     *
     * @param method the {@code Method} instance corresponding to the interface
     *               method invoked on the proxy instance. The declaring class of
     *               the {@code Method} object will be the interface that the method
     *               was declared in, which may be a superinterface of the proxy
     *               interface that the proxy class inherits the method through.
     *
     * @param args   an array of objects containing the values of the arguments
     *               passed in the method invocation on the proxy instance, or
     *               {@code null} if interface method takes no arguments. Arguments
     *               of primitive types are wrapped in instances of the appropriate
     *               primitive wrapper class, such as {@code java.lang.Integer} or
     *               {@code java.lang.Boolean}.
     */
    public void logBefore(Object proxy, Method method, Object[] args);

    /**
     * /** Do logging of the webhook event and payload after event happen
     * 
     * @param proxy  the proxy instance that the method was invoked on
     *
     * @param method the {@code Method} instance corresponding to the interface
     *               method invoked on the proxy instance. The declaring class of
     *               the {@code Method} object will be the interface that the method
     *               was declared in, which may be a superinterface of the proxy
     *               interface that the proxy class inherits the method through.
     *
     * @param args   an array of objects containing the values of the arguments
     *               passed in the method invocation on the proxy instance, or
     *               {@code null} if interface method takes no arguments. Arguments
     *               of primitive types are wrapped in instances of the appropriate
     *               primitive wrapper class, such as {@code java.lang.Integer} or
     *               {@code java.lang.Boolean}.
     *
     */
    public void logAfter(Object proxy, Method method, Object[] args);

    /**
     * Create webhook logging instance, return false if object (proxy) type mismatch
     * 
     * @param proxy the proxy instance that the method was invoked on
     * @return webhook logging instance
     */
    public static IWebhookLogging createInstance(Object proxy) {
        if (IEngine.class.isAssignableFrom(proxy.getClass())) {
            return new WebhookTriggerLogging();
//        } else if (IMessageListener.class.isAssignableFrom(proxy.getClass())) {
//            return new WebhookConsumeLogging();
        } else if (IWebhookPoster.class.isAssignableFrom(proxy.getClass())) {
            return new WebhookPostLogging();
        } else {
            return null;
        }
    }
}
