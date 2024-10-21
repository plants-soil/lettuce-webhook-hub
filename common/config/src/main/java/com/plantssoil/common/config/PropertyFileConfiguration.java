package com.plantssoil.common.config;

import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.plantssoil.common.fs.ConfigurationFileLocator;

/**
 * <p>A configuration that read properties file. </p>
 * <p>This implementation will load the properties file from:</p>
 * <ul>
 * <li>OS variable: <code>$LETTUCE_HOME/config</code></li>
 * <li>System Property: <code>${lettuce.config.dir}</code></li>
 * <li>OS variable: <code>$JBOSS_HOME/standalone/configuration</code></li>
 * <li>System Property: <code>${jboss.server.config.dir}</code></li>
 * <li>OS variable: <code>$CATALINA_HOME/conf</code></li>
 * <li>System Property: <code>${tomcat.config.dir}</code></li>
 * <li>System Class Path</li>
 * </ul>
 * 
 * @author danialdy
 *
 */
public class PropertyFileConfiguration extends PropertiesConfiguration {
	private final static String LETTUCE_CONFIGURATION_FILE = "lettuce.properties";
	
    private PropertyFileConfiguration(URL url) throws ConfigurationException {
        super(url);
    }

    public static PropertyFileConfiguration createPropertyFileConfiguration() {
    	try {
			return new PropertyFileConfiguration(ConfigurationFileLocator.getConfigurationFile(LETTUCE_CONFIGURATION_FILE));
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
    }
}
