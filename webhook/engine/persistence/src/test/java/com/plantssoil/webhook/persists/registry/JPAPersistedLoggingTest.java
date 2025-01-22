package com.plantssoil.webhook.persists.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.IEntityQuery.FilterOperator;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.common.persistence.rdbms.JPAPersistenceFactory;
import com.plantssoil.common.test.TempDirectoryUtility;
import com.plantssoil.webhook.persists.beans.DataGroup;
import com.plantssoil.webhook.persists.beans.DataGroupSubscribed;
import com.plantssoil.webhook.persists.beans.Event;
import com.plantssoil.webhook.persists.beans.EventSubscribed;
import com.plantssoil.webhook.persists.beans.Organization;
import com.plantssoil.webhook.persists.beans.Publisher;
import com.plantssoil.webhook.persists.beans.Subscriber;
import com.plantssoil.webhook.persists.beans.Webhook;
import com.plantssoil.webhook.persists.beans.WebhookLog;
import com.plantssoil.webhook.persists.beans.WebhookLogLine;
import com.plantssoil.webhook.persists.logging.PersistedLogging;

import io.netty.util.internal.ThreadLocalRandom;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JPAPersistedLoggingTest extends PersistedRegistryTestParent {
    private static TempDirectoryUtility util = new TempDirectoryUtility();

    public static void main(String[] args) throws Exception {
        JPAPersistedLoggingTest test = new JPAPersistedLoggingTest();
        JPAPersistedLoggingTest.setUpBeforeClass();
        test.test01Logging();
        JPAPersistedLoggingTest.tearDownAfterClass();
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
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_LOGGING_CONFIGURABLE, PersistedLogging.class.getName());

        try (FileOutputStream out = new FileOutputStream(util.getTempDir() + "/" + LettuceConfiguration.CONFIGURATION_FILE_NAME)) {
            p.store(out, "## No comments");
        }
        System.setProperty(LettuceConfiguration.CONF_DIRECTORY_PROPERTY_NAME, util.getTempDir());
        ConfigFactory.reload();
        ConfigurableLoader.reload();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        try (IPersistence p = IPersistenceFactory.getFactoryInstance().create()) {
            List<DataGroupSubscribed> dgss = p.createQuery(DataGroupSubscribed.class).maxResults(10000).resultList().get();
            List<EventSubscribed> sss = p.createQuery(EventSubscribed.class).maxResults(10000).resultList().get();
            List<Webhook> ws = p.createQuery(Webhook.class).maxResults(10000).resultList().get();
            List<Subscriber> ss = p.createQuery(Subscriber.class).maxResults(10000).resultList().get();
            List<DataGroup> dgs = p.createQuery(DataGroup.class).maxResults(10000).resultList().get();
            List<Event> es = p.createQuery(Event.class).maxResults(10000).resultList().get();
            List<Publisher> ps = p.createQuery(Publisher.class).maxResults(10000).resultList().get();
            List<Organization> os = p.createQuery(Organization.class).maxResults(10000).resultList().get();
            List<WebhookLog> wls = p.createQuery(WebhookLog.class).maxResults(10000).resultList().get();
            List<WebhookLogLine> wlls = p.createQuery(WebhookLogLine.class).maxResults(10000).resultList().get();
            p.remove(dgss);
            p.remove(sss);
            p.remove(ws);
            p.remove(dgs);
            p.remove(es);
            p.remove(ps);
            p.remove(ss);
            p.remove(os);
            p.remove(wlls);
            p.remove(wls);
        }
        util.removeTempDirectory();
    }

    @Test
    public void test01Logging() {
        addPublisher();
        addSubscriber();
        messageTrigger();

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try (IPersistence p = IPersistenceFactory.getFactoryInstance().create()) {
            List<WebhookLog> logs = p.createQuery(WebhookLog.class).maxResults(10000).resultList().get();
            assertEquals(30, logs.size());
            WebhookLog log = logs.get(ThreadLocalRandom.current().nextInt(logs.size() - 1));
            List<WebhookLogLine> logLines = p.createQuery(WebhookLogLine.class).filter("requestId", FilterOperator.equals, log.getRequestId()).resultList()
                    .get();
            assertTrue(logLines.size() >= 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
