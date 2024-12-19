package com.plantssoil.webhook.core.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;

/**
 * The implementation of webhook registry (for message queue)
 * 
 * @author danialdy
 * @Date 21 Nov 2024 3:21:43 pm
 */
class SingleMessageQueueRegistry extends AbstractRegistry {
    public final static String MESSAGE_QUEUE_NAME = "LETTUCE_WEBHOOK_UNIVERSAL_QUEQUE";
    private SingleMessageQueueEventListener listener = new SingleMessageQueueEventListener();
    private volatile AtomicInteger consumerId = new AtomicInteger(0);

    public SingleMessageQueueRegistry() {
        super();
        // message service factory
        IMessageServiceFactory<Message> f = IMessageServiceFactory.getFactoryInstance();
        // message consumer
        IMessageConsumer<Message> consumer = f.createMessageConsumer();
        // consume message from message service
        consumer.consumerId("WEBHOOK-CONSUMER-" + this.consumerId.incrementAndGet()).queueName(MESSAGE_QUEUE_NAME).addMessageListener(listener)
                .consume(Message.class);
    }

    @Override
    public void addSubscriber(ISubscriber subscriber) {
        // don't need add again if already added
        if (findSubscriber(subscriber.getSubscriberId()) != null) {
            return;
        }
        super.addSubscriber(subscriber);
        // construct and load subscriber
        loadSubscriber(subscriber);
    }

    private void loadSubscriber(ISubscriber subscriber) {
        int page = 0;
        List<IWebhook> webhooks = subscriber.findWebhooks(page, PAGE_SIZE);
        while (webhooks != null && webhooks.size() > 0) {
            for (IWebhook webhook : webhooks) {
                loadSubscriber(subscriber, webhook);
            }
            if (webhooks.size() < PAGE_SIZE) {
                break;
            }
            page++;
            webhooks = subscriber.findWebhooks(page, PAGE_SIZE);
        }
    }

    private void loadSubscriber(ISubscriber subscriber, IWebhook webhook) {
        int page = 0;
        boolean hasDataGroup = false;
        List<IDataGroup> dataGroups = webhook.findSubscribedDataGroups(page, PAGE_SIZE);
        while (dataGroups != null && dataGroups.size() > 0) {
            if (!hasDataGroup) {
                hasDataGroup = true;
            }
            for (IDataGroup dataGroup : dataGroups) {
                loadSubscriber(subscriber, webhook, dataGroup);
            }
            if (dataGroups.size() < PAGE_SIZE) {
                break;
            }
            page++;
            dataGroups = webhook.findSubscribedDataGroups(page, PAGE_SIZE);
        }
        if (!hasDataGroup) {
            loadSubscriber(subscriber, webhook, null);
        }
    }

    private void loadSubscriber(ISubscriber subscriber, IWebhook webhook, IDataGroup dataGroup) {
        PublisherKey key = new PublisherKey(webhook.getPublisherId(), webhook.getPublisherVersion(), dataGroup == null ? null : dataGroup.getDataGroup());
        this.listener.addSubscriber(key, webhook);
    }

    @Override
    public void removeSubscriber(String subscriberId) {
        this.listener.removeSubscriber(subscriberId);
        super.removeSubscriber(subscriberId);
    }

}
