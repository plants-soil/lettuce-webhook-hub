package com.plantssoil.webhook.core.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * The implementation of webhook engine base on message queue <br/>
 * Could get this webhook engine instance via {@link IEngineFactory}<br/>
 * e.g:
 * 
 * <pre>
 * <code>
 *   IEngineFactory factory = IEngineFactory.getFactoryInstance();
 *   IEngine engine = factory.getEngine();
 *   ...
 * </code>
 * </pre>
 * 
 * @author danialdy
 * @Date 5 Dec 2024 3:40:29 pm
 */
class MultiMessageQueueEngine extends AbstractEngine implements IEngine {
    /**
     * key - the publisher id loaded, value - the publisher id loaded
     */
    private Map<String, String> publishersLoaded = new ConcurrentHashMap<>();
    /**
     * key - the subscriber id loaded, value - the subscriber id loaded
     */
    private Map<String, String> subscriberLoaded = new ConcurrentHashMap<>();
    private Map<PublisherKey, IMessageConsumer<Message>> consumers = new ConcurrentHashMap<>();
    private volatile AtomicInteger consumerId = new AtomicInteger(0);

    public MultiMessageQueueEngine() {
        super();
    }

    @Override
    public void trigger(Message message) {
        if (!this.publishersLoaded.containsKey(message.getPublisherId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20004,
                    String.format("The publisher (%s) does not register yet!", message.getPublisherId()));
        }

        // message service factory
        IMessageServiceFactory<Message> f = IMessageServiceFactory.getFactoryInstance();
        // message publisher
        try (IMessagePublisher<Message> messagePublisher = f.createMessagePublisher()) {
            // publish to message service
            messagePublisher.channelName(getQueueName(message.getPublisherId(), message.getVersion(), message.getDataGroup())).publish(message);
        } catch (Exception e) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20006, e);
        }
    }

    private String getQueueName(String publisherId, String version, String dataGroup) {
        return String.format("%s#R#K#%s#R#K#%s", publisherId, version, dataGroup == null ? "NULL" : dataGroup);
    }

    @Override
    void loadPublisher(IPublisher publisher) {
        // don't need add again if already added
        if (this.publishersLoaded.containsKey(publisher.getPublisherId())) {
            return;
        }
        this.publishersLoaded.put(publisher.getPublisherId(), publisher.getPublisherId());
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
        MultiMessageQueueEventListener listener = new MultiMessageQueueEventListener();
        // consume message from message service
        consumer.consumerId("WEBHOOK-CONSUMER-" + this.consumerId.incrementAndGet()).channelName(queueName).addMessageListener(listener).consume(Message.class);
        // add consumer into map
        this.consumers.put(new PublisherKey(publisher.getPublisherId(), publisher.getVersion(), dataGroup), consumer);
    }

    @Override
    void unloadPublisher(IPublisher publisher) {
        removePublisher(publisher);
        this.publishersLoaded.remove(publisher.getPublisherId());
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
    void loadSubscriber(ISubscriber subscriber) {
        // don't need add again if already added
        if (this.subscriberLoaded.containsKey(subscriber.getSubscriberId())) {
            return;
        }
        this.subscriberLoaded.put(subscriber.getSubscriberId(), subscriber.getSubscriberId());
        // construct and load subscriber
        loadSubscriber0(subscriber);
    }

    private void loadSubscriber0(ISubscriber subscriber) {
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
            l.getClass().isAssignableFrom(MultiMessageQueueEventListener.class);
            if (!(l instanceof MultiMessageQueueEventListener)) {
                continue;
            }
            MultiMessageQueueEventListener mql = (MultiMessageQueueEventListener) l;
            mql.addSubscriber(subscriber, webhook);
        }
    }

    @Override
    void unloadSubscriber(ISubscriber subscriber) {
        for (IMessageConsumer<Message> consumer : this.consumers.values()) {
            for (IMessageListener<Message> l : consumer.getListeners()) {
                if (!(l instanceof MultiMessageQueueEventListener)) {
                    continue;
                }
                MultiMessageQueueEventListener mql = (MultiMessageQueueEventListener) l;
                mql.removeSubscriber(subscriber.getSubscriberId());
            }
        }
        this.subscriberLoaded.remove(subscriber.getSubscriberId());
    }

}
