package com.plantssoil.common.persistence.rdbms;

import java.io.FileOutputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.common.test.TempDirectoryUtility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JPAPersistenceDatasourceTest {
    private static TempDirectoryUtility util = new TempDirectoryUtility();
    private JPAPersistenceTestUtil test = new JPAPersistenceTestUtil();

    @BeforeClass
    public static void clearPersistenceFactory() throws Exception {
        Thread.sleep(1000);
        ConfigurableLoader.getInstance().removeSingleton(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE);

        // setup initial context
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.eclipse.jetty.jndi.InitialContextFactory");
        InitialContext ic = new InitialContext();

        // Construct BasicDataSource
        Class.forName(org.h2.Driver.class.getName());
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName(org.h2.Driver.class.getName());
        bds.setUrl("jdbc:h2:mem:testDatasource;DB_CLOSE_DELAY=-1");
        bds.setUsername("sa");
        bds.setPassword("sa");
        ic.rebind("datasource-lettuce", bds);

        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE, JPAPersistenceFactory.class.getName());
        p.setProperty(LettuceConfiguration.RDBMS_DATASOURCE, "datasource-lettuce");
        p.setProperty(LettuceConfiguration.RDBMS_DATABASE_SHOWSQL, "true");

        try (FileOutputStream out = new FileOutputStream(util.getTempDir() + "/" + LettuceConfiguration.CONFIGURATION_FILE_NAME)) {
            p.store(out, "## No comments");
        }
        System.setProperty(LettuceConfiguration.CONF_DIRECTORY_PROPERTY_NAME, util.getTempDir());
        ConfigFactory.reload();

        IPersistenceFactory.getFactoryInstance();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        util.removeTempDirectory();
    }

    @Test
    public void test1CreateObject() {
        test.testCreate(IPersistenceFactory.getFactoryInstance());
    }

    @Test
    public void test2CreateListOfQ() {
        test.testCreateList(IPersistenceFactory.getFactoryInstance());
    }

    @Test
    public void test3UpdateT() {
        test.testUpdate(IPersistenceFactory.getFactoryInstance());
    }

    @Test
    public void test4UpdateListOfT() {
        test.testUpdateList(IPersistenceFactory.getFactoryInstance());
    }

    @Test
    public void test5RemoveObject() {
        test.testRemove(IPersistenceFactory.getFactoryInstance());
    }

    @Test
    public void test6RemoveListOfQ() {
        test.testRemoveList(IPersistenceFactory.getFactoryInstance());
    }

    @Test
    public void test7QueryStudent() {
        test.testEntityQuery(IPersistenceFactory.getFactoryInstance());
    }
}
