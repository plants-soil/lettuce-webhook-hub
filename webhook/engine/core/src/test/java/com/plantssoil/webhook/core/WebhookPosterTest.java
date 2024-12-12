package com.plantssoil.webhook.core;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.plantssoil.webhook.core.impl.SimpleEvent;
import com.plantssoil.webhook.core.impl.SimpleWebhook;
import com.plantssoil.webhook.core.impl.WebhookPoster;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WebhookPosterTest {
    private static TempDirectoryUtility util = new TempDirectoryUtility();
    private final static String WEBHOOK_URL_PREFIX = "http://dev.e-yunyi.com:8080/api/test";
    private volatile AtomicInteger payloadId = new AtomicInteger(0);
    private long testNumber = System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
        WebhookPosterTest test = new WebhookPosterTest();
        WebhookPosterTest.setUpBeforeClass();
        test.test01PostWebhook();
        Thread.sleep(1000 * 10);
        WebhookPosterTest.tearDownAfterClass();
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Thread.sleep(1000);
        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE, MongodbPersistenceFactory.class.getName());
        p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_URL,
                "mongodb://lettuce:lettuce20241101@192.168.0.67:27017/?retryWrites=false&retryReads=false");
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_CORE_POOL_SIZE, String.valueOf(111));
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_MAXIMUM_POOL_SIZE, String.valueOf(222));
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_WORK_QUEUE_CAPACITY, String.valueOf(100000));
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_RETRY_QUEUE_CAPACITY5, String.valueOf(100002));
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_RETRY_QUEUE_CAPACITY30, String.valueOf(100003));

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

    private Message createMessageInstance() {
        Message message = new Message("publisher-id-0001", "1.0.0", "test.event.type.001", "test", "application/json", "UTF-8", null,
                EntityIdUtility.getInstance().generateUniqueId(),
                "{\"data\": \"This is the test payload-" + this.testNumber + "-" + this.payloadId.getAndIncrement() + "\"}");
        return message;
    }

    private IWebhook createWebhookInstance() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YXBpdXNlcjE6NjY2NjY2NjY2Ng==");
        headers.put("test-header-01", "test-value-01");
        headers.put("test-header-02", "test-value-02");
        IWebhook webhook = new SimpleWebhook();
        webhook.setWebhookId(EntityIdUtility.getInstance().generateUniqueId());
        webhook.setWebhookSecret(EntityIdUtility.getInstance().generateUniqueId());
        webhook.setWebhookStatus(WebhookStatus.TEST);
        webhook.setWebhookUrl(WEBHOOK_URL_PREFIX);
        webhook.setSecurityStrategy(SecurityStrategy.TOKEN);
        webhook.setAccessToken(EntityIdUtility.getInstance().generateUniqueId());
        webhook.setPublisherId("publisher-id-0001");
        webhook.setPubliserhVersion("1.0.0");
        webhook.setCustomizedHeaders(headers);
        webhook.subscribeEvent(createEventInstance("test.event.type.001"));
        return webhook;
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

    @Test
    public void test01PostWebhook() {
        IWebhook webhook = createWebhookInstance();
        for (int i = 0; i < 20; i++) {
            CompletableFuture.runAsync(() -> {
                Message message = createMessageInstance();
                WebhookPoster.getInstance().postWebhook(message, webhook);
            });
        }
    }

}
