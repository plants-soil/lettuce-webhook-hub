package com.plantssoil.common.jpa.impl;

import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.plantssoil.common.config.LettuceConfiguration;

public class DefaultPersistenceTest1 {
	private DefaultPersistenceCases test = new DefaultPersistenceCases();

	@Before
	public void setUp() throws Exception {
		Properties p = new Properties();
		p.setProperty(LettuceConfiguration.RDBMS_JPA_PERSISTENCE_CONFIGURABLE, DefaultPersistence.class.getName());

		p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_DRIVER, org.h2.Driver.class.getName());
		p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_URL, "jdbc:h2:mem:testShared;DB_CLOSE_DELAY=-1");
		p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_USERNAME, "sa");
		p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_PASSWORD, "sa");
		p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_POOLSIZE, "20");
		p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_SHOWSQL, "false");

		try (FileOutputStream out = new FileOutputStream(test.util.getTempDir() + "/lettuce.properties")) {
			p.store(out, "## No comments");
		}

		System.setProperty("lettuce.config.dir", test.util.getTempDir());
	}

	@After
	public void tearDown() throws Exception {
		test.util.removeTempDirectory();
	}

	@Test
	public void testCreateObject() {
		test.testCreate();
	}

	@Test
	public void testCreateListOfQ() {
		test.testCreateList();
	}

	@Test
	public void testUpdateT() {
		test.testUpdate();
	}

	@Test
	public void testUpdateListOfT() {
		test.testUpdateList();
	}

	@Test
	public void testRemoveObject() {
		test.testRemove();
	}

	@Test
	public void testRemoveListOfQ() {
		test.testRemoveList();
	}

}
