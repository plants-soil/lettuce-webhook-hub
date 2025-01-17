package com.plantssoil.webhook.persists.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.IWebhook.SecurityStrategy;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;
import com.plantssoil.webhook.persists.beans.DataGroup;
import com.plantssoil.webhook.persists.beans.Event;
import com.plantssoil.webhook.persists.beans.Organization;
import com.plantssoil.webhook.persists.beans.Publisher;
import com.plantssoil.webhook.persists.beans.Subscriber;
import com.plantssoil.webhook.persists.beans.Webhook;
import com.plantssoil.webhook.persists.beans.Event.EventStatus;
import com.plantssoil.webhook.persists.beans.Organization.OrganizationStatus;
import com.plantssoil.webhook.core.Message;

import io.netty.util.internal.ThreadLocalRandom;

public class PersistedRegistryTestParent {
    private final static String EVENT_PREFIX = "test.event.type.";
    private final static String DATAGROUP_PREFIX = "test.data.group.";
    private final static String WEBHOOK_URL_PREFIX = "http://dev.e-yunyi.com:8080/webhook/test";
    private AtomicInteger messageSequence = new AtomicInteger(0);
    private AtomicInteger entitySequence = new AtomicInteger(0);
    private long startTimeMilliseconds = System.currentTimeMillis();

    private Organization createOrganizationInstance(IRegistry r) {
        int id = this.entitySequence.getAndIncrement();
        Organization o = new Organization();
        o.setOrganizationId("ORG-" + this.startTimeMilliseconds + "-" + id);
        o.setOrganizationName("Test Organization" + this.startTimeMilliseconds + "-" + id);
        o.setOrganizationStatus(OrganizationStatus.ACTIVE);
        o.setEmail("email" + this.startTimeMilliseconds + id + "@gmail.com");
        o.setCreatedBy("danialdy");
        o.setCreationTime(new Date());
        r.addOrganization(o);
        return o;
    }

    private void createPublisherInstance(IRegistry r, Organization o) {
        int id = this.entitySequence.getAndIncrement();
        Publisher publisher = new Publisher();
        publisher.setPublisherId("PUBLISHER-" + this.startTimeMilliseconds + "-" + id);
        publisher.setOrganizationId(o.getOrganizationId());
        publisher.setCreatedBy("danialdy");
        publisher.setCreationTime(new Date());
        // randomly support multi-datagroup
        if (ThreadLocalRandom.current().nextInt(1011) % 20 == 4) {
            publisher.setSupportDataGroup(true);
        } else {
            publisher.setSupportDataGroup(false);
        }
        publisher.setVersion("1.0.0");
        r.addPublisher(publisher);

        for (int i = 0; i < 10; i++) {
            r.addEvent(publisher.getPublisherId(), createEventInstance(EVENT_PREFIX + i, publisher));
        }

        if (publisher.isSupportDataGroup()) {
            for (int i = 0; i < 15; i++) {
                IDataGroup dg = createDataGroupInstance(DATAGROUP_PREFIX + i);
                r.addDataGroup(publisher.getPublisherId(), dg);
            }
        }
    }

    private DataGroup createDataGroupInstance(String dataGroup) {
        int id = this.entitySequence.getAndIncrement();
        DataGroup dg = new DataGroup();
        dg.setDataGroup(dataGroup);
        dg.setDataGroupId("PUBLISHER-" + this.startTimeMilliseconds + "-" + id);
        dg.setAccessToken("ACCESSTOKEN-" + startTimeMilliseconds + "-" + entitySequence.getAndIncrement());
        dg.setRefreshToken("REFRESHTOKEN-" + +startTimeMilliseconds + "-" + entitySequence.getAndIncrement());
        return dg;
    }

    private Event createEventInstance(String eventType, Publisher publisher) {
        int id = this.entitySequence.getAndIncrement();
        Event event = new Event();
        event.setEventId("EVENT-" + this.startTimeMilliseconds + "-" + id);
        event.setPublisherId(publisher.getPublisherId());
        event.setEventTag("test");
        event.setEventType(eventType);
        event.setContentType("application/json");
        event.setCharset("UTF-8");
        event.setCreatedBy("danialdy");
        event.setCreationTime(new Date());
        event.setEventStatus(EventStatus.PUBLISHED);
        return event;
    }

    private void createSubscriberInstance(IRegistry r, Organization organization, Publisher publisher) {
        int id = this.entitySequence.getAndIncrement();
        Subscriber subscriber = new Subscriber();
        subscriber.setSubscriberId("SUBSCRIBER-" + this.startTimeMilliseconds + "-" + id);
        subscriber.setOrganizationId(organization.getOrganizationId());
        r.addSubscriber(subscriber);
        IWebhook webhook = createWebhookInstance(publisher, subscriber);
        r.addWebhook(webhook);
        List<IEvent> events = r.findEvents(publisher.getPublisherId(), 0, 100);
        r.subscribeEvent(webhook, events.get(1));
        r.subscribeEvent(webhook, events.get(3));
        r.subscribeEvent(webhook, events.get(5));
        if (publisher.isSupportDataGroup()) {
//          List<String> dgs = publisher.findDataGroups(0, 100);
//          webhook.subscribeDataGroup(dataGroup);
        }
    }

