package com.plantssoil.common.config;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.SystemConfiguration;

/**
 * <p>A {@link Configuration} implementation based on current java system properties.</p>
 * <p>
 * This implementation differs from {@link SystemConfiguration}
 * because it pulls the system properties live, rather than caching them when the configuration instance is created.
 * This allows configuration to change by modifying the system properties at runtime.
 * 
 * @author danialdy
 *
 */
public class SystemPropertiesConfiguration extends AbstractConfiguration {

    public SystemPropertiesConfiguration() {
    }

    /**
     * @see org.apache.commons.configuration.Configuration#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * @see org.apache.commons.configuration.Configuration#containsKey(java.lang.String)
     */
    @Override
    public boolean containsKey(String key) {
        return System.getProperties().containsKey(key);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(String key) {
        return System.getProperties().getProperty(key);
    }

    /**
     * @see org.apache.commons.configuration.Configuration#getKeys()
     */
    @Override
    public Iterator<String> getKeys() {
        Set<String> keys = new HashSet<>();
        Set<Object> keySet = System.getProperties().keySet();
        for (Object object : keySet) {
            keys.add(String.valueOf(object));
        }
        return keys.iterator();
    }

    /**
     * @see org.apache.commons.configuration.AbstractConfiguration#addPropertyDirect(java.lang.String, java.lang.Object)
     */
    @Override
    protected void addPropertyDirect(String key, Object value) {
        System.getProperties().setProperty(key, String.valueOf(value));
    }
}