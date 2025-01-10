package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;

/**
 * The message queue event listener, triggered when new message comes<br/>
 * 
 * @author danialdy
 * @Date 5 Dec 2024 3:32:14 pm
 */
class MultiMessageQueueEventListener implements IMessageListener<Message> {
	private Map<String, List<IWebhook>> webhooks = new ConcurrentHashMap<>();
	/**
	 * Key - Webhook id, value - event type subscribed by the webhook
	 */
	private Map<String, Set<String>> eventsSubscribed = new ConcurrentHashMap<>();
	/**
	 * Key - Webhook id, value - data group subscribed by the webhook
	 */
	private Map<String, Set<String>> dataGroupsSubscribed = new ConcurrentHashMap<>();

	/**
	 * Add subscriber on this listener
	 * 
	 * @param subscriber the subscriber to add
	 * @param webhook    the webhook to add
	 */
	void addSubscriber(ISubscriber subscriber, IWebhook webhook) {
		getWebhooks(subscriber.getSubscriberId()).add(webhook);
	}

	private List<IWebhook> getWebhooks(String subscriberId) {
		List<IWebhook> webhookList = this.webhooks.get(subscriberId);
		if (webhookList == null) {
			synchronized (subscriberId) {
				webhookList = this.webhooks.get(subscriberId);
				if (webhookList == null) {
					webhookList = new ArrayList<>();
					this.webhooks.put(subscriberId, webhookList);
				}
			}
		}
		return webhookList;
	}

	private Set<String> getEventsSubscribed(IWebhook webhook) {
		String webhookId = webhook.getWebhookId();
		Set<String> es = this.eventsSubscribed.get(webhookId);
		if (es == null) {
			synchronized (webhookId.intern()) {
				es = this.eventsSubscribed.get(webhookId);
				if (es == null) {
					es = loadWebhookEventsSubscribed(webhook);
					this.eventsSubscribed.put(webhook.getWebhookId(), es);
				}
			}
		}
		return es;
	}

	private Set<String> loadWebhookEventsSubscribed(IWebhook webhook) {
		Set<String> es = new HashSet<>();
		int page = 0, pageSize = 20;
		List<IEvent> events = webhook.findSubscribedEvents(page, pageSize);
		while (events.size() > 0) {
			for (IEvent event : events) {
				es.add(event.getEventType());
			}
			if (events.size() < pageSize) {
				break;
			}
			page++;
			events = webhook.findSubscribedEvents(page, pageSize);
		}
		return es;
	}

	private Set<String> getDataGroupsSubscribed(IWebhook webhook) {
		String webhookId = webhook.getWebhookId();
		Set<String> dgs = this.dataGroupsSubscribed.get(webhookId);
		if (dgs == null) {
			synchronized (webhookId.intern()) {
				dgs = this.dataGroupsSubscribed.get(webhookId);
				if (dgs == null) {
					dgs = loadWebhookDataGroupsSubscribed(webhook);
					this.dataGroupsSubscribed.put(webhook.getWebhookId(), dgs);
				}
			}
		}
		return dgs;
	}

	private Set<String> loadWebhookDataGroupsSubscribed(IWebhook webhook) {
		Set<String> dgs = new HashSet<>();
		int page = 0, pageSize = 20;
		List<IDataGroup> dataGroups = webhook.findSubscribedDataGroups(page, pageSize);
		while (dataGroups.size() > 0) {
			for (IDataGroup dataGroup : dataGroups) {
				dgs.add(dataGroup.getDataGroup());
			}
			if (dataGroups.size() < pageSize) {
				break;
			}
			page++;
			dataGroups = webhook.findSubscribedDataGroups(page, pageSize);
		}
		return dgs;
	}

	/**
	 * Remove subscriber from this listener
	 * 
	 * @param subscriberId subscriber id to remove
	 */
	void removeSubscriber(String subscriberId) {
		this.webhooks.remove(subscriberId);
	}

	@Override
	public void onMessage(Message message, String consumerId) {
		for (List<IWebhook> webhooks : this.webhooks.values()) {
			for (IWebhook webhook : webhooks) {
				if (!getEventsSubscribed(webhook).contains(message.getEventType())) {
					continue;
				}
				if (message.getDataGroup() == null) {
					WebhookPoster.getInstance().postWebhook(message, webhook);
				} else if (getDataGroupsSubscribed(webhook).contains(message.getDataGroup())) {
					WebhookPoster.getInstance().postWebhook(message, webhook);
				}
			}
		}
	}

}
