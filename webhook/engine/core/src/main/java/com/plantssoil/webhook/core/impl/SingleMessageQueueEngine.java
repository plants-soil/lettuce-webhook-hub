package com.plantssoil.webhook.core.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.plantssoil.common.mq.ChannelType;
import com.plantssoil.common.mq.IMessageConsumer;
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
class SingleMessageQueueEngine extends AbstractEngine implements IEngine {
    public final static String MESSAGE_QUEUE_NAME = "LETTUCE_WEBHOOK_UNIVERSAL_QUEQUE";
    /**
     * key - the subscriber id loaded, value - the subscriber id loaded
     */
    private Map<String, String> subscriberLoaded = new ConcurrentHashMap<>();
    private SingleMessageQueueEventListener listener = new SingleMessageQueueEventListener();
    private volatile AtomicInteger consumerId = new AtomicInteger(0);

    public SingleMessageQueueEngine() {
        super();
        loadConsumer();
    }

    private void loadConsumer() {
        // message service factory
        IMessageServiceFactory<Message> f = IMessageServiceFactory.getFactoryInstance();
        // message consumer
        IMessageConsumer<Message> consumer = f.createMessageConsumer().consumerId("WEBHOOK-CONSUMER-" + this.consumerId.incrementAndGet())
                .channelName(MESSAGE_QUEUE_NAME).channelType(ChannelType.QUEUE).addMessageListener(listener);
        // consume message from message service
        consumer.consume(Message.class);
    }

    @Override
    public void trigger(Message message) {
        // message service factory
        IMessageServiceFactory<Message> f = IMessageServiceFactory.getFactoryInstance();
        // message publisher
        try (IMessagePublisher<Message> messagePublisher = f.createMessagePublisher().channelName(MESSAGE_QUEUE_NAME).channelType(ChannelType.QUEUE)) {
            // publish to message service
            messagePublisher.publish(message);
        } catch (Exception e) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20006, e);
        }
    }

    @Override
    void loadPublisher(IPublisher publisher) {
        // Nothing needed to do when load publisher, MQ will be received and the
        // listener will determine where the message should go
    }

    @Override
    void unloadPublisher(IPublisher publisher) {
        // Nothing needed to do when unload publisher, because nothing did when load
        // publisher
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
        this.listener.addSubscriber(key, webhook);
    }

    @Override
    void unloadSubscriber(ISubscriber subscriber) {
        this.listener.removeSubscriber(subscriber.getSubscriberId());
        this.subscriberLoaded.remove(subscriber.getSubscriberId());
    }

}
