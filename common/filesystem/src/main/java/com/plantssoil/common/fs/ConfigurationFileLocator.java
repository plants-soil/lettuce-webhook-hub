package com.plantssoil.common.fs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import com.plantssoil.common.fs.exception.FileSystemException;

/**
 * <p>
 * Locate configuration file from any possible location
 * </p>
 * Seek precedence:
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
public class ConfigurationFileLocator {
    /**
     * Find the configuration file<br/>
     * Seek precedence:
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
     * @param fileName configuration file name to seek
     * @return Configuration File URL (will be null if can't find any location)
     */
    public static URL getConfigurationFile(String fileName) {
        URL url = null;
        Path dir = DirectoryLocator.getConfigDirectory();
        if (dir == null) {
            url = ClassLoader.getSystemResource(fileName);
        } else {
            String filePath = String.format("%s/%s", dir.toString(), fileName);
            File file = new File(filePath);
            if (file.exists()) {
                try {
                    url = file.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new FileSystemException(FileSystemException.BUSINESS_EXCEPTION_CODE_11001, e);
                }
            }
        }
        return url;
    }
}
