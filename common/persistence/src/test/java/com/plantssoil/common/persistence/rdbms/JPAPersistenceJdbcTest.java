package com.plantssoil.common.persistence.rdbms;

import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.common.test.TempDirectoryUtility;

public class JPAPersistenceJdbcTest {
    private TempDirectoryUtility util = new TempDirectoryUtility();
    private JPAPersistenceTestUtil test = new JPAPersistenceTestUtil();

    @BeforeClass
    public static void clearPersistenceFactory() throws Exception {
        ConfigurableLoader.getInstance().removeSingleton(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE);
    }

    @Before
    public void setUp() throws Exception {
        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE, JPAPersistenceFactory.class.getName());

        p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_DRIVER, org.h2.Driver.class.getName());
        p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_URL, "jdbc:h2:mem:testJdbc;DB_CLOSE_DELAY=-1");
        p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_USERNAME, "sa");
        p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_PASSWORD, "sa");
        p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_POOLSIZE, "22");
        p.setProperty(LettuceConfiguration.ENGINE_CORE_DATABASE_SHOWSQL, "false");

        try (FileOutputStream out = new FileOutputStream(util.getTempDir() + "/lettuce.properties")) {
            p.store(out, "## No comments");
        }
        System.setProperty("lettuce.config.dir", util.getTempDir());
    }

    @After
    public void tearDown() throws Exception {
        util.removeTempDirectory();
    }

    @Test
    public void testCreateObject() {
        test.testCreate(IPersistenceFactory.getDefaultFactory());
    }

    @Test
    public void testCreateListOfQ() {
        test.testCreateList(IPersistenceFactory.getDefaultFactory());
    }

    @Test
    public void testUpdateT() {
        test.testUpdate(IPersistenceFactory.getDefaultFactory());
    }

    @Test
    public void testUpdateListOfT() {
        test.testUpdateList(IPersistenceFactory.getDefaultFactory());
    }

    @Test
    public void testRemoveObject() {
        test.testRemove(IPersistenceFactory.getDefaultFactory());
    }

    @Test
    public void testRemoveListOfQ() {
        test.testRemoveList(IPersistenceFactory.getDefaultFactory());
    }

}
