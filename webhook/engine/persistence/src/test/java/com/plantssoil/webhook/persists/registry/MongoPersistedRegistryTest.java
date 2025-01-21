package com.plantssoil.webhook.persists.registry;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.common.persistence.mongodb.MongodbPersistenceFactory;
import com.plantssoil.common.test.TempDirectoryUtility;
import com.plantssoil.webhook.persists.beans.DataGroup;
import com.plantssoil.webhook.persists.beans.DataGroupSubscribed;
import com.plantssoil.webhook.persists.beans.Event;
import com.plantssoil.webhook.persists.beans.EventSubscribed;
import com.plantssoil.webhook.persists.beans.Organization;
import com.plantssoil.webhook.persists.beans.Publisher;
import com.plantssoil.webhook.persists.beans.Subscriber;
import com.plantssoil.webhook.persists.beans.Webhook;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MongoPersistedRegistryTest extends PersistedRegistryTestParent {
    private static TempDirectoryUtility util = new TempDirectoryUtility();

    public static void main(String[] args) throws Exception {
        MongoPersistedRegistryTest test = new MongoPersistedRegistryTest();
        MongoPersistedRegistryTest.setUpBeforeClass();
        test.addPublisher();
        test.updatePublisher();
//		test.removePublisher();
        test.addSubscriber();
        test.updateSubscriber();
        test.removeSubscriber();
        test.messageTrigger();
        MongoPersistedRegistryTest.tearDownAfterClass();
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Thread.sleep(1000);
        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE, MongodbPersistenceFactory.class.getName());
        p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_URL,
                "mongodb://lettuce:lettuce20241101@192.168.0.67:27017/?retryWrites=false&retryReads=false");
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
        ConfigurableLoader.reload();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            deleteAllData(p, DataGroupSubscribed.class);
            deleteAllData(p, EventSubscribed.class);
            deleteAllData(p, Webhook.class);
            deleteAllData(p, Subscriber.class);
            deleteAllData(p, Event.class);
            deleteAllData(p, DataGroup.class);
            deleteAllData(p, Publisher.class);
            deleteAllData(p, Organization.class);
        }
        util.removeTempDirectory();
    }

    private static <T> void deleteAllData(IPersistence p, Class<T> entityClass) throws InterruptedException, ExecutionException {
        List<T> list = p.createQuery(entityClass).maxResults(1000).resultList().get();
        p.remove(list);
    }

    @Test
    public void test01AddPublisher() {
        addPublisher();
    }

    @Test
    public void test02UpdatePublisher() {
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updatePublisher();
    }
//
//    @Test
//    public void test03RemovePublisher() {
//        try {
//            Thread.sleep(5 * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        removePublisher();
//    }

    @Test
    public void test04AddSubscriber() {
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        addSubscriber();
    }

    @Test
    public void test05UpdateSubscriber() {
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updateSubscriber();
    }

    @Test
    public void test06DeleteSubscriber() {
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        removeSubscriber();
    }

    @Test
    public void test07Trigger() {
        messageTrigger();
    }

}
