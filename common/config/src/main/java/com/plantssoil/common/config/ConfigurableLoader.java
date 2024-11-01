package com.plantssoil.common.config;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.plantssoil.common.config.exception.ConfigException;

/**
 * Read and Load configurable class from the configured name which defined in
 * lettuce.properties
 * 
 * @see com.plantssoil.common.config.IConfigurable
 * @author danialdy
 *
 */
public class ConfigurableLoader {
    private static volatile ConfigurableLoader instance;
    private Map<String, IConfigurable> configurables;

    private ConfigurableLoader() {
        configurables = new ConcurrentHashMap<>();
    }

    public static ConfigurableLoader getInstance() {
        if (instance == null) {
            synchronized (ConfigurableLoader.class) {
                if (instance == null) {
                    instance = new ConfigurableLoader();
                }
            }
        }
        return instance;
    }

    /**
     * Load the configurable singleton instance (which implements
     * com.plantssoil.common.config.IConfigurable), usually used for factory
     * initialize
     * 
     * @param configName configure item name
     * @return The singleton instance which implements
     *         com.plantssoil.common.config.IConfigurable
     * 
     * @see com.plantssoil.common.config.IConfigurable
     */
    public IConfigurable createSingleton(String configName) {
        IConfigurable configurable = configurables.get(configName);
        if (configurable != null) {
            return configurable;
        }

        synchronized (configName) {
            configurable = configurables.get(configName);
            if (configurable == null) {
                configurable = createConfigurable(configName, true);
            }
            return configurable;
        }
    }

    /**
     * Remove the configurable singleton instance (if it is and exists)<br/>
     * The method {@link AutoCloseable#close()} will be called 1 seconds later if
     * the singleton implements {@link AutoCloseable} interface <br/>
     * 
     * @param configName configure item name
     * @return The singleton instance removed
     * 
     * @see com.plantssoil.common.config.IConfigurable
     */
    public IConfigurable removeSingleton(String configName) {
        IConfigurable singleton = configurables.remove(configName);
        if (singleton != null && singleton instanceof AutoCloseable) {
            try {
                ((AutoCloseable) singleton).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return singleton;
    }

    /**
     * Create new configurable instance (which implements
     * com.plantssoil.common.config.IConfigurable)
     * 
     * @param configName configure item name
     * @return The new instance which implements
     *         com.plantssoil.common.config.IConfigurable
     * 
     * @see com.plantssoil.common.config.IConfigurable
     * 
     */
    public IConfigurable createConfigurable(String configName) {
        return createConfigurable(configName, false);
    }

    private IConfigurable createConfigurable(String configName, boolean singleton) {
        String clazzName = ConfigFactory.getInstance().getConfiguration().getString(configName);
        if (clazzName == null || clazzName.strip().length() == 0) {
            throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12001, "Can't find the configuration: " + configName);
        }
        try {
            Class<?> clazz = Class.forName(clazzName);
            if (IConfigurable.class.isAssignableFrom(clazz)) {
                IConfigurable configurable = (IConfigurable) clazz.getConstructor().newInstance();
                if (singleton) {
                    configurables.put(configName, configurable);
                }
                return configurable;
            } else {
                String err = String.format("The implementation (%s) with configure name (%s) does not implement '%s'!", clazz.getName(), configName,
                        IConfigurable.class.getName());
                throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12002, err);
            }
        } catch (ClassNotFoundException e) {
            throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12003, e);
        } catch (InstantiationException e) {
            throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12004, e);
        } catch (IllegalAccessException e) {
            throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12005, e);
        } catch (IllegalArgumentException e) {
            throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12006, e);
        } catch (InvocationTargetException e) {
            throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12007, e);
        } catch (NoSuchMethodException e) {
            throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12008, e);
        } catch (SecurityException e) {
            throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12009, e);
        }
    }
}
