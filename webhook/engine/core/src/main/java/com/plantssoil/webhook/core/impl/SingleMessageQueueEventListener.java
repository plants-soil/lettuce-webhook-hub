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
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;

/**
 * The message queue event listener, triggered when new message comes<br/>
 * 
 * @author danialdy
 * @Date 5 Dec 2024 3:32:14 pm
 */
class SingleMessageQueueEventListener implements IMessageListener<Message> {
	/**
	 * Key - publisherKey (publisherId + version + dataGruop), value - webhooks
	 * bound on publisherKey
	 */
	private Map<PublisherKey, List<IWebhook>> webhooks = new ConcurrentHashMap<>();
	/**
	 * Key - Webhook id, value - event type subscribed by the webhook
	 */
	private Map<String, Set<String>> eventsSubscribed = new ConcurrentHashMap<>();
	/**
	 * Key - Webhook id, value - data group subscribed by the webhook
	 */
	private Map<String, Set<String>> dataGroupsSubscribed = new ConcurrentHashMap<>();

	/**
	 * Add webhook into the listener
	 * 
	 * @param publisherKey the publisher key which add webhooks on
	 * @param webhook      the webhook to add
	 */
	void addSubscriber(PublisherKey publisherKey, IWebhook webhook) {
		getWebhooks(publisherKey).add(webhook);
	}

	private List<IWebhook> getWebhooks(PublisherKey publisherKey) {
		List<IWebhook> webhookList = this.webhooks.get(publisherKey);
		if (webhookList == null) {
			synchronized (publisherKey.toString().intern()) {
				webhookList = this.webhooks.get(publisherKey);
				if (webhookList == null) {
					webhookList = new ArrayList<>();
					this.webhooks.put(publisherKey, webhookList);
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
		for (Map.Entry<PublisherKey, List<IWebhook>> entry : this.webhooks.entrySet()) {
			List<IWebhook> webhooks = entry.getValue();
			List<Integer> indexes = new ArrayList<Integer>();
			for (int i = 0; i < webhooks.size(); i++) {
				if (webhooks.get(i).getSubscriberId().equals(subscriberId)) {
					indexes.add(i);
				}
			}
			for (Integer index : indexes) {
				int i = index.intValue();
				String webhookId = webhooks.get(i).getWebhookId();
				this.eventsSubscribed.remove(webhookId);
				this.dataGroupsSubscribed.remove(webhookId);
				webhooks.remove(i);
			}
		}
	}

	@Override
	public void onMessage(Message message, String consumerId) {
		PublisherKey key = new PublisherKey(message.getPublisherId(), message.getVersion(), message.getDataGroup());
		List<IWebhook> webhooks = getWebhooks(key);
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
