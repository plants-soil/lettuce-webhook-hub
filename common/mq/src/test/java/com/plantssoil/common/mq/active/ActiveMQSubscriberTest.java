package com.plantssoil.common.mq.active;

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
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.common.mq.IMessageSubscriber;
import com.plantssoil.common.test.TempDirectoryUtility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ActiveMQSubscriberTest {
    public static void main(String[] args) throws Exception {
        ActiveMQSubscriberTest test = new ActiveMQSubscriberTest();
        setUpBeforeClass();
        test.testSubscribeOrganization01();
        test.testSubscribeOrganization02();
        tearDownAfterClass();
    }

    private static TempDirectoryUtility util = new TempDirectoryUtility();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Thread.sleep(1000);
        ConfigurableLoader.getInstance().removeSingleton(LettuceConfiguration.MESSAGE_SERVICE_FACTORY_CONFIGURABLE);

        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_FACTORY_CONFIGURABLE, MessageServiceFactory.class.getName());
        p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_URI, "tcp://admin:admin@192.168.0.116:61616");

        try (FileOutputStream out = new FileOutputStream(util.getTempDir() + "/lettuce.properties")) {
            p.store(out, "## No comments");
        }
        System.setProperty("lettuce.config.dir", util.getTempDir());
        ConfigFactory.reload();

        IMessageServiceFactory.getDefaultFactory();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        util.removeTempDirectory();
    }

    @Test
    public void testSubscribeOrganization01() {
        for (int i = 0; i < 5; i++) {
            IMessageSubscriber subscriber = IMessageServiceFactory.getDefaultFactory().createMessageSubscriber();
            subscriber.setConsumerId("consumerId-" + i);
            subscriber.addMessageListener(new MessageListener());
            subscriber.subscribe("PUBLISHER-ID-01", "V1.0", null);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // publish message
        IMessagePublisher publisher = IMessageServiceFactory.getDefaultFactory().createMessagePublisher();
        for (int i = 0; i < 20; i++) {
            publisher.publish(new Message("PUBLISHER-ID-01", "V1.0", null, "This is the " + i + " message comes from PUBLISHER-ID-01 (V1.0)"));
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    @Test
    public void testSubscribeOrganization02() {
        // setup 2 subscribers
        for (int i = 5; i < 8; i++) {
            IMessageSubscriber subscriber = IMessageServiceFactory.getDefaultFactory().createMessageSubscriber();
            subscriber.setConsumerId("consumerId-" + i);
            subscriber.addMessageListener(new MessageListener());
            subscriber.subscribe("PUBLISHER-ID-02", "V2.0", null);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // publish message
        IMessagePublisher publisher = IMessageServiceFactory.getDefaultFactory().createMessagePublisher();
        for (int i = 0; i < 30; i++) {
            publisher.publish(new Message("PUBLISHER-ID-02", "V2.0", null, "This is the " + i + " message comes from PUBLISHER-ID-02 (V2.0)"));
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

}
