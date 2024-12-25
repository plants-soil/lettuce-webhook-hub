package com.plantssoil.common.fs;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Test;

import com.plantssoil.common.test.TempDirectoryUtility;

public class ConfigurationFileLocatorTest {
    private TempDirectoryUtility lettuceDir = new TempDirectoryUtility();
    private TempDirectoryUtility jbossDir = new TempDirectoryUtility();
    private TempDirectoryUtility tomcatDir = new TempDirectoryUtility();

    @After
    public void tearDown() {
        lettuceDir.removeTempDirectory();
        jbossDir.removeTempDirectory();
        tomcatDir.removeTempDirectory();
    }

    @Test
    public void testGetConfigurationFile() {
        assertNull(ConfigurationFileLocator.getConfigurationFile("lettuce.properties"));
    }

    private void createConfigurationFile(String dir) {
        try {
            new File(String.format("%s/%s", dir, "lettuce.properties")).createNewFile();
        } catch (IOException e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testGetConfigurationFileFromLettuce() {
        System.setProperty("lettuce.config.dir", lettuceDir.getTempDir());
        createConfigurationFile(lettuceDir.getTempDir());
        URL url = ConfigurationFileLocator.getConfigurationFile("lettuce.properties");
        System.out.println(url.toString());
        System.setProperty("lettuce.config.dir", "");
        assertTrue(true);
    }

    @Test
    public void testGetConfigurationFileFromJBoss() {
        System.setProperty("jboss.server.config.dir", jbossDir.getTempDir());
        createConfigurationFile(jbossDir.getTempDir());
        URL url = ConfigurationFileLocator.getConfigurationFile("lettuce.properties");
        System.out.println(url.toString());
        System.setProperty("jboss.server.config.dir", "");
        assertTrue(true);
    }

    @Test
    public void testGetConfigurationFileFromTomcat() {
        System.setProperty("tomcat.config.dir", tomcatDir.getTempDir());
        createConfigurationFile(tomcatDir.getTempDir());
        URL url = ConfigurationFileLocator.getConfigurationFile("lettuce.properties");
        System.out.println(url.toString());
        System.setProperty("tomcat.config.dir", "");
        assertTrue(true);
    }

    @Test
    public void testGetConfigurationFileFromClassPath() {
        String location = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        createConfigurationFile(location);
        URL url = ConfigurationFileLocator.getConfigurationFile("lettuce.properties");
        System.out.println(url.toString());
        new File(String.format("%s%s%s", location, System.getProperty("file.separator"), "lettuce.properties")).delete();
        assertTrue(true);
    }

}
