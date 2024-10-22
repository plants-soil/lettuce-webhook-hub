package com.plantssoil.common.config.configuration;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 * A {@link Configuration} implementation based on the current OS variables.
 * <p>
 * Lookup value from OS variables, variable 'synonyms' (upper case and underscores instead of kebab/dot) works as well
 * </p>
 * <p>
 * For example, <code>io.luttuce.engine.url = LETTUCE_ENGINE_URL</code> or <code>io_luttuce_engine_url = LETTUCE_ENGINE_URL</code>.
 * </p>
 * 
 * @author danialdy
 *
 */
public class SystemEnvConfiguration extends AbstractConfiguration {

    public SystemEnvConfiguration() {
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {
        throw new IllegalArgumentException("Environment is read-only. Can't add a new variable.");
    }

    private static List<String> synonyms(String key) {
        String dotToUnderscore = key.replace(".", "_").replace("-", "_");
        String upperCase = dotToUnderscore.toUpperCase();
        return List.of(key, dotToUnderscore, upperCase);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(String key) {
        return getProperty(key) != null;
    }

    @Override
    public Object getProperty(String key) {
        for (String s : synonyms(key)) {
            Object result = System.getenv(s);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Iterator<String> getKeys() {
        return System.getenv().keySet().iterator();
    }
}
