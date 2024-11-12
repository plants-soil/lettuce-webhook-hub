package com.plantssoil.common.mq.simple;

import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.test.TempDirectoryUtility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InMemoryMQConcurrentTest {
    public static void main(String[] args) throws Exception {
        InMemoryMQConcurrentTest test = new InMemoryMQConcurrentTest();
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

        try (FileOutputStream out = new FileOutputStream(util.getTempDir() + "/" + LettuceConfiguration.CONFIGURATION_FILE_NAME)) {
            p.store(out, "## No comments");
        }
        System.setProperty(LettuceConfiguration.CONF_DIRECTORY_PROPERTY_NAME, util.getTempDir());
        ConfigFactory.reload();

        IMessageServiceFactory.getDefaultFactory();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        util.removeTempDirectory();
    }

    private class InnerConsumer implements Runnable {
        private int no;
        private String publisherId;
        private String version;

        InnerConsumer(int no, String publisherId, String version) {
            this.no = no;
            this.publisherId = publisherId;
            this.version = version;
        }

        @Override
        public void run() {
            IMessageConsumer consumer = IMessageServiceFactory.getDefaultFactory().createMessageConsumer();
            consumer.consumerId("Subscriber-" + this.no).publisherId(this.publisherId).version(this.version).addMessageListener(new MessageListener());
            consumer.consume();
        }
    }

    private class InnerPublisher implements Runnable {
        private int sequence;
        private String publisherId;
        private String version;

        InnerPublisher(int sequence, String publisherId, String version) {
            this.sequence = sequence;
            this.publisherId = publisherId;
            this.version = version;
        }

        @Override
        public void run() {
            IMessagePublisher publisher = IMessageServiceFactory.getDefaultFactory().createMessagePublisher();
            publisher.publisherId(this.publisherId).version(this.version);
            String message = String.format("This is the %d message comes from %s (%s)", this.sequence, this.publisherId, this.version);
            publisher.publish(message);
        }
    }

    @Test
    public void testSubscribeOrganization01() {
        ExecutorService es = Executors.newFixedThreadPool(200);
        for (int i = 0; i < 5; i++) {
            es.submit(new InnerConsumer(i, "PUBLISHER-ID-01", "V1.0"));
        }
        // publish message
        for (int i = 0; i < 20; i++) {
//            es.submit(new InnerPublisher(i, "PUBLISHER-ID-01", "V1.0"));
            new InnerPublisher(i, "PUBLISHER-ID-01", "V1.0").run();
        }
        assertTrue(true);
    }

    @Test
    public void testSubscribeOrganization02() {
        // setup 2 subscribers
        ExecutorService es = Executors.newFixedThreadPool(200);
        for (int i = 5; i < 8; i++) {
            es.submit(new InnerConsumer(i, "PUBLISHER-ID-02", "V2.0"));
        }
        // publish message
        for (int i = 0; i < 30; i++) {
//            es.submit(new InnerPublisher(i, "PUBLISHER-ID-02", "V2.0"));
            new InnerPublisher(i, "PUBLISHER-ID-02", "V2.0").run();
        }
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        assertTrue(true);
    }

}
