package com.plantssoil.webhook.core;

import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.mongodb.MongodbPersistenceFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SingleActiveMessageQueueEngineTest extends IEngineTestParent {
    public static void main(String[] args) throws Exception {
        SingleActiveMessageQueueEngineTest.setUpBeforeClass();
        SingleActiveMessageQueueEngineTest test = new SingleActiveMessageQueueEngineTest();
        test.test01GetVersion();
        test.test02GetRegistry();
        test.test03Trigger();
        Thread.sleep(10000);
        SingleActiveMessageQueueEngineTest.tearDownAfterClass();
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Thread.sleep(1000);
        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE, MongodbPersistenceFactory.class.getName());
        p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_URL,
                "mongodb://lettuce:lettuce20241101@192.168.0.67:27017/?retryWrites=false&retryReads=false");
        p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_FACTORY_CONFIGURABLE, com.plantssoil.common.mq.active.MessageServiceFactory.class.getName());
        p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_URI, "tcp://admin:admin@192.168.0.67:61616");
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_VERSION, "1.0.0");
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_FACTORY_CONFIGURABLE,
                com.plantssoil.webhook.core.impl.SingleMessageQueueEngineFactory.class.getName());

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
    public void test01GetVersion() {
        testGetVersion();
    }

    @Test
    public void test02GetRegistry() {
        testGetRegistry();
    }

    @Test
    public void test03Trigger() {
        testTrigger();
    }

}
