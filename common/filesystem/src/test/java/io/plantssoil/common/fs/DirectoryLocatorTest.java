package io.plantssoil.common.fs;

import static org.junit.Assert.*;

import org.junit.Test;

public class DirectoryLocatorTest {
	
	@Test
	public void testGetConfigDirectoryLettuce() {
		if (System.getenv("JBOSS_HOME") != null || System.getenv("CATALINA_HOME") != null) {
			assertTrue(true);
			return;
		}
		
		if (System.getenv("LETTUCE_HOME") != null) {
			System.out.println("testGetConfigDirectoryLettuce...LETTUCE_HOME...");
			assertEquals("D:\\workspace\\content\\config", DirectoryLocator.getConfigDirectory().toString());
		}
		else {
			System.out.println("testGetConfigDirectoryLettuce...PROPERTY...");
			System.setProperty("lettuce.config.dir", "D:\\workspace\\content\\config");
			assertEquals("D:\\workspace\\content\\config", DirectoryLocator.getConfigDirectory().toString());
			System.setProperty("lettuce.config.dir", "");
		}
	}
	
	@Test
	public void testGetConfigDirectoryJBoss() {
		if (System.getenv("LETTUCE_HOME") != null || System.getenv("CATALINA_HOME") != null) {
			assertTrue(true);
			return;
		}
		
		if (System.getenv("JBOSS_HOME") != null) {
			System.out.println("testGetConfigDirectoryJBoss...JBOSS_HOME...");
			assertEquals("D:\\jboss\\wildfly-23.0.2.Final\\standalone\\configuration", DirectoryLocator.getConfigDirectory().toString());
		}
		else {
			System.out.println("testGetConfigDirectoryJBoss...PROPERTY...");
			System.setProperty("jboss.server.config.dir", "D:\\jboss\\wildfly-23.0.2.Final\\standalone\\configuration");
			assertEquals("D:\\jboss\\wildfly-23.0.2.Final\\standalone\\configuration", DirectoryLocator.getConfigDirectory().toString());
			System.setProperty("jboss.server.config.dir", "");
		}
	}

	@Test
	public void testGetConfigDirectoryTomcat() {
		if (System.getenv("LETTUCE_HOME") != null || System.getenv("JBOSS_HOME") != null) {
			assertTrue(true);
			return;
		}
		
		if (System.getenv("CATALINA_HOME") != null) {
			System.out.println("testGetConfigDirectoryTomcat...CATALINA_HOME...");
			assertEquals("D:\\apache-tomcat-3.9.5\\conf", DirectoryLocator.getConfigDirectory().toString());
		}
		else {
			System.out.println("testGetConfigDirectoryTomcat...PROPERTY...");
			System.setProperty("tomcat.config.dir", "D:\\apache-tomcat-3.9.5\\conf");
			assertEquals("D:\\apache-tomcat-3.9.5\\conf", DirectoryLocator.getConfigDirectory().toString());
			System.setProperty("tomcat.config.dir", "");
		}
	}

	@Test
	public void testGetDataDirectoryLettuce() {
		if (System.getenv("JBOSS_HOME") != null || System.getenv("CATALINA_HOME") != null) {
			assertTrue(true);
			return;
		}
		
		if (System.getenv("LETTUCE_HOME") != null) {
			System.out.println("testGetDataDirectoryLettuce...LETTUCE_HOME...");
			assertEquals("D:\\workspace\\content\\data", DirectoryLocator.getDataDirectory().toString());
		}
		else {
			System.out.println("testGetDataDirectoryLettuce...PROPERTY...");
			System.setProperty("lettuce.data.dir", "D:\\workspace\\content\\data");
			assertEquals("D:\\workspace\\content\\data", DirectoryLocator.getDataDirectory().toString());
			System.setProperty("lettuce.data.dir", "");
		}
	}
	
	@Test
	public void testGetDataDirectoryJBoss() {
		if (System.getenv("LETTUCE_HOME") != null || System.getenv("CATALINA_HOME") != null) {
			assertTrue(true);
			return;
		}
		
		if (System.getenv("JBOSS_HOME") != null) {
			System.out.println("testGetDataDirectoryJBoss...JBOSS_HOME...");
			assertEquals("D:\\jboss\\wildfly-23.0.2.Final\\standalone\\data\\lettuce", DirectoryLocator.getDataDirectory().toString());
		}
		else {
			System.out.println("testGetDataDirectoryJBoss...PROPERTY...");
			System.setProperty("jboss.server.data.dir", "D:\\jboss\\wildfly-23.0.2.Final\\standalone\\data");
			assertEquals("D:\\jboss\\wildfly-23.0.2.Final\\standalone\\data\\lettuce", DirectoryLocator.getDataDirectory().toString());
			System.setProperty("jboss.server.data.dir", "");
		}
	}

	@Test
	public void testGetDataDirectoryTomcat() {
		if (System.getenv("LETTUCE_HOME") != null || System.getenv("JBOSS_HOME") != null) {
			assertTrue(true);
			return;
		}
		
		if (System.getenv("CATALINA_HOME") != null) {
			System.out.println("testGetDataDirectoryTomcat...CATALINA_HOME...");
			assertEquals("D:\\apache-tomcat-3.9.5\\data", DirectoryLocator.getDataDirectory().toString());
		}
		else {
			System.out.println("testGetDataDirectoryTomcat...PROPERTY...");
			System.setProperty("tomcat.data.dir", "D:\\apache-tomcat-3.9.5\\data");
			assertEquals("D:\\apache-tomcat-3.9.5\\data", DirectoryLocator.getDataDirectory().toString());
			System.setProperty("tomcat.data.dir", "");
		}
	}
	
	@Test
	public void testCouldNotFoundAnyDirectory() {
		assertNull(DirectoryLocator.getConfigDirectory());
		assertNull(DirectoryLocator.getDataDirectory());
	}

}
