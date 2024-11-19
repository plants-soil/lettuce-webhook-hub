package com.plantssoil.webhook.core.logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * The logging handler (within standalone thread) to log every webhook event
 * operate
 * <p>
 * Will log webhook event publish / consume / result (consumer, success or
 * fail)...
 * </p>
 * 
 * @author danialdy
 * @Date 18 Nov 2024 1:00:42 pm
 */
public class WebhookLoggingHandler implements InvocationHandler {
    private final Object target;

    /**
     * Constructor of the logging handler
     * 
     * @param target the proxy instance that the method was invoked on
     */
    public WebhookLoggingHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        IWebhookLogging logging = IWebhookLogging.createInstance(proxy);
        if (logging != null) {
            logging.logBefore(proxy, method, args);
        }
        // Proceed to the original method
        Object result = method.invoke(target, args);
        if (logging != null) {
            logging.logAfter(proxy, method, args);
        }

        return result;
    }

    /**
     * Factory method to create the proxy
     * 
     * @param target the instance to be proxied
     * @return the proxy instance
     */
    public static Object createProxy(Object target) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new WebhookLoggingHandler(target));
    }
}