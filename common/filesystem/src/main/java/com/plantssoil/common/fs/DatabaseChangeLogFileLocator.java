package com.plantssoil.common.fs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import com.plantssoil.common.fs.exception.FileSystemException;

/**
 * <p>
 * Locate database change log file (Liquibase) from any possible location
 * </p>
 * Seek precedence:
 * <ul>
 * <li>OS variable: <code>$LETTUCE_HOME/data</code></li>
 * <li>System Property: <code>${lettuce.data.dir}</code></li>
 * <li>OS variable: <code>$JBOSS_HOME/standalone/data/lettuce</code></li>
 * <li>System Property: <code>${jboss.server.data.dir}</code></li>
 * <li>OS variable: <code>$CATALINA_HOME/data</code></li>
 * <li>System Property: <code>${tomcat.data.dir}</code></li>
 * <li>System Class Path</li>
 * </ul>
 * 
 * @author danialdy
 *
 */
public class DatabaseChangeLogFileLocator {
    /**
     * Find the database change log file<br/>
     * Seek precedence:
     * <ul>
     * <li>OS variable: <code>$LETTUCE_HOME/data</code></li>
     * <li>System Property: <code>${lettuce.data.dir}</code></li>
     * <li>OS variable: <code>$JBOSS_HOME/standalone/data/lettuce</code></li>
     * <li>System Property: <code>${jboss.server.data.dir}</code></li>
     * <li>OS variable: <code>$CATALINA_HOME/data</code></li>
     * <li>System Property: <code>${tomcat.data.dir}</code></li>
     * <li>System Class Path</li>
     * </ul>
     * 
     * @param fileName data change log file name to seek (could be any format that
     *                 liquibase supports)
     * @return Configuration File URL (will be null if can't find any location)
     */
    public static URL getDatabaseChangeLogFile(String fileName) {
        URL url = null;
        Path dir = DirectoryLocator.getDataDirectory();
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
