package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.plantssoil.common.mq.IMessageListener;
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
                WebhookPoster.getInstance().postWebhook(message, webhook);
            }
        }
    }

}
