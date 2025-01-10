package com.plantssoil.webhook.persists.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IEntityQuery.FilterOperator;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.beans.DataGroup;
import com.plantssoil.webhook.beans.Event;
import com.plantssoil.webhook.beans.Event.EventStatus;
import com.plantssoil.webhook.beans.Organization;
import com.plantssoil.webhook.beans.Organization.OrganizationStatus;
import com.plantssoil.webhook.beans.Webhook;
import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.IWebhook.SecurityStrategy;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;
import com.plantssoil.webhook.core.Message;

import io.netty.util.internal.ThreadLocalRandom;

public class PersistedRegistryTestParent {
	private final static String EVENT_PREFIX = "test.event.type.";
	private final static String DATAGROUP_PREFIX = "test.data.group.";
	private final static String WEBHOOK_URL_PREFIX = "http://dev.e-yunyi.com:8080/webhook/test";
	private AtomicInteger messageSequence = new AtomicInteger(0);
	private AtomicInteger entitySequence = new AtomicInteger(0);
	private long startTimeMilliseconds = System.currentTimeMillis();

	private Organization createOrganizationInstance() {
		int id = this.entitySequence.getAndIncrement();
		Organization o = new Organization();
		o.setOrganizationId("ORG-" + this.startTimeMilliseconds + "-" + id);
		o.setOrganizationName("Test Organization" + this.startTimeMilliseconds + "-" + id);
		o.setOrganizationStatus(OrganizationStatus.ACTIVE);
		o.setEmail("email" + this.startTimeMilliseconds + id + "@gmail.com");
		o.setCreatedBy("danialdy");
		o.setCreationTime(new Date());
		return o;
	}

	private PersistedPublisher createPublisherInstance(Organization o) {
		int id = this.entitySequence.getAndIncrement();
		PersistedPublisher publisher = new PersistedPublisher();
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
		for (int i = 0; i < 10; i++) {
			publisher.addEvent(createEventInstance(EVENT_PREFIX + i, publisher));
		}

		if (publisher.isSupportDataGroup()) {
			for (int i = 0; i < 15; i++) {
				publisher.addDataGroup(DATAGROUP_PREFIX + i);
			}
		}
		return publisher;
	}

	private PersistedEvent createEventInstance(String eventType, PersistedPublisher publisher) {
		int id = this.entitySequence.getAndIncrement();
		PersistedEvent event = new PersistedEvent();
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

	private PersistedSubscriber createSubscriberInstance(Organization organization, PersistedPublisher publisher) {
		int id = this.entitySequence.getAndIncrement();
		PersistedSubscriber subscriber = new PersistedSubscriber();
		subscriber.setSubscriberId("SUBSCRIBER-" + this.startTimeMilliseconds + "-" + id);
		subscriber.setOrganizationId(organization.getOrganizationId());
		subscriber.addWebhook(createWebhookInstance(publisher, subscriber));
		return subscriber;
	}

	private PersistedWebhook createWebhookInstance(PersistedPublisher publisher, PersistedSubscriber subscriber) {
		int id = this.entitySequence.getAndIncrement();
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "Basic YXBpdXNlcjE6NjY2NjY2NjY2Ng==");
		headers.put("test-header-01", "test-value-01");
		headers.put("test-header-02", "test-value-02");
		PersistedWebhook webhook = new PersistedWebhook();
		webhook.setWebhookId("WEBHOOK-" + this.startTimeMilliseconds + "-" + id);
		webhook.setSubscriberId(subscriber.getSubscriberId());
		webhook.setWebhookSecret("WEBHOOKSECRET-" + this.startTimeMilliseconds + "-" + id);
		webhook.setWebhookStatus(WebhookStatus.TEST);
		webhook.setWebhookUrl(WEBHOOK_URL_PREFIX);
		webhook.setSecurityStrategy(SecurityStrategy.TOKEN);
		webhook.setAccessToken("ACCESSTOKEN-" + this.startTimeMilliseconds + "-" + id);
		webhook.setPublisherId(publisher.getPublisherId());
		webhook.setPubliserhVersion("1.0.0");
		webhook.setCustomizedHeaders(headers);
		webhook.setAppName("APPNAME-" + this.startTimeMilliseconds + "-" + id);
		webhook.setAppTag("Test");
		webhook.setDescription("DESCRIPTION-" + this.startTimeMilliseconds + "-" + id);
		webhook.setRefreshToken("REFRESHTOKEN-" + this.startTimeMilliseconds + "-" + id);
		webhook.setTrustedIps(new String[] { "192.168.0.1", "192.168.0.2" });
		webhook.setCreatedBy("danialdy");
		webhook.setCreationTime(new Date());
		List<IEvent> events = publisher.findEvents(0, 100);
		webhook.subscribeEvent(events.get(1));
		webhook.subscribeEvent(events.get(3));
		webhook.subscribeEvent(events.get(5));
		if (publisher.isSupportDataGroup()) {
//			List<String> dgs = publisher.findDataGroups(0, 100);
//			webhook.subscribeDataGroup(dataGroup);
		}
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
			Organization o = createOrganizationInstance();
			IPublisher publisher = createPublisherInstance(o);
			r.addPublisher(publisher);
		}

