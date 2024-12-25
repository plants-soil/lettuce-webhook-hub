package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.plantssoil.common.mq.IMessageListener;
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
            synchronized (publisherKey) {
                webhookList = this.webhooks.get(publisherKey);
                if (webhookList == null) {
                    webhookList = new ArrayList<>();
                    this.webhooks.put(publisherKey, webhookList);
                }
            }
        }
        return webhookList;
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
                webhooks.remove(index.intValue());
            }
        }
    }

    @Override
    public void onMessage(Message message, String consumerId) {
        PublisherKey key = new PublisherKey(message.getPublisherId(), message.getVersion(), message.getDataGroup());
        List<IWebhook> webhooks = getWebhooks(key);
        for (IWebhook webhook : webhooks) {
            WebhookPoster.getInstance().postWebhook(message, webhook);
        }
    }

}
