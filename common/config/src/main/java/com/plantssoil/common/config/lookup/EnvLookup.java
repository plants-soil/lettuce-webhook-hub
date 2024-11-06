package com.plantssoil.common.config.lookup;

import org.apache.commons.lang.text.StrLookup;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Allows users to use OS variables (in .env file or EXPORT command in Linux) as configuration<br/>
 * Differ from properties file, items in env use underscore delimit words<br/>
 * For example:<br/>
 * Configuration in properties:<br/>
 * <code>io.lettuce.engine.url = https://www.lettuce.io/engine/api</code><br/>
 * Should be like this in env:<br/>
 * <code>io_lettuce_engine_url = https://www.lettuce.io/engine/api</code><br/>
 * Or<br/>
 * <code>IO_LETTUCE_ENGINE_URL = https://www.lettuce.io/engine/api</code><br/>
 * 
 * @author danialdy
 *
 */
public class EnvLookup extends StrLookup {

    /**
     * @see org.apache.commons.lang.text.StrLookup#lookup(java.lang.String)
     */
    @Override
    public String lookup(String key) {
        for (String s : synonyms(key)) {
            if (s != null) {
                return s;
            }
        }
        return "";
    }

    private List<String> synonyms(String key) {
        String dotToUnderscore = key.replace(".", "_").replace("-", "_");
        String upperCase = dotToUnderscore.toUpperCase();
        return Stream.of(System.getenv(dotToUnderscore), System.getenv(upperCase))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

}