		List<IPublisher> publishers = r.findAllPublishers(0, 1000);
//		assertEquals(99, publishers.size());
		IPublisher publisher = publishers.get(ThreadLocalRandom.current().nextInt(publishers.size()));
		assertEquals(10, publisher.findEvents(0, 100).size());
		if (publisher.isSupportDataGroup()) {
			assertEquals(15, publisher.findDataGroups(0, 100).size());
		} else {
			assertEquals(0, publisher.findDataGroups(0, 100).size());
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
		publisher.setVersion("2.0.0");
		publisher.setSupportDataGroup(publisher.isSupportDataGroup() ? false : true);
		if (publisher.isSupportDataGroup()) {
			for (int i = 0; i < 13; i++) {
				publisher.addDataGroup("DATAGROUP(NEW)-" + ThreadLocalRandom.current().nextInt(1000));
			}
		}
		List<IEvent> es = publisher.findEvents(0, 100);
		for (int i = 0; i < 7; i++) {
			publisher.addEvent(es.get(i));
		}
		r.updatePublisher(publisher);
		IPublisher publisherUpdated = r.findPublisher(publisher.getPublisherId());
		printPublisher(publisherUpdated, "Publisher After updated:");
		assertEquals("2.0.0", publisherUpdated.getVersion());
		assertEquals(7, publisher.findEvents(0, 100).size());
		if (publisher.isSupportDataGroup()) {
			assertEquals(13, publisher.findDataGroups(0, 100).size());
		} else {
			assertEquals(0, publisher.findDataGroups(0, 100).size());
		}
	}

