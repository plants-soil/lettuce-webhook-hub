package com.plantssoil.common.fs;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>Locate config / data directory via OS viriable or system property</p>
 * <ul>
 * <li>Lettuce standalone: OS Variable $LETTUCE_HOME, System property ${lettuce.[config|data].dir}</li>
 * <li>Deployed on Wildfly / JBoss EAP: OS Variable $JBOSS_HOME, System property ${jboss.server.[config|data].dir}</li>
 * <li>Deployed on Tomcat: OS Variable $CATALINA_HOME, System property ${tomcat.[config|data].dir}</li>
 * </ul>
 * 
 * @author danialdy
 *
 */
public class DirectoryLocator {

	/**
	 * Return the standard Lettuce config directory.
	 *
	 * Seek precedence:
	 * <ul>
	 * <li>OS variable: <code>$LETTUCE_HOME/config</code></li>
	 * <li>System Property: <code>${lettuce.config.dir}</code></li>
	 * <li>OS variable: <code>$JBOSS_HOME/standalone/configuration</code></li>
	 * <li>System Property: <code>${jboss.server.config.dir}</code></li>
	 * <li>OS variable: <code>$CATALINA_HOME/conf</code></li>
	 * <li>System Property: <code>${tomcat.config.dir}</code></li>
	 * </ul>
	 */
	public static Path getConfigDirectory() {
		// check lettuce config first
		Path configPath = getLettuceDirectory("config");
		if (configPath != null) {
			return configPath;
		}

		// If that wasn't set, then check to see if we're running in wildfly/jboss eap
		configPath = getJBossDirectory("config", null);
		if (configPath != null) {
			return configPath;
		}

		// If that wasn't set as well, try to locate tomcat conf directory
		configPath = getTomcatDirectory("config");
		if (configPath != null) {
			return configPath;
		}
		
		return null;
	}

	/**
	 * Return the standard Lettuce data directory.
	 *
	 * Seek precedence:
	 * <ul>
	 * <li>OS variable: <code>$LETTUCE_HOME/data</code></li>
	 * <li>System Property: <code>${lettuce.data.dir}</code></li>
	 * <li>OS variable: <code>$JBOSS_HOME/standalone/data/lettuce</code></li>
	 * <li>System Property: <code>${jboss.server.data.dir}</code></li>
	 * <li>OS variable: <code>$CATALINA_HOME/data</code></li>
	 * <li>System Property: <code>${tomcat.data.dir}</code></li>
	 * </ul>
	 */
	public static Path getDataDirectory() {
		// check lettuce data first
		Path configPath = getLettuceDirectory("data");
		if (configPath != null) {
			return configPath;
		}

		// If that wasn't set, then check to see if we're running in wildfly/jboss eap
		configPath = getJBossDirectory("data", "lettuce");
		if (configPath != null) {
			return configPath;
		}

		// If that wasn't set as well, try to locate tomcat data directory
		configPath = getTomcatDirectory("data");
		if (configPath != null) {
			return configPath;
		}
		
		return null;
	}
	
	private static Path getLettuceDirectory(String directoryName) {
		String dir = System.getenv("LETTUCE_HOME");
		if (dir != null && dir.strip().length() > 0) {
			dir = String.format("%s%s%s", dir, System.getProperty("file.separator"), directoryName);
			return Paths.get(dir);
		}
		
		dir = System.getProperty(String.format("lettuce.%s.dir", directoryName));
		if (dir != null && dir.strip().length() > 0) {
			return Paths.get(dir);
		}
		else {
			return null;
		}
	}
	
	private static Path getJBossDirectory(String directoryName, String subdirectory) {
		String dir = System.getenv("JBOSS_HOME");
		if (dir != null && dir.strip().length() > 0) {
			if (subdirectory != null) {
				dir = String.format("%s%sstandalone%s%s%s%s", dir, System.getProperty("file.separator"),
						System.getProperty("file.separator"),
						"config".equals(directoryName) ? "configuration" : directoryName,
						System.getProperty("file.separator"), subdirectory);
				return Paths.get(dir);
			}
			else {
				dir = String.format("%s%sstandalone%s%s", dir, System.getProperty("file.separator"),
						System.getProperty("file.separator"),
						"config".equals(directoryName) ? "configuration" : directoryName);
				return Paths.get(dir);
			}
		}
		
		dir = System.getProperty(String.format("jboss.server.%s.dir", directoryName));
		if (dir != null && dir.strip().length() > 0) {
			if (subdirectory != null) {
				return Paths.get(dir, subdirectory);
			}
			else {
				return Paths.get(dir);
			}
		}
		else {
			return null;
		}
	}
	
	private static Path getTomcatDirectory(String directoryName) {
		String dir = System.getenv("CATALINA_HOME");
		if (dir != null && dir.strip().length() > 0) {
			dir = String.format("%s%s%s", dir, System.getProperty("file.separator"), directoryName);
			return Paths.get(dir);
		}
		
		dir = System.getProperty(String.format("tomcat.%s.dir", directoryName));
		if (dir != null && dir.strip().length() > 0) {
			return Paths.get(dir);
		}
		else {
			return null;
		}
	}

}
