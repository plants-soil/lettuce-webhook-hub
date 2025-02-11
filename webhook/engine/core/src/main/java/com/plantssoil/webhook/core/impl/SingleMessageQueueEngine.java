package com.plantssoil.webhook.core.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.plantssoil.common.mq.ChannelType;
import com.plantssoil.common.mq.IMessageConsumer;
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
class SingleMessageQueueEngine extends AbstractEngine implements IEngine {
    public final static String MESSAGE_QUEUE_NAME = "com.plantssoil.mq.universal.channel";
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
    public void triggerMessage(Message message) {
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
    void loadEvent(IPublisher publisher, IEvent event) {
        // Nothing needed to do when load event, MQ will be received and the
        // listener will determine where the message should go
    }

    @Override
    void loadDataGroup(IPublisher publisher, IDataGroup dataGroup) {
        // Nothing needed to do when load dataGroup, MQ will be received and the
        // listener will determine where the message should go
    }

    @Override
    void loadSubscriber(ISubscriber subscriber) {
        // Nothing needed to do when load subscriber, MQ will be received and the
        // listener will determine where the message should go
    }

    private void loadSubscriber(IWebhook webhook, IDataGroup dataGroup) {
        PublisherKey key = new PublisherKey(webhook.getPublisherId(), webhook.getPublisherVersion(), dataGroup == null ? null : dataGroup.getDataGroup());
        this.listener.addSubscriber(key, webhook);
        if (dataGroup != null) {
            this.listener.addDataGroupSubscribed(webhook.getWebhookId(), dataGroup.getDataGroup());
        }
    }

    @Override
    void unloadSubscriber(ISubscriber subscriber) {
        this.listener.removeSubscriber(subscriber.getSubscriberId());
    }

    @Override
    void loadWebhook(IWebhook webhook) {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        IPublisher publisher = r.findPublisher(webhook.getPublisherId());
        if (!publisher.getSupportDataGroup()) {
            loadSubscriber(webhook, null);
        }
    }

    @Override
    void unloadWebhook(IWebhook webhook) {
        this.listener.removeWebhook(webhook);
    }

    @Override
    void loadSubscribedEvent(IWebhook webhook, List<IEvent> events) {
        this.listener.addEventsSubscribed(webhook.getWebhookId(), events);
    }

    @Override
    void unloadSubscribedEvent(IWebhook webhook, List<IEvent> events) {
        this.listener.removeEventsSubscribed(webhook.getWebhookId(), events);
    }

    @Override
    void loadSubscribedDataGroup(IWebhook webhook, IDataGroup dataGroup) {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        IPublisher publisher = r.findPublisher(webhook.getPublisherId());
        if (publisher.getSupportDataGroup()) {
            loadSubscriber(webhook, dataGroup);
        }
    }

    @Override
    void unloadSubscribedDataGroup(IWebhook webhook, IDataGroup dataGroup) {
        this.listener.removeDataGroupSubscribed(webhook.getWebhookId(), dataGroup.getDataGroup());
    }

}
