package com.plantssoil.common.config;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;

import com.plantssoil.common.config.configuration.SystemEnvConfiguration;
import com.plantssoil.common.config.configuration.SystemPropertiesConfiguration;
import com.plantssoil.common.config.exception.ConfigException;
import com.plantssoil.common.config.lookup.CryptLookup;
import com.plantssoil.common.config.lookup.EnvLookup;
import com.plantssoil.common.fs.ConfigurationFileLocator;
import com.plantssoil.common.security.KeyStoreEncrypter;

/**
 * 
 * This is the singleton factory to get configuration object<br/>
 * which could read all configuration items.
 * 
 * @author danialdy
 *
 */
public class ConfigFactory {
    private CompositeConfiguration configuration;
    private static volatile ConfigFactory instance;

    private ConfigFactory() {
        ConfigurationInterpolator.registerGlobalLookup("env", new EnvLookup()); //$NON-NLS-1$
        ConfigurationInterpolator.registerGlobalLookup("crypt", new CryptLookup()); //$NON-NLS-1$

        configuration = new CompositeConfiguration();
        // add OS env variables
        configuration.addConfiguration(new SystemEnvConfiguration());
        // add system property
        configuration.addConfiguration(new SystemPropertiesConfiguration());
        // add keystore configuration
        Map<String, String> keystoreMap = getConfigurationFromKeystore();
        if (keystoreMap.size() > 0) {
            configuration.addConfiguration(new MapConfiguration(keystoreMap));
        }
        // add lettuce configuration property file
        URL url = getConfigurationPropertyFile();
        if (url != null) {
            try {
                configuration.addConfiguration(new PropertiesConfiguration(url));
            } catch (ConfigurationException e) {
                throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12011, e);
            }
        }
    }

    private URL getConfigurationPropertyFile() {
        return ConfigurationFileLocator.getConfigurationFile(LettuceConfiguration.CONFIGURATION_FILE_NAME);
    }

    private Map<String, String> getConfigurationFromKeystore() {
        String keystoreType = System.getProperty(LettuceConfiguration.KEYSTORE_TYPE_PROPERTY_NAME);
        String keystoreFileName = System.getProperty(LettuceConfiguration.KEYSTORE_FILENAME_PROPERTY_NAME);
        String keystorePassword = System.getProperty(LettuceConfiguration.KEYSTORE_PASSWORD_PROPERTY_NAME);
        if (keystoreType == null || keystoreFileName == null) {
            return new HashMap<>();
        }
        if (!keystoreType.equals(KeyStoreEncrypter.KeyStoreType.jceks.toString()) && !keystoreType.equals(KeyStoreEncrypter.KeyStoreType.pkcs12.toString())) {
            throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12012,
                    "Only support jceks or pkcs12 as Lettuce Keystore Type configured by 'lettuce.keystore.type'!");
        }
        URL url = ConfigurationFileLocator.getConfigurationFile(keystoreFileName);
        if (url == null) {
            throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12013, "Can't find lettuce keystore file configured by 'lettuce.keystore.file'!");
        }
        try {
            KeyStoreEncrypter keyStoreEncrypter = KeyStoreEncrypter.getInstance(KeyStoreEncrypter.KeyStoreType.valueOf(keystoreType),
                    new File(url.toURI()).getAbsolutePath(), keystorePassword);
            return keyStoreEncrypter.readEntries();
        } catch (URISyntaxException e) {
            throw new ConfigException(ConfigException.BUSINESS_EXCEPTION_CODE_12014, e);
        }
    }

    /**
     * Get instance of the config factory
     * 
     * @return config factory instance
     */
    public static ConfigFactory getInstance() {
        if (instance == null) {
            synchronized (ConfigFactory.class) {
                if (instance == null) {
                    instance = new ConfigFactory();
                }
            }
        }
        return instance;
    }

    /**
     * Reload all configurations and get new factory instance<br/>
     * This is for testing purpose, should avoid to use in production due to
     * performance issue<br/>
     * 
     * @return config factory instance
     */
    public static ConfigFactory reload() {
        synchronized (ConfigFactory.class) {
            instance = new ConfigFactory();
        }
        return instance;
    }

    /**
     * Get configuration object which contains all luttuce variables
     * 
     * @return Configuration Object
     */
    public Configuration getConfiguration() {
        return configuration;
    }
}
