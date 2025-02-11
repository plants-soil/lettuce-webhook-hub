package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;

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
        case EVENT:
            eventChanged(message);
            break;
        case DATAGROUP:
            dataGroupChanged(message);
            break;
        case SUBSCRIBER:
            subscriberChanged(message);
            break;
        case WEBHOOK:
            webhookChanged(message);
            break;
        case EVENT_SUBSCRIBE:
            eventSubcribedChanged(message);
            break;
        case DATAGROUP_SUBSCRIBE:
            dataGroupSubscribedChanged(message);
            break;
        default:
            break;
        }
    }

    private void publisherChanged(RegistryChangeMessage message) {
        IPublisher publisher = this.engine.getRegistry().findPublisher(message.getPublisherId());
        if (publisher == null) {
            return;
        }
        switch (message.getChangeType()) {
        case ADD:
            this.engine.loadPublisher(publisher);
            break;
        case UPDATE:
            this.engine.loadPublisher(publisher);
            break;
//        case REMOVE:
//            this.engine.unloadPublisher(publisher);
//            break;
        default:
            break;
        }
    }

    private void eventChanged(RegistryChangeMessage message) {
        IPublisher publisher = this.engine.getRegistry().findPublisher(message.getPublisherId());
        if (publisher == null) {
            return;
        }
        switch (message.getChangeType()) {
        case ADD:
            for (String eventId : message.getRegistryId()) {
                IEvent event = this.engine.getRegistry().findEvent(eventId);
                if (event != null) {
                    this.engine.loadEvent(publisher, event);
                }
            }
            break;
        default:
            break;
        }
    }

    private void dataGroupChanged(RegistryChangeMessage message) {
        IPublisher publisher = this.engine.getRegistry().findPublisher(message.getPublisherId());
        if (publisher == null) {
            return;
        }
        switch (message.getChangeType()) {
        case ADD:
            for (String dataGroupId : message.getRegistryId()) {
                IDataGroup dataGroup = this.engine.getRegistry().findDataGroup(dataGroupId);
                if (dataGroup != null) {
                    this.engine.loadDataGroup(publisher, dataGroup);
                }
            }
            break;
        default:
            break;
        }
    }

    private void subscriberChanged(RegistryChangeMessage message) {
        ISubscriber subscriber = this.engine.getRegistry().findSubscriber(message.getSubscriberId());
        if (subscriber == null) {
            return;
        }
        switch (message.getChangeType()) {
        case ADD:
            this.engine.loadSubscriber(subscriber);
            break;
        case UPDATE:
            this.engine.loadSubscriber(subscriber);
            break;
        case REMOVE:
            this.engine.unloadSubscriber(subscriber);
            break;
        default:
            break;
        }
    }

    private void webhookChanged(RegistryChangeMessage message) {
        IWebhook webhook = this.engine.getRegistry().findWebhook(message.getWebhookId());
        if (webhook == null) {
            return;
        }
        switch (message.getChangeType()) {
        case ADD:
            this.engine.loadWebhook(webhook);
            break;
        case UPDATE:
            this.engine.unloadWebhook(webhook);
            this.engine.loadWebhook(webhook);
            this.engine.loadExistingDataGroupsSubscribed(webhook);
            this.engine.loadExistingEventsSubscribed(webhook);
            break;
        case ACTIVATE:
            this.engine.loadWebhook(webhook);
            this.engine.loadExistingEventsSubscribed(webhook);
            this.engine.loadExistingDataGroupsSubscribed(webhook);
            break;
        case DEACTIVATE:
            this.engine.unloadWebhook(webhook);
            break;
        default:
            break;
        }
    }

    private void eventSubcribedChanged(RegistryChangeMessage message) {
        IWebhook webhook = this.engine.getRegistry().findWebhook(message.getWebhookId());
        if (webhook == null) {
            return;
        }
        List<IEvent> events = new ArrayList<>();
        switch (message.getChangeType()) {
        case SUBSCRIBE:
            for (String eventId : message.getRegistryId()) {
                IEvent event = this.engine.getRegistry().findEvent(eventId);
                if (event != null) {
                    events.add(event);
                }
            }
            if (events.size() > 0) {
                this.engine.loadSubscribedEvent(webhook, events);
            }
            break;
        case UNSUBSCRIBE:
            for (String eventId : message.getRegistryId()) {
                IEvent event = this.engine.getRegistry().findEvent(eventId);
                if (event != null) {
                    events.add(event);
                }
            }
            if (events.size() > 0) {
                this.engine.unloadSubscribedEvent(webhook, events);
            }
            break;
        default:
            break;
        }
    }

    private void dataGroupSubscribedChanged(RegistryChangeMessage message) {
        IWebhook webhook = this.engine.getRegistry().findWebhook(message.getWebhookId());
        if (webhook == null) {
            return;
        }
        switch (message.getChangeType()) {
        case SUBSCRIBE:
            for (String dataGroupId : message.getRegistryId()) {
                IDataGroup dataGroup = this.engine.getRegistry().findDataGroup(dataGroupId);
                if (dataGroup != null) {
                    this.engine.loadSubscribedDataGroup(webhook, dataGroup);
                }
            }
            break;
        case UNSUBSCRIBE:
            for (String dataGroupId : message.getRegistryId()) {
                IDataGroup dataGroup = this.engine.getRegistry().findDataGroup(dataGroupId);
                if (dataGroup != null) {
                    this.engine.unloadSubscribedDataGroup(webhook, dataGroup);
                }
            }
            break;
        default:
            break;
        }
    }

}
