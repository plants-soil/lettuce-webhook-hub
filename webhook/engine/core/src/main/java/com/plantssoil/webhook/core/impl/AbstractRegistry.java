package com.plantssoil.webhook.core.impl;

import com.plantssoil.common.mq.ChannelType;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * The abstract registry implementation<br/>
 * Will notify webhook engine when add/update/remove publisher or
 * add/update/remove subscriber<br/>
 * 
 * @author danialdy
 * @Date 3 Dec 2024 4:49:06 pm
 */
public abstract class AbstractRegistry implements IRegistry {
    final static int PAGE_SIZE = 50;

    /**
     * Save new created publisher into persistence
     * 
     * @param publisher publisher new created to save
     */
    protected abstract void saveNewPublisher(IPublisher publisher);

    @Override
    public void addPublisher(IPublisher publisher) {
        // persist publisher
        saveNewPublisher(publisher);
        // notify webhook engine
        notifyWebhookEngine(RegistryChangeMessage.RegistryType.PUBLISHER, RegistryChangeMessage.ChangeType.ADD, publisher.getPublisherId());
    }

    /**
     * Save updated publisher into persistence
     * 
     * @param publisher publisher to update
     */
    protected abstract void saveUpdatedPublisher(IPublisher publisher);

    @Override
    public void updatePublisher(IPublisher publisher) {
        // persist publisher
        saveUpdatedPublisher(publisher);
        // notify webhook engine
        notifyWebhookEngine(RegistryChangeMessage.RegistryType.PUBLISHER, RegistryChangeMessage.ChangeType.UPDATE, publisher.getPublisherId());
    }

    /**
     * Delete publisher from persistence
     * 
     * @param publisher publisher to delete
     */
    protected abstract void deletePublisher(IPublisher publisher);

    @Override
    public void removePublisher(IPublisher publisher) {
        // delete publisher
        deletePublisher(publisher);
        // notify webhook engine
        notifyWebhookEngine(RegistryChangeMessage.RegistryType.PUBLISHER, RegistryChangeMessage.ChangeType.REMOVE, publisher.getPublisherId());
    }

    /**
     * Save new created subscriber into persistence
     * 
     * @param subscriber subscriber new created to save
     */
    protected abstract void saveNewSubscriber(ISubscriber subscriber);

    @Override
    public void addSubscriber(ISubscriber subscriber) {
        // persist subscriber
        saveNewSubscriber(subscriber);
        // notify webhook engine
        notifyWebhookEngine(RegistryChangeMessage.RegistryType.SUBSCRIBER, RegistryChangeMessage.ChangeType.ADD, subscriber.getSubscriberId());
    }

    /**
     * Save updated subscriber into persistence
     * 
     * @param subscriber subscriber to update
     */
    protected abstract void saveUpdatedSubscriber(ISubscriber subscriber);

    @Override
    public void updateSubscriber(ISubscriber subscriber) {
        // persist subscriber
        saveUpdatedSubscriber(subscriber);
        // notify webhook engine
        notifyWebhookEngine(RegistryChangeMessage.RegistryType.SUBSCRIBER, RegistryChangeMessage.ChangeType.UPDATE, subscriber.getSubscriberId());
    }

    /**
     * Delete subscriber from persistence
     * 
     * @param subscriber subscriber to delete
     */
    protected abstract void deleteSubscriber(ISubscriber subscriber);

    @Override
    public void removeSubscriber(ISubscriber subscriber) {
        // persist subscriber
        deleteSubscriber(subscriber);
        // notify webhook engine
        notifyWebhookEngine(RegistryChangeMessage.RegistryType.SUBSCRIBER, RegistryChangeMessage.ChangeType.REMOVE, subscriber.getSubscriberId());
    }

    private void notifyWebhookEngine(RegistryChangeMessage.RegistryType registryType, RegistryChangeMessage.ChangeType changeType, String registryId) {
        IMessageServiceFactory<RegistryChangeMessage> messageFactory = IMessageServiceFactory
                .getFactoryInstance(com.plantssoil.common.mq.simple.MessageServiceFactory.class);
        try (IMessagePublisher<RegistryChangeMessage> publisher = messageFactory.createMessagePublisher()
                .channelName(AbstractEngine.REGISTRY_CHANGE_MESSAGE_QUEUE_NAME).channelType(ChannelType.TOPIC)) {
            RegistryChangeMessage message = new RegistryChangeMessage(registryType, changeType, registryId);
            publisher.publish(message);
        } catch (Exception e) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20007, e);
        }
    }
}
