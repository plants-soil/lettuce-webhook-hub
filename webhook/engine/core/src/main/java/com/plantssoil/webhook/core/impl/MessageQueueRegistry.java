package com.plantssoil.webhook.core.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;

/**
 * The implementation of webhook registry (for message queue)
 * 
 * @author danialdy
 * @Date 21 Nov 2024 3:21:43 pm
 */
class MessageQueueRegistry extends AbstractRegistry {
    private Map<PublisherKey, IMessageConsumer<Message>> consumers = new ConcurrentHashMap<>();
    private volatile AtomicInteger consumerId = new AtomicInteger(0);

    @Override
    public void addPublisher(IPublisher publisher) {
        // don't need add again if already added
        if (findSubscriber(publisher.getPublisherId()) != null) {
            return;
        }
        super.addPublisher(publisher);
        loadConsumer(publisher);

    }

    private void loadConsumer(IPublisher publisher) {
        if (publisher.isSupportDataGroup()) {
            int page = 0;
            List<String> dataGroups = publisher.findDataGroups(page, PAGE_SIZE);
            while (dataGroups != null && dataGroups.size() > 0) {
                for (String dataGroup : dataGroups) {
                    loadConsumer(publisher, dataGroup);
                }
                if (dataGroups.size() < PAGE_SIZE) {
                    break;
                }
                page++;
                dataGroups = publisher.findDataGroups(page, PAGE_SIZE);
            }
        } else {
            loadConsumer(publisher, null);
        }
    }

    private void loadConsumer(IPublisher publisher, String dataGroup) {
        // queue name
        String queueName = getQueueName(publisher.getPublisherId(), publisher.getVersion(), dataGroup);
        // message service factory
        IMessageServiceFactory<Message> f = IMessageServiceFactory.getFactoryInstance();
        // message consumer
        IMessageConsumer<Message> consumer = f.createMessageConsumer();
        // message listener
        MessageQueueEventListener listener = new MessageQueueEventListener();
//        @SuppressWarnings("unchecked")
//        IMessageListener<Message> listener = (IMessageListener<Message>) WebhookLoggingHandler.createProxy(listenerImpl);
        // consume message from message service
        consumer.consumerId("WEBHOOK-CONSUMER-" + this.consumerId.incrementAndGet()).queueName(queueName).addMessageListener(listener).consume(Message.class);
        // add consumer into map
        this.consumers.put(new PublisherKey(publisher.getPublisherId(), publisher.getVersion(), dataGroup), consumer);
    }

    private String getQueueName(String publisherId, String version, String dataGroup) {
        return String.format("%s#R#K#%s#R#K#%s", publisherId, version, dataGroup == null ? "NULL" : dataGroup);
    }

    @Override
    public void removePublisher(String publisherId) {
        IPublisher publisher = findPublisher(publisherId);
        if (publisher == null) {
            return;
        }

        removePublisher(publisher);
        super.removePublisher(publisherId);
    }

    private void removePublisher(IPublisher publisher) {
        if (publisher.isSupportDataGroup()) {
            int page = 0;
            List<String> dataGroups = publisher.findDataGroups(page, PAGE_SIZE);
            while (dataGroups != null && dataGroups.size() > 0) {
                for (String dataGroup : dataGroups) {
                    removePublisher(publisher.getPublisherId(), publisher.getVersion(), dataGroup);
                }
                if (dataGroups.size() < PAGE_SIZE) {
                    break;
                }
                page++;
                dataGroups = publisher.findDataGroups(page, PAGE_SIZE);
            }
        } else {
            removePublisher(publisher.getPublisherId(), publisher.getVersion(), null);
        }
    }

    private void removePublisher(String publisherId, String version, String dataGroup) {
        PublisherKey key = new PublisherKey(publisherId, version, dataGroup);
        IMessageConsumer<Message> consumer = this.consumers.get(key);
        if (consumer == null) {
            return;
        }
        consumer.close();
        this.consumers.remove(key);
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
        IMessageConsumer<Message> consumer = this.consumers.get(key);
        if (consumer == null) {
            return;
        }
        for (IMessageListener<Message> l : consumer.getListeners()) {
            l.getClass().isAssignableFrom(MessageQueueEventListener.class);
            if (!(l instanceof MessageQueueEventListener)) {
                continue;
            }
            MessageQueueEventListener mql = (MessageQueueEventListener) l;
            mql.addSubscriber(subscriber, webhook);
        }
    }

    @Override
    public void removeSubscriber(String subscriberId) {
        for (IMessageConsumer<Message> consumer : this.consumers.values()) {
            for (IMessageListener<Message> l : consumer.getListeners()) {
                if (!(l instanceof MessageQueueEventListener)) {
                    continue;
                }
                MessageQueueEventListener mql = (MessageQueueEventListener) l;
                mql.removeSubscriber(subscriberId);
            }
        }
        super.removeSubscriber(subscriberId);
    }

}
