package com.plantssoil.webhook.core;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
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
import com.plantssoil.common.test.TempDirectoryUtility;
import com.plantssoil.webhook.core.IWebhook.SecurityStrategy;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;
import com.plantssoil.webhook.core.impl.WebhookPoster;
import com.plantssoil.webhook.core.registry.InMemoryEvent;
import com.plantssoil.webhook.core.registry.InMemoryWebhook;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WebhookPosterTest {
    private static TempDirectoryUtility util = new TempDirectoryUtility();
    private final static String WEBHOOK_URL_PREFIX = "http://dev.e-yunyi.com:8080/webhook/test";
    private volatile AtomicInteger payloadId = new AtomicInteger(0);
    private volatile AtomicInteger entitySequence = new AtomicInteger(0);
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
//        p.setProperty(LettuceConfiguration.HTTPCLIENT_CONFIGURABLE, "com.plantssoil.common.httpclient.impl.OkHttpClientImpl");
//        p.setProperty(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE, MongodbPersistenceFactory.class.getName());
//        p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_URL,
//                "mongodb://lettuce:lettuce20241101@192.168.0.67:27017/?retryWrites=false&retryReads=false");
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_MAX_REQUESTS_PER_HOST, String.valueOf(15));
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
                "MESSAGE-" + testNumber + "-" + this.entitySequence.getAndIncrement(),
                "{\"data\": \"This is the test payload-" + this.testNumber + "-" + this.payloadId.getAndIncrement() + "\"}");
        return message;
    }

    private IWebhook createWebhookInstance() {
        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", "Basic YXBpdXNlcjE6NjY2NjY2NjY2Ng==");
        headers.put("test-header-01", "test-value-01");
        headers.put("test-header-02", "test-value-02");
        IWebhook webhook = new InMemoryWebhook();
        webhook.setWebhookId("WEBHOOK-" + testNumber + "-" + this.entitySequence.getAndIncrement());
        webhook.setWebhookSecret("WEBHOOKSECRET-" + testNumber + "-" + this.entitySequence.getAndIncrement());
        webhook.setWebhookStatus(WebhookStatus.TEST);
        webhook.setWebhookUrl(WEBHOOK_URL_PREFIX);
        webhook.setSecurityStrategy(SecurityStrategy.TOKEN);
        webhook.setAccessToken("ACCESSTOKEN-" + testNumber + "-" + this.entitySequence.getAndIncrement());
        webhook.setPublisherId("publisher-id-0001");
        webhook.setPubliserhVersion("1.0.0");
        webhook.setCustomizedHeaders(headers);
        webhook.subscribeEvent(createEventInstance("test.event.type.001"));
        return webhook;
    }

    private IEvent createEventInstance(String eventType) {
        IEvent event = new InMemoryEvent();
        event.setEventId("EVENT-" + testNumber + "-" + this.entitySequence.getAndIncrement());
        event.setEventTag("test");
        event.setEventType(eventType);
        event.setContentType("application/json");
        event.setCharset("UTF-8");
        return event;
    }

    private int randomPostWebhook(ExecutorService e, IWebhook webhook) {
        final int count = ThreadLocalRandom.current().nextInt(5);
        e.submit(() -> {
            for (int i = 0; i < count; i++) {
                Message message = createMessageInstance();
                WebhookPoster.getInstance().postWebhook(message, webhook);
            }
        });
        try {
            Thread.sleep(1 * 100);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        return count;
    }

    @Test
    public void test01PostWebhook() {
        IWebhook webhook = createWebhookInstance();
        ExecutorService e = Executors.newFixedThreadPool(1);
        int totally = 0;
        for (int i = 0; i < 10; i++) {
            int count = randomPostWebhook(e, webhook);
            totally += count;
            System.out.println(String.format("Looped %d, Randomly sent %d messages, totally %d.", i, count, totally));
        }
        System.out.println(String.format("Sent %d messages completed", totally));
        e.shutdown();
    }

}
