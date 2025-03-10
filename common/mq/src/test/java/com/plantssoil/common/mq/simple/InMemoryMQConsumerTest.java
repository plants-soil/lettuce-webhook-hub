package com.plantssoil.common.mq.simple;

import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.common.mq.MessageConsumerParent;
import com.plantssoil.common.test.TempDirectoryUtility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InMemoryMQConsumerTest extends MessageConsumerParent {
    public static void main(String[] args) throws Exception {
        InMemoryMQConsumerTest test = new InMemoryMQConsumerTest();
        setUpBeforeClass();
        test.test01ConsumeOrganization();
        test.test02ConsumeOrganization();
        test.test03ConsumeTopicMessages();
        tearDownAfterClass();
    }

    private static TempDirectoryUtility util = new TempDirectoryUtility();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Thread.sleep(1000);
        ConfigurableLoader.getInstance().removeSingleton(LettuceConfiguration.MESSAGE_SERVICE_FACTORY_CONFIGURABLE);

        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_FACTORY_CONFIGURABLE, MessageServiceFactory.class.getName());

        try (FileOutputStream out = new FileOutputStream(util.getTempDir() + "/" + LettuceConfiguration.CONFIGURATION_FILE_NAME)) {
            p.store(out, "## No comments");
        }
        System.setProperty(LettuceConfiguration.CONF_DIRECTORY_PROPERTY_NAME, util.getTempDir());
        ConfigFactory.reload();

        IMessageServiceFactory.getFactoryInstance();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        util.removeTempDirectory();
    }

    @Test
    public void test01ConsumeOrganization() {
        consumeOrganization01();
    }

    @Test
    public void test02ConsumeOrganization() {
        consumeOrganization02();
    }

    @Test
    public void test03ConsumeTopicMessages() {
        consumeTopicMessages03();
    }
}
