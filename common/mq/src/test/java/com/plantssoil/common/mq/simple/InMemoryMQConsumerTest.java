package com.plantssoil.common.mq.simple;

import static org.junit.Assert.assertTrue;

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
import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.common.mq.MessageListener;
import com.plantssoil.common.mq.TestEventMessage;
import com.plantssoil.common.test.TempDirectoryUtility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InMemoryMQConsumerTest {
    public static void main(String[] args) throws Exception {
        InMemoryMQConsumerTest test = new InMemoryMQConsumerTest();
        setUpBeforeClass();
        test.testConsumeOrganization01();
        test.testConsumeOrganization02();
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
    public void testConsumeOrganization01() {
        for (int i = 0; i < 5; i++) {
            IMessageServiceFactory<TestEventMessage> f = IMessageServiceFactory.getFactoryInstance();
            IMessageConsumer<TestEventMessage> consumer = f.createMessageConsumer().consumerId("consumerId-" + i).queueName("PUBLISHER-ID-01-V1.0")
                    .addMessageListener(new MessageListener());
            consumer.consume(TestEventMessage.class);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // publish message
        IMessageServiceFactory<TestEventMessage> f = IMessageServiceFactory.getFactoryInstance();
        try (IMessagePublisher<TestEventMessage> publisher = f.createMessagePublisher().queueName("PUBLISHER-ID-01-V1.0")) {
            for (int i = 0; i < 20; i++) {
                TestEventMessage om = new TestEventMessage("order.created", String.valueOf(i),
                        "This is the " + i + " message comes from PUBLISHER-ID-01 (V1.0)");
                publisher.publish(om);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    @Test
    public void testConsumeOrganization02() {
        // setup 2 consumers
        for (int i = 5; i < 8; i++) {
            IMessageServiceFactory<TestEventMessage> f = IMessageServiceFactory.getFactoryInstance();
            IMessageConsumer<TestEventMessage> consumer = f.createMessageConsumer().consumerId("consumerId-" + i).queueName("PUBLISHER-ID-02-V2.0")
                    .addMessageListener(new MessageListener());
            consumer.consume(TestEventMessage.class);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // publish message
        IMessageServiceFactory<TestEventMessage> f = IMessageServiceFactory.getFactoryInstance();
        try (IMessagePublisher<TestEventMessage> publisher = f.createMessagePublisher().queueName("PUBLISHER-ID-02-V2.0")) {
            for (int i = 0; i < 30; i++) {
                TestEventMessage om = new TestEventMessage("order.updated", String.valueOf(i),
                        "This is the " + i + " message comes from PUBLISHER-ID-02 (V2.0)");
                publisher.publish(om);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

}
