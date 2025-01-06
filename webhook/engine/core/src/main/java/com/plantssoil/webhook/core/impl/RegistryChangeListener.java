package com.plantssoil.webhook.core.impl;

import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.ISubscriber;

/**
 * The listener to monitor registry changes, will notify webhook engine responds
 * to the change of publisher / subscriber
 * 
 * @author danialdy
 * @Date 3 Jan 2025 10:19:57 am
 */
class RegistryChangeListener implements IMessageListener<RegistryChangeMessage> {
    private AbstractEngine engine;

    RegistryChangeListener(AbstractEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onMessage(RegistryChangeMessage message, String consumerId) {
        switch (message.getRegistryType()) {
        case PUBLISHER:
            publisherChanged(message);
            break;
        case SUBSCRIBER:
            subscriberChanged(message);
            break;
        default:
            break;
        }
    }

    private void publisherChanged(RegistryChangeMessage message) {
        IPublisher publisher = this.engine.getRegistry().findPublisher(message.getRegistryId());
        if (publisher == null) {
            return;
        }
        switch (message.getChangeType()) {
        case ADD:
            this.engine.loadPublisher(publisher);
            break;
        case UPDATE:
            this.engine.reloadPublisher(publisher);
            break;
        case REMOVE:
            this.engine.unloadPublisher(publisher);
            break;
        default:
            break;
        }
    }

    private void subscriberChanged(RegistryChangeMessage message) {
        ISubscriber subscriber = this.engine.getRegistry().findSubscriber(message.getRegistryId());
        switch (message.getChangeType()) {
        case ADD:
            this.engine.loadSubscriber(subscriber);
            break;
        case UPDATE:
            this.engine.reloadSubscriber(subscriber);
            break;
        case REMOVE:
            this.engine.unloadSubscriber(subscriber);
            break;
        default:
            break;
        }
    }

}
