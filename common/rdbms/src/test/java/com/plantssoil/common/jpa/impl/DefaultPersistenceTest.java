package com.plantssoil.common.jpa.impl;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.plantssoil.common.jpa.IPersistence;
import com.plantssoil.common.jpa.PersistenceFactory;

public class DefaultPersistenceTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateListOfQ() {
		try (IPersistence persist = PersistenceFactory.getInstance("").getPersistence()) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateT() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateListOfT() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveListOfQ() {
		fail("Not yet implemented");
	}

}
