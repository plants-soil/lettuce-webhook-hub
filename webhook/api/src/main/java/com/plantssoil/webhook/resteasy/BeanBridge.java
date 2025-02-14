package com.plantssoil.webhook.resteasy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.logging.InMemoryWebhookLog;
import com.plantssoil.webhook.core.logging.InMemoryWebhookLogLine;
import com.plantssoil.webhook.core.registry.InMemoryDataGroup;
import com.plantssoil.webhook.core.registry.InMemoryEvent;
import com.plantssoil.webhook.core.registry.InMemoryOrganization;
import com.plantssoil.webhook.core.registry.InMemoryPublisher;
import com.plantssoil.webhook.core.registry.InMemorySubscriber;
import com.plantssoil.webhook.core.registry.InMemoryWebhook;
import com.plantssoil.webhook.persists.beans.DataGroup;
import com.plantssoil.webhook.persists.beans.Event;
import com.plantssoil.webhook.persists.beans.Organization;
import com.plantssoil.webhook.persists.beans.Publisher;
import com.plantssoil.webhook.persists.beans.Subscriber;
import com.plantssoil.webhook.persists.beans.Webhook;
import com.plantssoil.webhook.persists.beans.WebhookLog;
import com.plantssoil.webhook.persists.beans.WebhookLogLine;
import com.plantssoil.webhook.persists.registry.PersistedRegistry;

/**
 * Bridge in-memory beans and persisted beans<br/>
 * APIs receive data into in-memory beans, when Webhook Engine use
 * PersistedRegistry as the implementation of IRegistry, need transform
 * in-memory beans to persisted beans before persisting
 * 
 * @author danialdy
 * @Date 10 Feb 2025 11:05:26 am
 */
public class BeanBridge {
    private ImplementationType implementationType;
    private Map<Class<?>, Class<?>> inMemoryToPersistedMapping = new ConcurrentHashMap<>();
    private Map<String, Map<String, Method>> sourceGetters = new ConcurrentHashMap<>();
    private Map<String, Map<String, Method>> targetSetters = new ConcurrentHashMap<>();
    private static BeanBridge instance;

    private enum ImplementationType {
        persisted, inMemory
    }

    private enum MethodPrefix {
        get, set
    }

    private BeanBridge() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        if (r instanceof PersistedRegistry) {
            this.implementationType = ImplementationType.persisted;
            this.inMemoryToPersistedMapping.put(InMemoryDataGroup.class, DataGroup.class);
            this.inMemoryToPersistedMapping.put(InMemoryEvent.class, Event.class);
            this.inMemoryToPersistedMapping.put(InMemoryOrganization.class, Organization.class);
            this.inMemoryToPersistedMapping.put(InMemoryPublisher.class, Publisher.class);
            this.inMemoryToPersistedMapping.put(InMemorySubscriber.class, Subscriber.class);
            this.inMemoryToPersistedMapping.put(InMemoryWebhook.class, Webhook.class);
            this.inMemoryToPersistedMapping.put(InMemoryWebhookLog.class, WebhookLog.class);
            this.inMemoryToPersistedMapping.put(InMemoryWebhookLogLine.class, WebhookLogLine.class);
        } else {
            this.implementationType = ImplementationType.inMemory;
        }
    }

    /**
     * Get the singleton instance of BeanBridge
     * 
     * @return The singleton instance
     */
    public static BeanBridge getInstance() {
        if (instance == null) {
            synchronized (BeanBridge.class) {
                if (instance == null) {
                    instance = new BeanBridge();
                }
            }
        }
        return instance;
    }

    /**
     * Clear the singleton instance of BeanBridge, for testing purpose
     */
    public static void reload() {
        instance = null;
    }

    private Map<String, Method> getSourceGetters(Class<?> clazz) {
        return getMethodMap(clazz, this.sourceGetters, MethodPrefix.get);
    }

    private Map<String, Method> getTargetSetters(Class<?> clazz) {
        return getMethodMap(clazz, this.targetSetters, MethodPrefix.set);
    }

    private Map<String, Method> getMethodMap(Class<?> clazz, Map<String, Map<String, Method>> clazzMethods, MethodPrefix methodPrefix) {
        String clazzName = clazz.getName();
        Map<String, Method> methods = clazzMethods.get(clazzName);
        if (methods == null) {
            synchronized (clazzName.intern()) {
                methods = clazzMethods.get(clazzName);
                if (methods == null) {
                    methods = createMethodMap(clazz, methodPrefix);
                    clazzMethods.put(clazzName, methods);
                }
            }
        }
        return methods;
    }

    private Map<String, Method> createMethodMap(Class<?> clazz, MethodPrefix methodPrefix) {
        Map<String, Method> methodMap = new ConcurrentHashMap<>();
        int parameterCount = methodPrefix == MethodPrefix.get ? 0 : 1;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().startsWith(methodPrefix.name()) && method.getParameterCount() == parameterCount) {
                methodMap.put(method.getName().substring(3), method);
            }
        }
        return methodMap;
    }

    /**
     * Bridge the source bean into target bean instance base on the configuration
     * 
     * @param source The source bean
     * @return The target bean
     * @throws Exception
     */
    public Object bridge(Object source) throws Exception {
        if (this.implementationType == ImplementationType.persisted) {
            Class<?> persistedBeanClass = this.inMemoryToPersistedMapping.get(source.getClass());
            return bridge(source, persistedBeanClass);
        } else {
            return source;
        }
    }

    /**
     * Bridge the source bean into destinationClazz (e.g. Persisted Bean) instance
     * 
     * @param <T>              The destination bean type
     * @param source           The source bean object (in-memory bean)
     * @param destinationClazz The destination bean class
     * @return The destination bean instance
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T> T bridge(Object source, Class<T> destinationClazz) throws Exception {
        if (source.getClass().equals(destinationClazz)) {
            return (T) source;
        }
        T destination = destinationClazz.getConstructor().newInstance();
        copyValue(source, destination);
        return destination;
    }

    /**
     * Copy attributes from source object to destination object
     * 
     * @param source      The source object
     * @param destination The destination object
     * @throws Exception
     */
    public void copyValue(Object source, Object destination) throws Exception {
        Map<String, Method> getters = getSourceGetters(source.getClass());
        Map<String, Method> setters = getTargetSetters(destination.getClass());

        // Copy properties
        for (Map.Entry<String, Method> entry : getters.entrySet()) {
            String propertyName = entry.getKey();
            Method getter = entry.getValue();
            Method setter = setters.get(propertyName);

            if (setter != null) {
                Object value = getter.invoke(source);
                setter.invoke(destination, value);
            }
        }
    }
}
