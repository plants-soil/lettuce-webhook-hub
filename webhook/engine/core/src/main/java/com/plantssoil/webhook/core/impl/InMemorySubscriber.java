package com.plantssoil.webhook.core.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;

/**
 * The subscriber to receive message and handle message
 * 
 * @author danialdy
 * @Date 3 Dec 2024 4:43:16 pm
 */
class InMemorySubscriber implements Subscriber<Message> {
	private final static Logger LOGGER = LoggerFactory.getLogger(InMemorySubscriber.class.getName());
	private IWebhook webhook;
	private Subscription subscription;
	/**
	 * event type subscribed by the webhook
	 */
	private Set<String> eventsSubscribed;
	/**
	 * ata group subscribed by the webhook
	 */
	private Set<String> dataGroupsSubscribed;

	InMemorySubscriber(IWebhook webhook) {
		super();
		this.webhook = webhook;
	}

	void unsubscribe() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Webhook subscriber (publisherId: %s, version: %s) unsubscribed.", this.webhook.getPublisherId(),
					this.webhook.getPublisherVersion()));
		}
		this.subscription.cancel();
	}

	@Override
	public void onSubscribe(Subscription subscription) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Webhook subscriber (publisherId: %s, version: %s) subscribed.", this.webhook.getPublisherId(),
					this.webhook.getPublisherVersion()));
		}
		this.subscription = subscription;
		this.subscription.request(1);
	}

	private Set<String> getEventsSubscribed() {
		if (this.eventsSubscribed == null) {
			synchronized (this) {
				this.eventsSubscribed = loadWebhookEventsSubscribed();
			}
		}
		return this.eventsSubscribed;
	}

	private Set<String> loadWebhookEventsSubscribed() {
		Set<String> es = new HashSet<>();
		int page = 0, pageSize = 20;
		List<IEvent> events = this.webhook.findSubscribedEvents(page, pageSize);
		while (events.size() > 0) {
			for (IEvent event : events) {
				es.add(event.getEventType());
			}
			if (events.size() < pageSize) {
				break;
			}
			page++;
			events = this.webhook.findSubscribedEvents(page, pageSize);
		}
		return es;
	}

	private Set<String> getDataGroupsSubscribed() {
		if (this.dataGroupsSubscribed == null) {
			synchronized (this) {
				this.dataGroupsSubscribed = loadWebhookDataGroupsSubscribed();
			}
		}
		return this.dataGroupsSubscribed;
	}

	private Set<String> loadWebhookDataGroupsSubscribed() {
		Set<String> dgs = new HashSet<>();
		int page = 0, pageSize = 20;
		List<IDataGroup> dataGroups = this.webhook.findSubscribedDataGroups(page, pageSize);
		while (dataGroups.size() > 0) {
			for (IDataGroup dataGroup : dataGroups) {
				dgs.add(dataGroup.getDataGroup());
			}
			if (dataGroups.size() < pageSize) {
				break;
			}
			page++;
			dataGroups = this.webhook.findSubscribedDataGroups(page, pageSize);
		}
		return dgs;
	}

	@Override
	public void onNext(Message item) {
		if (!getEventsSubscribed().contains(item.getEventType())) {
			this.subscription.request(1);
			return;
		}
		if (item.getDataGroup() == null) {
			WebhookPoster.getInstance().postWebhook(item, this.webhook);
		} else if (getDataGroupsSubscribed().contains(item.getDataGroup())) {
			WebhookPoster.getInstance().postWebhook(item, this.webhook);
		}
		this.subscription.request(1);
	}

	@Override
	public void onError(Throwable throwable) {
		LOGGER.error(throwable.getMessage());
	}

	@Override
	public void onComplete() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Webhook subscriber (publisherId: %s, version: %s) completed.", this.webhook.getPublisherId(),
					this.webhook.getPublisherVersion()));
		}
		this.webhook = null;
	}

}
