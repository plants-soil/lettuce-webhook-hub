package com.plantssoil.webhook.persists.registry;

import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.rdbms.JPAPersistenceFactory;
import com.plantssoil.common.test.TempDirectoryUtility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JPAPersistedRegistryTest extends PersistedRegistryTestParent {
    private static TempDirectoryUtility util = new TempDirectoryUtility();

    public static void main(String[] args) throws Exception {
        JPAPersistedRegistryTest test = new JPAPersistedRegistryTest();
        JPAPersistedRegistryTest.setUpBeforeClass();
        test.addPublisher();
        test.updatePublisher();
        test.addSubscriber();
        test.updateSubscriber();
        test.removeSubscriber();
        test.messageTrigger();
        JPAPersistedRegistryTest.tearDownAfterClass();
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Thread.sleep(1000);
        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE, JPAPersistenceFactory.class.getName());
        p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_URL, "jdbc:h2:mem:testJdbc;DB_CLOSE_DELAY=-1");
        p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_USERNAME, "sa");
        p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_PASSWORD, "sa");
        p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_POOLSIZE, "22");
        p.setProperty(LettuceConfiguration.RDBMS_DATABASE_DRIVER, org.h2.Driver.class.getName());
        p.setProperty(LettuceConfiguration.RDBMS_DATABASE_SHOWSQL, "false");

        p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_FACTORY_CONFIGURABLE, com.plantssoil.common.mq.rabbit.MessageServiceFactory.class.getName());
        p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_URI, "amqp://lettuce:lettuce20241101@192.168.0.67:5672/lettuce");
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_VERSION, "1.0.0");
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_FACTORY_CONFIGURABLE,
                com.plantssoil.webhook.core.impl.SingleMessageQueueEngineFactory.class.getName());
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_REGISTRY_CONFIGURABLE, com.plantssoil.webhook.persists.registry.PersistedRegistry.class.getName());

        try (FileOutputStream out = new FileOutputStream(util.getTempDir() + "/" + LettuceConfiguration.CONFIGURATION_FILE_NAME)) {
            p.store(out, "## No comments");
        }
        System.setProperty(LettuceConfiguration.CONF_DIRECTORY_PROPERTY_NAME, util.getTempDir());
        ConfigFactory.reload();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        util.removeTempDirectory();
    }

    @Test
    public void test01AddPublisher() {
        addPublisher();
    }

    @Test
    public void test02UpdatePublisher() {
        updatePublisher();
    }

    @Test
    public void test04AddSubscriber() {
        addSubscriber();
    }

    @Test
    public void test05UpdateSubscriber() {
        updateSubscriber();
    }

    @Test
    public void test06DeleteSubscriber() {
        removeSubscriber();
    }

    @Test
    public void test07Trigger() {
        messageTrigger();
    }

}
