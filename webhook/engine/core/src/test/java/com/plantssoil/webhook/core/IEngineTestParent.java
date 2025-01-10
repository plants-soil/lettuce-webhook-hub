package com.plantssoil.webhook.core;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.plantssoil.webhook.core.IWebhook.SecurityStrategy;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;
import com.plantssoil.webhook.core.registry.InMemoryDataGroup;
import com.plantssoil.webhook.core.registry.InMemoryEvent;
import com.plantssoil.webhook.core.registry.InMemoryPublisher;
import com.plantssoil.webhook.core.registry.InMemorySubscriber;
import com.plantssoil.webhook.core.registry.InMemoryWebhook;

import io.netty.util.internal.ThreadLocalRandom;

public class IEngineTestParent {
    private final static String EVENT_PREFIX = "test.event.type.";
    private final static String DATAGROUP_PREFIX = "test.data.group.";
    private final static String WEBHOOK_URL_PREFIX = "http://dev.e-yunyi.com:8080/webhook/test";
    private AtomicInteger messageSequence = new AtomicInteger(0);
    private AtomicInteger entitySequence = new AtomicInteger(0);
    private long startTimeMilliseconds = System.currentTimeMillis();

    public void testGetVersion() {
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        assertEquals("1.0.0", engine.getVersion());
    }

    public void testGetRegistry() {
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        IRegistry registry = engine.getRegistry();
        addPublishersAndSubscribers(registry);
    }

    private void addPublishersAndSubscribers(IRegistry registry) {
        for (int i = 0; i < 100; i++) {
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
        IPublisher publisher = new InMemoryPublisher();
        publisher.setPublisherId("PUBLISHER-" + startTimeMilliseconds + "-" + entitySequence.getAndIncrement());
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
        IEvent event = new InMemoryEvent();
        event.setEventId("EVENT-" + startTimeMilliseconds + "-" + entitySequence.getAndIncrement());
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
        ISubscriber subscriber = new InMemorySubscriber();
        subscriber.setSubscriberId("SUBSCRIBER-" + startTimeMilliseconds + "-" + entitySequence.getAndIncrement());
        subscriber.addWebhook(createWebhookInstance(publisher, subscriber));
        return subscriber;
    }

    private IWebhook createWebhookInstance(IPublisher publisher, ISubscriber subscriber) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YXBpdXNlcjE6NjY2NjY2NjY2Ng==");
        headers.put("test-header-01", "test-value-01");
        headers.put("test-header-02", "test-value-02");
        IWebhook webhook = new InMemoryWebhook();
        webhook.setWebhookId("WEBHOOK-" + startTimeMilliseconds + "-" + entitySequence.getAndIncrement());
        webhook.setWebhookSecret("WEBHOOKSECRET-" + startTimeMilliseconds + "-" + entitySequence.getAndIncrement());
        webhook.setWebhookStatus(WebhookStatus.TEST);
        webhook.setWebhookUrl(WEBHOOK_URL_PREFIX);
        webhook.setSecurityStrategy(SecurityStrategy.TOKEN);
        webhook.setAccessToken("ACCESSTOKEN-" + startTimeMilliseconds + "-" + entitySequence.getAndIncrement());
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
        IDataGroup dg = new InMemoryDataGroup();
        dg.setDataGroup(dataGroup);
        dg.setAccessToken("ACCESSTOKEN-" + startTimeMilliseconds + "-" + entitySequence.getAndIncrement());
        dg.setRefreshToken("REFRESHTOKEN-" + +startTimeMilliseconds + "-" + entitySequence.getAndIncrement());
        return dg;
    }

    public void testTrigger(int publisherCount, int messagesPerPublisher) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        final IRegistry registry = engine.getRegistry();
        ExecutorService es = Executors.newFixedThreadPool(1);
        for (int i = 0; i < publisherCount; i++) {
            es.submit(() -> {
                List<IPublisher> publishers = registry.findAllPublishers(ThreadLocalRandom.current().nextInt(publisherCount), 1);
                for (int j = 0; j < messagesPerPublisher; j++) {
                    Message message = new Message(publishers.get(0).getPublisherId(), "1.0.0", EVENT_PREFIX + 3, "test", "application/json", "UTF-8", null,
                            "MESSAGE-" + startTimeMilliseconds + "-" + entitySequence.getAndIncrement(),
                            "{\"data\": \"This is the test payload-" + startTimeMilliseconds + "-" + messageSequence.getAndIncrement() + "\"}");
                    engine.trigger(message);
                }
            });
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("%d message sent.", this.messageSequence.get()));
    }

}
