package com.plantssoil.common.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	private Map<String, Class<IConfigurable>> configurableClasses;

	private ConfigurableLoader() {
		configurableClasses = new ConcurrentHashMap<>();
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
	 * Load the configurable class (implements
	 * com.plantssoil.common.config.IConfigurable)
	 * 
	 * @param configName configure item name in lettuce.properties
	 * @return The class which implements com.plantssoil.common.config.IConfigurable
	 *         interface
	 * @see com.plantssoil.common.config.IConfigurable
	 */
	@SuppressWarnings("unchecked")
	public Class<IConfigurable> loadConfigurable(String configName) {
		Class<IConfigurable> configurable = configurableClasses.get(configName);
		if (configurable != null) {
			return configurable;
		}
		String clazzName = ConfigFactory.getInstance().getConfiguration().getString(configName);
		if (clazzName == null || clazzName.strip().length() == 0) {
			throw new RuntimeException("Can't find the configuration: " + configName);
		}
		try {
			Class<?> clazz = Class.forName(clazzName);
			if (IConfigurable.class.isAssignableFrom(clazz)) {
				configurableClasses.put(configName, ((Class<IConfigurable>) clazz));
				return (Class<IConfigurable>) clazz;
			} else {
				String err = String.format("The implementation (%s) with configure name (%s) does not implement 'com.plantssoil.common.config.IConfigurable'!",
						clazz.getName(), configName);
				throw new RuntimeException(err);
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
