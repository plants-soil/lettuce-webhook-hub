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
class MultiMessageQueueEventListener implements IMessageListener<Message> {
    /**
     * Key - Subscriber id, value - webhook list
     */
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
     * Add subscriber on this listener, ignore if already exists
     * 
     * @param webhook the webhook to add
     */
    void addSubscriber(IWebhook webhook) {
        List<IWebhook> ws = getWebhooks(webhook.getSubscriberId());
        boolean exists = false;
        for (IWebhook w : ws) {
            if (Objects.equals(w.getWebhookId(), webhook.getWebhookId())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            ws.add(webhook);
        }
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
     * Remove subscriber from this listener, belongs (webhooks, subscribed event,
     * data groups) will all be cleared
     * 
     * @param subscriberId subscriber id to remove
     */
    void removeSubscriber(String subscriberId) {
        List<IWebhook> ws = getWebhooks(subscriberId);
        for (IWebhook w : ws) {
            this.eventsSubscribed.remove(w.getWebhookId());
            this.dataGroupsSubscribed.remove(w.getWebhookId());
        }
        this.webhooks.remove(subscriberId);
    }

    /**
     * Remove subscriber from this listener, belongs (webhooks, subscribed event,
     * data groups) will all be cleared
     * 
     * @param webhook webhook id to remove
     */
    void removeWebhook(IWebhook webhook) {
        List<IWebhook> ws = getWebhooks(webhook.getSubscriberId());
        ArrayList<Integer> existedIndexes = new ArrayList<>();
        for (int i = 0; i < ws.size(); i++) {
            if (Objects.equals(ws.get(i).getWebhookId(), webhook.getWebhookId())) {
                existedIndexes.add(i);
            }
        }
        for (Integer existedIndex : existedIndexes) {
            ws.remove(existedIndex.intValue());
        }
        this.eventsSubscribed.remove(webhook.getWebhookId());
        this.dataGroupsSubscribed.remove(webhook.getWebhookId());
    }

    /**
     * Add subscribed data group into this listener
     * 
     * @param webhookId     the webhook id which data group belongs to
     * @param dataGroupName data group name to subscribe
     */
    void addDataGroupSubscribed(String webhookId, String dataGroupName) {
        getDataGroupsSubscribed(webhookId).add(dataGroupName);
    }

    /**
     * Remove subscribed data group from this listener
     * 
     * @param webhookId     the webhook id which data group belongs to
     * @param dataGroupName data group name to unsubscribe
     */
    void removeDataGroupSubscribed(String webhookId, String dataGroupName) {
        getDataGroupsSubscribed(webhookId).remove(dataGroupName);
    }

    /**
     * Add subscribed events into this listener
     * 
     * @param webhookId the webhook id which events belong to
     * @param events    the events to subscribe
     */
    void addEventsSubscribed(String webhookId, List<IEvent> events) {
        Set<String> eventsSubscribed = getEventsSubscribed(webhookId);
        for (IEvent event : events) {
            eventsSubscribed.add(event.getEventType());
        }
    }

    /**
     * Remove subscribed events from this listener
     * 
     * @param webhookId the webhook id which events belong to
     * @param events    the events to unsubscribe
     */
    void removeEventsSubscribed(String webhookId, List<IEvent> events) {
        Set<String> eventsSubscribed = getEventsSubscribed(webhookId);
        for (IEvent event : events) {
            eventsSubscribed.remove(event.getEventType());
        }
    }

    @Override
    public void onMessage(Message message, String consumerId) {
        for (List<IWebhook> webhooks : this.webhooks.values()) {
            for (IWebhook webhook : webhooks) {
                String webhookId = webhook.getWebhookId();
                if (!getEventsSubscribed(webhookId).contains(message.getEventType())) {
                    continue;
                }
                if (message.getDataGroup() == null) {
                    if (getDataGroupsSubscribed(webhookId).size() == 0) {
                        WebhookPoster.getInstance().postWebhook(message, webhook);
                    }
                } else if (getDataGroupsSubscribed(webhookId).contains(message.getDataGroup())) {
                    WebhookPoster.getInstance().postWebhook(message, webhook);
                }
            }
        }
    }

}