    private Webhook createWebhookInstance(Publisher publisher, Subscriber subscriber) {
        int id = this.entitySequence.getAndIncrement();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YXBpdXNlcjE6NjY2NjY2NjY2Ng==");
        headers.put("test-header-01", "test-value-01");
        headers.put("test-header-02", "test-value-02");
        Webhook webhook = new Webhook();
        webhook.setWebhookId("WEBHOOK-" + this.startTimeMilliseconds + "-" + id);
        webhook.setSubscriberId(subscriber.getSubscriberId());
        webhook.setWebhookSecret("WEBHOOKSECRET-" + this.startTimeMilliseconds + "-" + id);
        webhook.setWebhookStatus(WebhookStatus.TEST);
        webhook.setWebhookUrl(WEBHOOK_URL_PREFIX);
        webhook.setSecurityStrategy(SecurityStrategy.TOKEN);
        webhook.setAccessToken("ACCESSTOKEN-" + this.startTimeMilliseconds + "-" + id);
        webhook.setPublisherId(publisher.getPublisherId());
        webhook.setPublisherVersion("1.0.0");
        webhook.setCustomizedHeaders(headers);
        webhook.setAppName("APPNAME-" + this.startTimeMilliseconds + "-" + id);
        webhook.setAppTag("Test");
        webhook.setDescription("DESCRIPTION-" + this.startTimeMilliseconds + "-" + id);
        webhook.setRefreshToken("REFRESHTOKEN-" + this.startTimeMilliseconds + "-" + id);
        webhook.setTrustedIps(new String[] { "192.168.0.1", "192.168.0.2" });
        webhook.setCreatedBy("danialdy");
        webhook.setCreationTime(new Date());
        return webhook;
    }

    void addPublisher() {
//		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
//		for (StackTraceElement element : elements) {
//			System.out.println(String.format("Class(%s), method(%s), line(%d)", element.getClassName(), element.getMethodName(), element.getLineNumber()));
//		}
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        IRegistry r = engine.getRegistry();
        for (int i = 0; i < 99; i++) {
            Organization o = createOrganizationInstance(r);
            createPublisherInstance(r, o);
        }

        List<IPublisher> publishers = r.findAllPublishers(0, 1000);
//		assertEquals(99, publishers.size());
        IPublisher publisher = publishers.get(ThreadLocalRandom.current().nextInt(publishers.size()));
        assertEquals(10, r.findEvents(publisher.getPublisherId(), 0, 100).size());
        if (publisher.isSupportDataGroup()) {
            assertEquals(15, r.findDataGroups(publisher.getPublisherId(), 0, 100).size());
        } else {
            assertEquals(0, r.findDataGroups(publisher.getPublisherId(), 0, 100).size());
        }
        System.out.println(String.format("Totally %d publishers created.", publishers.size()));
    }

    private IPublisher findRandomPublisher(IRegistry r) {
        List<IPublisher> publishers = r.findAllPublishers(0, 100);
        IPublisher publisher = publishers.get(ThreadLocalRandom.current().nextInt(99));
        return publisher;
    }

    void updatePublisher() {
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        IRegistry r = engine.getRegistry();
        IPublisher publisher = findRandomPublisher(r);
        printPublisher(publisher, "Publisher Before updated:");
        ((Publisher) publisher).setCreatedBy("duyong");
        ((Publisher) publisher).setCreationTime(new Date());
//        publisher.setVersion("2.0.0");
//        publisher.setSupportDataGroup(publisher.isSupportDataGroup() ? false : true);
        r.updatePublisher(publisher);
        IPublisher publisherUpdated = r.findPublisher(publisher.getPublisherId());
        printPublisher(publisherUpdated, "Publisher After updated:");
        assertEquals("duyong", ((Publisher) publisher).getCreatedBy());
    }

    private void printPublisher(IPublisher publisher, String title) {
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        IRegistry r = engine.getRegistry();
        System.out.println();
        System.out.println(title);
        System.out.println("===================================");
        System.out.println("Publisher id: " + publisher.getPublisherId());
        System.out.println("Version: " + publisher.getVersion());
        System.out.println("Created by: " + ((Publisher) publisher).getCreatedBy());
        System.out.println("Creation time: " + ((Publisher) publisher).getCreationTime());
        System.out.println("Events size: " + r.findEvents(publisher.getPublisherId(), 0, 100).size());
        System.out.println("Data groups size: " + r.findDataGroups(publisher.getPublisherId(), 0, 100).size());
    }