	void removePublisher() {
		IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
		IRegistry r = engine.getRegistry();
		IPublisher publisher = findRandomPublisher(r);
		List<IEvent> events = publisher.findEvents(0, 100);
		List<String> dataGroups = publisher.findDataGroups(0, 100);
		printPublisher(publisher, "Publisher Before removed:");
		r.removePublisher(publisher);
		IPublisher publisherRemoved = r.findPublisher(publisher.getPublisherId());
		System.out.println();
		System.out.println(String.format("Publisher removed: %s", publisherRemoved == null ? "YES" : "NO"));
		assertNull(publisherRemoved);
		try (IPersistence p = IPersistenceFactory.getFactoryInstance().create()) {
			IEntityQuery<Event> q = p.createQuery(Event.class);
			Event e = q.singleResult(events.get(3).getEventId()).get();
			assertNull(e);
			if (dataGroups.size() > 0) {
				IEntityQuery<DataGroup> qd = p.createQuery(DataGroup.class);
				DataGroup dg = qd.filter("publisherId", FilterOperator.equals, publisher.getPublisherId())
						.filter("dataGroup", FilterOperator.equals, dataGroups.get(3)).singleResult().get();
				assertNull(dg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printPublisher(IPublisher publisher, String title) {
		System.out.println();
		System.out.println(title);
		System.out.println("===================================");
		System.out.println("Publisher id: " + publisher.getPublisherId());
		System.out.println("Version: " + publisher.getVersion());
		System.out.println("Events size: " + publisher.findEvents(0, 100).size());
		System.out.println("Data groups size: " + publisher.findDataGroups(0, 100).size());
	}

	void addSubscriber() {
		IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
		IRegistry r = engine.getRegistry();
		List<IPublisher> publishers = r.findAllPublishers(0, 100);
		for (int i = 0; i < 5; i++) {
			IPublisher publisher = publishers.get(ThreadLocalRandom.current().nextInt(99));
			for (int j = 0; j < 3; j++) {
				Organization o = createOrganizationInstance();
				ISubscriber subscriber = createSubscriberInstance(o, (PersistedPublisher) publisher);
				r.addSubscriber(subscriber);
			}
		}

		List<ISubscriber> subscribers = r.findAllSubscribers(0, 100);
//		assertEquals(15, subscribers.size());
		ISubscriber subscriber = subscribers.get(ThreadLocalRandom.current().nextInt(15));
		List<IWebhook> webhooks = subscriber.findWebhooks(0, 10);
		assertEquals(1, webhooks.size());
		List<IEvent> events = webhooks.get(0).findSubscribedEvents(0, 10);
		assertEquals(3, events.size());
		System.out.println();
		System.out.println(String.format("Totally %d subscribers created.", subscribers.size()));
	}

	void updateSubscriber() {
		IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
		IRegistry r = engine.getRegistry();
		List<ISubscriber> subscribers = r.findAllSubscribers(0, 100);
		ISubscriber subscriber = subscribers.get(ThreadLocalRandom.current().nextInt(15));
		List<IWebhook> webhooks = subscriber.findWebhooks(0, 10);
		printSubscriber(webhooks.get(0), "Subscriber Before updated:");
		List<IEvent> events = webhooks.get(0).findSubscribedEvents(0, 10);
		subscriber.addWebhook(webhooks.get(0));
		webhooks.get(0).setWebhookSecret("UPDATED SECRET");
		webhooks.get(0).getCustomizedHeaders().remove("test-header-01");
		for (int i = 0; i < 2; i++) {
			webhooks.get(0).subscribeEvent(events.get(i));
		}
		r.updateSubscriber(subscriber);

		ISubscriber subscriberUpdated = r.findSubscriber(subscriber.getSubscriberId());
		webhooks = subscriberUpdated.findWebhooks(0, 10);
		printSubscriber(webhooks.get(0), "Subscriber After updated:");
		assertEquals("UPDATED SECRET", webhooks.get(0).getWebhookSecret());
		assertEquals(2, webhooks.get(0).findSubscribedEvents(0, 10).size());
	}

	void removeSubscriber() {
		IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
		IRegistry r = engine.getRegistry();
		List<ISubscriber> subscribers = r.findAllSubscribers(0, 100);
		ISubscriber subscriber = subscribers.get(ThreadLocalRandom.current().nextInt(15));
		List<IWebhook> webhooks = subscriber.findWebhooks(0, 10);
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
			ISubscriber subscriber = subscribers.get(ThreadLocalRandom.current().nextInt(subscribers.size()));
			List<IWebhook> webhooks = subscriber.findWebhooks(0, 10);
			IWebhook webhook = webhooks.get(0);
			List<IEvent> events = webhook.findSubscribedEvents(0, 100);
			for (IEvent event : events) {
				int id = this.messageSequence.getAndIncrement();
				Message message = new Message(webhook.getPublisherId(), webhook.getPublisherVersion(), event.getEventType(), "test", "application/json",
						"UTF-8", null, "MESSAGE-" + this.startTimeMilliseconds + "-" + id,
						"{\"data\": \"This is the test payload-" + this.startTimeMilliseconds + "-" + id + "\"}");
				engine.trigger(message);
				count++;
			}
		}
		System.out.println(String.format("%d message sent.", count));
	}
}
