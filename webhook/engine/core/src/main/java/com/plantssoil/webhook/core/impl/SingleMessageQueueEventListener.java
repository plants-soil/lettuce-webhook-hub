package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IRegistry;
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
     * Has consumers for the specific publisher key
     * 
     * @param key the publisher key
     * @return true - has consumers, false - has no consumers
     */
    boolean hasConsumers(PublisherKey key) {
        return this.webhooks.containsKey(key);
    }

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

    private Set<String> getEventsSubscribed(String webhookId) {
        Set<String> es = this.eventsSubscribed.get(webhookId);
        if (es == null) {
            synchronized (webhookId.intern()) {
                es = this.eventsSubscribed.get(webhookId);
                if (es == null) {
                    es = loadWebhookEventsSubscribed(webhookId);
                    this.eventsSubscribed.put(webhookId, es);
                }
            }
        }
        return es;
    }

    private Set<String> loadWebhookEventsSubscribed(String webhookId) {
        Set<String> es = new HashSet<>();
        int page = 0, pageSize = 20;
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        List<IEvent> events = r.findSubscribedEvents(webhookId, page, pageSize);
        while (events.size() > 0) {
            for (IEvent event : events) {
                es.add(event.getEventType());
            }
            if (events.size() < pageSize) {
                break;
            }
            page++;
            events = r.findSubscribedEvents(webhookId, page, pageSize);
        }
        return es;
    }

    private Set<String> getDataGroupsSubscribed(String webhookId) {
        Set<String> dgs = this.dataGroupsSubscribed.get(webhookId);
        if (dgs == null) {
            synchronized (webhookId.intern()) {
                dgs = this.dataGroupsSubscribed.get(webhookId);
                if (dgs == null) {
                    dgs = loadWebhookDataGroupsSubscribed(webhookId);
                    this.dataGroupsSubscribed.put(webhookId, dgs);
                }
            }
        }
        return dgs;
    }

    private Set<String> loadWebhookDataGroupsSubscribed(String webhookId) {
        Set<String> dgs = new HashSet<>();
        int page = 0, pageSize = 20;
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        List<IDataGroup> dataGroups = r.findSubscribedDataGroups(webhookId, page, pageSize);
        while (dataGroups.size() > 0) {
            for (IDataGroup dataGroup : dataGroups) {
                dgs.add(dataGroup.getDataGroup());
            }
            if (dataGroups.size() < pageSize) {
                break;
            }
            page++;
            dataGroups = r.findSubscribedDataGroups(webhookId, page, pageSize);
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
                if (Objects.equals(webhooks.get(i).getSubscriberId(), subscriberId)) {
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

    void removeWebhook(IWebhook webhook) {
        String webhookId = webhook.getWebhookId();
        for (Map.Entry<PublisherKey, List<IWebhook>> entry : this.webhooks.entrySet()) {
            List<IWebhook> webhooks = entry.getValue();
            List<Integer> indexes = new ArrayList<Integer>();
            for (int i = 0; i < webhooks.size(); i++) {
                if (Objects.equals(webhooks.get(i).getWebhookId(), webhookId)) {
                    indexes.add(i);
                }
            }
            for (Integer index : indexes) {
                int i = index.intValue();
                webhooks.remove(i);
            }
        }
        this.eventsSubscribed.remove(webhookId);
        this.dataGroupsSubscribed.remove(webhookId);
    }

    void addDataGroupSubscribed(String webhookId, String dataGroupName) {
        getDataGroupsSubscribed(webhookId).add(dataGroupName);
    }

    void removeDataGroupSubscribed(String webhookId, String dataGroupName) {
        getDataGroupsSubscribed(webhookId).remove(dataGroupName);
    }

    void addEventsSubscribed(String webhookId, List<IEvent> events) {
        Set<String> eventsSubscribed = getEventsSubscribed(webhookId);
        for (IEvent event : events) {
            eventsSubscribed.add(event.getEventType());
        }
    }

    void removeEventsSubscribed(String webhookId, List<IEvent> events) {
        Set<String> eventsSubscribed = getEventsSubscribed(webhookId);
        for (IEvent event : events) {
            eventsSubscribed.remove(event.getEventType());
        }
    }

    @Override
    public void onMessage(Message message, String consumerId) {
        PublisherKey key = new PublisherKey(message.getPublisherId(), message.getVersion(), message.getDataGroup());
        List<IWebhook> webhooks = getWebhooks(key);
        for (IWebhook webhook : webhooks) {
            String webhookId = webhook.getWebhookId();
            if (!getEventsSubscribed(webhookId).contains(message.getEventType())) {
                continue;
            }
            if (message.getDataGroup() == null) {
                WebhookPoster.getInstance().postWebhook(message, webhook);
            } else if (getDataGroupsSubscribed(webhookId).contains(message.getDataGroup())) {
                WebhookPoster.getInstance().postWebhook(message, webhook);
            }
        }
    }

}