    void addSubscriber() {
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        IRegistry r = engine.getRegistry();
        List<IPublisher> publishers = r.findAllPublishers(0, 100);
        for (int i = 0; i < 5; i++) {
            IPublisher publisher = publishers.get(i);
            for (int j = 0; j < 3; j++) {
                Organization o = createOrganizationInstance(r);
                createSubscriberInstance(r, o, (Publisher) publisher);
            }
        }

        List<ISubscriber> subscribers = r.findAllSubscribers(0, 100);
//		assertEquals(15, subscribers.size());
        ISubscriber subscriber = subscribers.get(ThreadLocalRandom.current().nextInt(15));
        List<IWebhook> webhooks = r.findWebhooks(subscriber.getSubscriberId(), 0, 10);
        assertEquals(1, webhooks.size());
        List<IEvent> events = r.findSubscribedEvents(webhooks.get(0).getWebhookId(), 0, 10);
        assertEquals(3, events.size());
        System.out.println();
        System.out.println(String.format("Totally %d subscribers created.", subscribers.size()));
    }

    void updateSubscriber() {
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        IRegistry r = engine.getRegistry();
        List<ISubscriber> subscribers = r.findAllSubscribers(0, 100);
        ISubscriber subscriber = subscribers.get(ThreadLocalRandom.current().nextInt(15));
        List<IWebhook> webhooks = r.findWebhooks(subscriber.getSubscriberId(), 0, 10);
        String webhookId = webhooks.get(0).getWebhookId();
        printSubscriber(webhooks.get(0), "Subscriber Before updated:");
        webhooks.get(0).setWebhookSecret("UPDATED SECRET");
        webhooks.get(0).getCustomizedHeaders().remove("test-header-01");
        r.updateWebhook(webhooks.get(0));

        IWebhook webhook = r.findWebhook(webhookId);
        printSubscriber(webhook, "Subscriber After updated:");
        assertEquals("UPDATED SECRET", webhook.getWebhookSecret());
        assertEquals(3, r.findSubscribedEvents(webhook.getWebhookId(), 0, 10).size());
    }

    void removeSubscriber() {
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        IRegistry r = engine.getRegistry();
        List<ISubscriber> subscribers = r.findAllSubscribers(0, 100);
        ISubscriber subscriber = subscribers.get(ThreadLocalRandom.current().nextInt(15));
        List<IWebhook> webhooks = r.findWebhooks(subscriber.getSubscriberId(), 0, 10);
        printSubscriber(webhooks.get(0), "Subscriber Before Removed:");
        r.removeSubscriber(subscriber);
        ISubscriber subscriberRemoved = r.findSubscriber(subscriber.getSubscriberId());
        System.out.println();
        System.out.println(String.format("Subscriber removed: %s", subscriberRemoved == null ? "YES" : "NO"));
        assertNull(subscriberRemoved);
        try (IPersistence p = IPersistenceFactory.getFactoryInstance().create()) {
            IEntityQuery<Webhook> q = p.createQuery(Webhook.class);
            Webhook e = q.singleResult(webhooks.get(0).getWebhookId()).get();
            assertNull(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printSubscriber(IWebhook webhook, String title) {
        System.out.println();
        System.out.println(title);
        System.out.println("===================================");
        System.out.println("Subscriber id: " + webhook.getSubscriberId());
        System.out.println("Publisher id: " + webhook.getPublisherId());
        System.out.println("Publisher version: " + webhook.getPublisherVersion());
        System.out.println("Webhook id: " + webhook.getWebhookId());
        System.out.println("Webhook URL: " + webhook.getWebhookUrl());
        System.out.println("Webhook Secret: " + webhook.getWebhookSecret());
        System.out.println("Customized Headers: ");
        webhook.getCustomizedHeaders().forEach((k, v) -> System.out.println(String.format("%s=%s", k, v)));
    }

    void messageTrigger() {
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        IRegistry r = engine.getRegistry();
        List<ISubscriber> subscribers = r.findAllSubscribers(0, 100);

        int count = 0;
        for (int i = 0; i < 10; i++) {
            ISubscriber subscriber = subscribers.get(i);
            List<IWebhook> webhooks = r.findWebhooks(subscriber.getSubscriberId(), 0, 10);
            IWebhook webhook = webhooks.get(0);
            List<IEvent> events = r.findSubscribedEvents(webhook.getWebhookId(), 0, 100);
            for (IEvent event : events) {
                int id = this.messageSequence.getAndIncrement();
                Message message = new Message(webhook.getPublisherId(), webhook.getPublisherVersion(), event.getEventType(), "test", "application/json",
                        "UTF-8", null, "MESSAGE-" + this.startTimeMilliseconds + "-" + id,
                        "{\"data\": \"This is the test payload-" + this.startTimeMilliseconds + "-" + id + "\"}");
                engine.trigger(message);
//                System.out.println(message);
                count++;
            }
        }
        System.out.println(String.format("%d message sent.", count));
    }
}
