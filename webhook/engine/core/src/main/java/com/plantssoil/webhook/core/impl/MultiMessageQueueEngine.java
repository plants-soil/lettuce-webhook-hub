package com.plantssoil.webhook.core.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
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
        if (!publisher.isSupportDataGroup()) {
            loadConsumer(publisher, null);
        }
    }

    private void loadConsumer(IPublisher publisher, String dataGroup) {
        PublisherKey key = new PublisherKey(publisher.getPublisherId(), publisher.getVersion(), dataGroup);
        if (this.consumers.containsKey(key)) {
            return;
        }
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
        this.consumers.put(key, consumer);
    }

    @Override
    void loadEvent(IPublisher publisher, IEvent event) {
        // Nothing needed to do when load event, MQ will be received and the
        // listener will determine where the message should go
    }

    @Override
    void loadDataGroup(IPublisher publisher, IDataGroup dataGroup) {
        if (publisher.isSupportDataGroup()) {
            loadConsumer(publisher, dataGroup.getDataGroup());
        }
    }

    @Override
    void loadSubscriber(ISubscriber subscriber) {
        // Nothing needed to do when load subscriber, MQ will be received and the
        // listener will determine where the message should go
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
    }

    private void loadSubscriber(IWebhook webhook, IDataGroup dataGroup) {
        PublisherKey key = new PublisherKey(webhook.getPublisherId(), webhook.getPublisherVersion(), dataGroup == null ? null : dataGroup.getDataGroup());
        IMessageConsumer<Message> consumer = this.consumers.get(key);
        if (consumer == null) {
            return;
        }
        for (IMessageListener<Message> l : consumer.getListeners()) {
            if (!(l instanceof MultiMessageQueueEventListener)) {
                continue;
            }
            MultiMessageQueueEventListener mql = (MultiMessageQueueEventListener) l;
            mql.addSubscriber(webhook);
            if (dataGroup != null) {
                mql.addDataGroupSubscribed(webhook.getWebhookId(), dataGroup.getDataGroup());
            }
        }
    }

    @Override
    void loadWebhook(IWebhook webhook) {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        IPublisher publisher = r.findPublisher(webhook.getPublisherId());
        if (!publisher.isSupportDataGroup()) {
            loadSubscriber(webhook, null);
        }
    }

    @Override
    void unloadWebhook(IWebhook webhook) {
        for (Map.Entry<PublisherKey, IMessageConsumer<Message>> entry : this.consumers.entrySet()) {
            PublisherKey key = entry.getKey();
            if (!Objects.equals(key.getPublisherId(), webhook.getPublisherId()) || !Objects.equals(key.getVersion(), webhook.getPublisherVersion())) {
                continue;
            }
            for (IMessageListener<Message> l : entry.getValue().getListeners()) {
                if (!(l instanceof MultiMessageQueueEventListener)) {
                    continue;
                }
                MultiMessageQueueEventListener mql = (MultiMessageQueueEventListener) l;
                mql.removeWebhook(webhook);
            }
        }
    }

    @Override
    void loadSubscribedEvent(IWebhook webhook, List<IEvent> events) {
        for (Map.Entry<PublisherKey, IMessageConsumer<Message>> entry : this.consumers.entrySet()) {
            PublisherKey key = entry.getKey();
            if (!Objects.equals(key.getPublisherId(), webhook.getPublisherId()) || !Objects.equals(key.getVersion(), webhook.getPublisherVersion())) {
                continue;
            }
            for (IMessageListener<Message> l : entry.getValue().getListeners()) {
                if (!(l instanceof MultiMessageQueueEventListener)) {
                    continue;
                }
                MultiMessageQueueEventListener mql = (MultiMessageQueueEventListener) l;
                mql.addEventsSubscribed(webhook.getWebhookId(), events);
            }
        }
    }

    @Override
    void unloadSubscribedEvent(IWebhook webhook, List<IEvent> events) {
        for (Map.Entry<PublisherKey, IMessageConsumer<Message>> entry : this.consumers.entrySet()) {
            PublisherKey key = entry.getKey();
            if (!Objects.equals(key.getPublisherId(), webhook.getPublisherId()) || !Objects.equals(key.getVersion(), webhook.getPublisherVersion())) {
                continue;
            }
            for (IMessageListener<Message> l : entry.getValue().getListeners()) {
                if (!(l instanceof MultiMessageQueueEventListener)) {
                    continue;
                }
                MultiMessageQueueEventListener mql = (MultiMessageQueueEventListener) l;
                mql.removeEventsSubscribed(webhook.getWebhookId(), events);
            }
        }
    }

    @Override
    void loadSubscribedDataGroup(IWebhook webhook, IDataGroup dataGroup) {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        IPublisher publisher = r.findPublisher(webhook.getPublisherId());
        if (publisher.isSupportDataGroup()) {
            loadSubscriber(webhook, dataGroup);
        }
    }

    @Override
    void unloadSubscribedDataGroup(IWebhook webhook, IDataGroup dataGroup) {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        IPublisher publisher = r.findPublisher(webhook.getPublisherId());
        if (publisher.isSupportDataGroup()) {
            PublisherKey key = new PublisherKey(webhook.getPublisherId(), webhook.getPublisherVersion(), dataGroup.getDataGroup());
            IMessageConsumer<Message> consumer = this.consumers.get(key);
            List<IMessageListener<Message>> ls = consumer.getListeners();
            for (IMessageListener<Message> l : ls) {
                if (!(l instanceof MultiMessageQueueEventListener)) {
                    continue;
                }
                MultiMessageQueueEventListener mql = (MultiMessageQueueEventListener) l;
                mql.removeDataGroupSubscribed(webhook.getWebhookId(), dataGroup.getDataGroup());
            }
        }
    }

}
