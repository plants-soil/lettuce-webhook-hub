package com.plantssoil.webhook.core;

import static org.junit.Assert.assertEquals;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.EntityIdUtility;
import com.plantssoil.common.persistence.mongodb.MongodbPersistenceFactory;
import com.plantssoil.common.test.TempDirectoryUtility;
import com.plantssoil.webhook.core.IWebhook.SecurityStrategy;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;
import com.plantssoil.webhook.core.impl.MessageQueueEngineFactory;
import com.plantssoil.webhook.core.impl.SimpleDataGroup;
import com.plantssoil.webhook.core.impl.SimpleEvent;
import com.plantssoil.webhook.core.impl.SimplePublisher;
import com.plantssoil.webhook.core.impl.SimpleSubscriber;
import com.plantssoil.webhook.core.impl.SimpleWebhook;

import io.netty.util.internal.ThreadLocalRandom;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IEngineTest {
    private static TempDirectoryUtility util = new TempDirectoryUtility();
    private final static String EVENT_PREFIX = "test.event.type.";
    private final static String DATAGROUP_PREFIX = "test.data.group.";
    private final static String WEBHOOK_URL_PREFIX = "https://webhook.site/";

    public static void main(String[] args) throws Exception {
        IEngineTest.setUpBeforeClass();
        IEngineTest test = new IEngineTest();
        test.test01GetVersion();
        test.test02GetRegistry();
        test.test03Trigger();
        Thread.sleep(10000);
        IEngineTest.tearDownAfterClass();
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Thread.sleep(1000);
        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE, MongodbPersistenceFactory.class.getName());
        p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_URL,
                "mongodb://lettuce:lettuce20241101@192.168.0.67:27017/?retryWrites=false&retryReads=false");
//        p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_FACTORY_CONFIGURABLE, MessageServiceFactory.class.getName());
        p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_FACTORY_CONFIGURABLE, com.plantssoil.common.mq.rabbit.MessageServiceFactory.class.getName());
        p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_URI, "amqp://lettuce:lettuce20241101@192.168.0.67:5672/lettuce");
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_VERSION, "1.0.0");
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_FACTORY_CONFIGURABLE, MessageQueueEngineFactory.class.getName());
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_CORE_POOL_SIZE, String.valueOf(111));
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_MAXIMUM_POOL_SIZE, String.valueOf(222));
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_WORK_QUEUE_CAPACITY, String.valueOf(1001));
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_RETRY_QUEUE_CAPACITY5, String.valueOf(1002));
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_RETRY_QUEUE_CAPACITY30, String.valueOf(1003));

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

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test01GetVersion() {
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        assertEquals("1.0.0", engine.getVersion());
    }

    @Test
    public void test02GetRegistry() {
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        IRegistry registry = engine.getRegistry();
        addPublishersAndSubscribers(registry);
    }

    private void addPublishersAndSubscribers(IRegistry registry) {
        for (int i = 0; i < 1011; i++) {
            IPublisher publisher = createPublisherInstance();
            registry.addPublisher(publisher);
            // randomly don't have subscriber for some publisher
            if (ThreadLocalRandom.current().nextInt(1011) % 10 == 4) {
                continue;
            }
            addSubscribers(registry, publisher);
        }
    }

    private IPublisher createPublisherInstance() {
        IPublisher publisher = new SimplePublisher();
        publisher.setPublisherId(EntityIdUtility.getInstance().generateUniqueId());
        // randomly support multi-datagroup
        if (ThreadLocalRandom.current().nextInt(1011) % 20 == 4) {
            publisher.setSupportDataGroup(false);
        } else {
            publisher.setSupportDataGroup(false);
        }
        publisher.setVersion("1.0.0");
        for (int i = 0; i < 10; i++) {
            publisher.addEvent(createEventInstance(EVENT_PREFIX + i));
        }

        if (publisher.isSupportDataGroup()) {
            for (int i = 0; i < 15; i++) {
                publisher.addDataGroup(DATAGROUP_PREFIX + i);
            }
        }
        return publisher;
    }

    private IEvent createEventInstance(String eventType) {
        IEvent event = new SimpleEvent();
        event.setEventId(EntityIdUtility.getInstance().generateUniqueId());
        event.setEventTag("test");
        event.setEventType(eventType);
        event.setContentType("application/json");
        event.setCharset("UTF-8");
        return event;
    }

    private void addSubscribers(IRegistry registry, IPublisher publisher) {
        for (int i = 0; i < 3; i++) {
            ISubscriber subscriber = createSubscriberInstance(publisher);
            registry.addSubscriber(subscriber);
        }
    }

    private ISubscriber createSubscriberInstance(IPublisher publisher) {
        ISubscriber subscriber = new SimpleSubscriber();
        subscriber.setSubscriberId(EntityIdUtility.getInstance().generateUniqueId());
        subscriber.addWebhook(createWebhookInstance(publisher, subscriber));
        return subscriber;
    }

    private IWebhook createWebhookInstance(IPublisher publisher, ISubscriber subscriber) {
        Map<String, String> headers = new HashMap<>();
        headers.put("test-header-01", "test-value-01");
        headers.put("test-header-02", "test-value-02");
        IWebhook webhook = new SimpleWebhook();
        webhook.setWebhookId(EntityIdUtility.getInstance().generateUniqueId());
        webhook.setWebhookSecret(EntityIdUtility.getInstance().generateUniqueId());
        webhook.setWebhookStatus(WebhookStatus.TEST);
        webhook.setWebhookUrl(WEBHOOK_URL_PREFIX + UUID.randomUUID());
        webhook.setSecurityStrategy(SecurityStrategy.TOKEN);
        webhook.setAccessToken(EntityIdUtility.getInstance().generateUniqueId());
        webhook.setPublisherId(publisher.getPublisherId());
        webhook.setPubliserhVersion("1.0.0");
        webhook.setCustomizedHeaders(headers);
        List<IEvent> events = publisher.findEvents(0, 100);
        webhook.subscribeEvent(events.get(1));
        webhook.subscribeEvent(events.get(3));
        webhook.subscribeEvent(events.get(5));
        if (publisher.isSupportDataGroup()) {
            List<String> dgs = publisher.findDataGroups(0, 100);
            IDataGroup dg = createDataGroupInstance(dgs.get(9));
            webhook.subscribeDataGroup(dg);
        }
        return webhook;
    }

    private IDataGroup createDataGroupInstance(String dataGroup) {
        IDataGroup dg = new SimpleDataGroup();
        dg.setDataGroup(dataGroup);
        dg.setAccessToken(EntityIdUtility.getInstance().generateUniqueId());
        dg.setRefreshToken(EntityIdUtility.getInstance().generateUniqueId());
        return dg;
    }

    @Test
    public void test03Trigger() {
        final IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        final IRegistry registry = engine.getRegistry();
        for (int i = 0; i < 5; i++) {
            CompletableFuture.runAsync(() -> {
                List<IPublisher> publishers = registry.findAllPublishers(ThreadLocalRandom.current().nextInt(1011), 1);
                Message message = new Message(publishers.get(0).getPublisherId(), "1.0.0", EVENT_PREFIX + 3, "test", "application/json", "UTF-8", null,
                        EntityIdUtility.getInstance().generateUniqueId(), "This is the test payload");
                engine.trigger(message);
            });
        }
    }

}
