package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.plantssoil.common.mq.ChannelType;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IOrganization;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * The abstract registry implementation<br/>
 * Could add/update publisher and events/data groups (one publisher may has
 * multiple events and multiple data groups)<br/>
 * Could Add/update/remove subscriber and webhooks (one subscriber may has
 * multiple webhooks)<br/>
 * Webhook could subscribe / unsubscribe events from publisher (one - many),
 * could subscribe / unsubscribe events from publisher(one - many)<br/>
 * Will notify webhook engine when add/update publisher (can't remove publisher,
 * due to maybe already been subscribed) or add/update/remove subscriber<br/>
 * 
 * @author danialdy
 * @Date 3 Dec 2024 4:49:06 pm
 */
public abstract class AbstractRegistry implements IRegistry {
    protected final static int PAGE_SIZE = 20;

    @Override
    public void addOrganization(IOrganization organization) {
        checkRequiredAttribute(organization);
    }

    @Override
    public void updateOrganization(IOrganization organization) {
        checkRequiredAttribute(organization);
    }

    private void checkRequiredAttribute(IOrganization organization) {
        if (organization == null || organization.getOrganizationId() == null) {
            String msg = String.format("The organization and it's attributes (%s) are required!", "organization id");
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20009, msg);
        }
    }

    /**
     * Save new created publisher into persistence (in-memory, JPA, or other)
     * 
     * @param publisher publisher new created to save
     */
    protected abstract void saveNewPublisher(IPublisher publisher);

    @Override
    public void addPublisher(IPublisher publisher) {
        checkRequiredAttribute(publisher);
        // persist publisher
        saveNewPublisher(publisher);
        // notify webhook engine
        RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.PUBLISHER, RegistryChangeMessage.ChangeType.ADD);
        message.setPublisherId(publisher.getPublisherId());
        notifyWebhookEngine(message);
    }

    /**
     * Save updated publisher into persistence (in-memory, JPA, or other)
     * 
     * @param publisher publisher to update
     */
    protected abstract void saveUpdatedPublisher(IPublisher publisher);

    @Override
    public void updatePublisher(IPublisher publisher) {
        checkRequiredAttribute(publisher);
        String publisherId = publisher.getPublisherId();
        synchronized (publisherId.intern()) {
            // persist publisher
            saveUpdatedPublisher(publisher);
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.PUBLISHER, RegistryChangeMessage.ChangeType.UPDATE);
            message.setPublisherId(publisherId);
            notifyWebhookEngine(message);
        }
    }

    private void checkRequiredAttribute(IPublisher publisher) {
        if (publisher == null || publisher.getOrganizationId() == null || publisher.getPublisherId() == null || publisher.getVersion() == null) {
            String msg = String.format("The publisher and it's attributes (%s, %s, %s) are required!", "publisher id", "organization id", "version");
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20009, msg);
        }
    }

    /**
     * Save new created events into persistence (in-memory, JPA, or other)
     * 
     * @param publisherId the publisher id which events belong to
     * @param events      the events new created
     */
    protected abstract void saveNewEvent(String publisherId, List<IEvent> events);

    @Override
    public void addEvent(String publisherId, IEvent event) {
        List<IEvent> events = new ArrayList<>();
        events.add(event);
        addEvent(publisherId, events);
    }

    @Override
    public void addEvent(String publisherId, List<IEvent> events) {
        if (publisherId == null) {
            String msg = String.format("The %s is required!", "publisherId");
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20009, msg);
        }
        for (IEvent event : events) {
            if (event.getEventId() == null || event.getEventType() == null) {
                String msg = String.format("The attributes (%s, %s) are required!", "eventId", "eventType");
                throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20009, msg);
            }
        }

        synchronized (publisherId.intern()) {
            // persist events
            saveNewEvent(publisherId, events);
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.EVENT, RegistryChangeMessage.ChangeType.ADD);
            message.setPublisherId(publisherId);
            Set<String> ids = new LinkedHashSet<>();
            for (IEvent event : events) {
                ids.add(event.getEventId());
            }
            message.setRegistryId(ids);
            notifyWebhookEngine(message);
        }
    }

    /**
     * Save new created data groups into persistence (in-memory, JPA, or other)
     * 
     * @param publisherId publisher id which the data group belong to
     * @param dataGroups  data groups new created
     */
    protected abstract void saveNewDataGroup(String publisherId, List<IDataGroup> dataGroups);

    @Override
    public void addDataGroup(String publisherId, IDataGroup dataGroup) {
        List<IDataGroup> dataGroups = new ArrayList<>();
        dataGroups.add(dataGroup);
        addDataGroup(publisherId, dataGroups);
    }

    @Override
    public void addDataGroup(String publisherId, List<IDataGroup> dataGroups) {
        if (publisherId == null) {
            String msg = String.format("The %s is required!", "publisherId");
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20009, msg);
        }
        for (IDataGroup dataGroup : dataGroups) {
            if (dataGroup.getDataGroupId() == null || dataGroup.getDataGroup() == null) {
                String msg = String.format("The attributes (%s, %s) are required!", "dataGroupId", "dataGroup");
                throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20009, msg);
            }
        }

        synchronized (publisherId.intern()) {
            // persist data groups
            saveNewDataGroup(publisherId, dataGroups);
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.DATAGROUP, RegistryChangeMessage.ChangeType.ADD);
            message.setPublisherId(publisherId);
            Set<String> ids = new LinkedHashSet<>();
            for (IDataGroup dataGroup : dataGroups) {
                ids.add(dataGroup.getDataGroupId());
            }
            message.setRegistryId(ids);
            notifyWebhookEngine(message);
        }
    }

    /**
     * Save new created subscriber into persistence (in-memory, JPA, or other)
     * 
     * @param subscriber Subscriber new created to save
     */
    protected abstract void saveNewSubscriber(ISubscriber subscriber);

    @Override
    public void addSubscriber(ISubscriber subscriber) {
        checkRequiredAttribute(subscriber);
        // persist subscriber
        saveNewSubscriber(subscriber);
        // notify webhook engine
        RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.SUBSCRIBER, RegistryChangeMessage.ChangeType.ADD);
        message.setSubscriberId(subscriber.getSubscriberId());
        notifyWebhookEngine(message);
    }

    /**
     * Save updated subscriber into persistence (in-memory, JPA, or other)
     * 
     * @param subscriber Subscriber to update
     */
    protected abstract void saveUpdatedSubscriber(ISubscriber subscriber);

    @Override
    public void updateSubscriber(ISubscriber subscriber) {
        checkRequiredAttribute(subscriber);
        String subscriberId = subscriber.getSubscriberId();
        synchronized (subscriberId.intern()) {
            // persist subscriber
            saveUpdatedSubscriber(subscriber);
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.SUBSCRIBER, RegistryChangeMessage.ChangeType.UPDATE);
            message.setSubscriberId(subscriberId);
            notifyWebhookEngine(message);
        }
    }

    private void checkRequiredAttribute(ISubscriber subscriber) {
        if (subscriber.getSubscriberId() == null || subscriber.getOrganizationId() == null) {
            String msg = String.format("The attributes (%s, %s) are required!", "subscriberId", "organizationId");
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20009, msg);
        }
    }

    /**
     * Delete subscriber from persistence, all belongs (webhooks/ events subscribed
     * / data groups subscribed) will be removed as well
     * 
     * @param subscriber Subscriber to delete
     */
    protected abstract void deleteSubscriber(ISubscriber subscriber);

    @Override
    public void removeSubscriber(ISubscriber subscriber) {
        String subscriberId = subscriber.getSubscriberId();
        synchronized (subscriberId.intern()) {
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.SUBSCRIBER, RegistryChangeMessage.ChangeType.REMOVE);
            message.setSubscriberId(subscriberId);
            notifyWebhookEngine(message);
            // persist subscriber
            deleteSubscriber(subscriber);
        }
    }

    /**
     * Save the new created webhook into persistence (in-memory, JPA, or other)
     * 
     * @param webhook The webhook new created to save
     */
    protected abstract void saveNewWebhook(IWebhook webhook);

    @Override
    public void addWebhook(IWebhook webhook) {
        checkRequiredAttribute(webhook);
        // persist webhook
        saveNewWebhook(webhook);
        // notify webhook engine
        RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.WEBHOOK, RegistryChangeMessage.ChangeType.ADD);
        message.setPublisherId(webhook.getPublisherId());
        message.setSubscriberId(webhook.getSubscriberId());
        message.setWebhookId(webhook.getWebhookId());
        notifyWebhookEngine(message);
    }

    /**
     * Save the updated webhook into persistence (in-memory, JPA, or other)
     * 
     * @param webhook The webhook to updated
     */
    protected abstract void saveUpdatedWebhook(IWebhook webhook);

    @Override
    public void updateWebhook(IWebhook webhook) {
        checkRequiredAttribute(webhook);
        String webhookId = webhook.getWebhookId();
        synchronized (webhookId.intern()) {
            // persist webhook
            saveUpdatedWebhook(webhook);
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.WEBHOOK, RegistryChangeMessage.ChangeType.UPDATE);
            message.setPublisherId(webhook.getPublisherId());
            message.setSubscriberId(webhook.getSubscriberId());
            message.setWebhookId(webhookId);
            notifyWebhookEngine(message);
        }
    }

    private void checkRequiredAttribute(IWebhook webhook) {
        if (webhook.getWebhookId() == null || webhook.getSubscriberId() == null || webhook.getPublisherId() == null || webhook.getPublisherVersion() == null
                || webhook.getWebhookUrl() == null) {
            String msg = String.format("The attributes (%s, %s, %s, %s, %s) are required!", "webhookId", "subscriberId", "publisherId", "publisherVersion",
                    "webhookUrl");
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20009, msg);
        }
    }

    /**
     * Change the webhook status to {@link WebhookStatus#PRODUCTION} and save to
     * persistence (in-memory, JPA, or other)
     * 
     * @param webhook The webhook to activate
     */
    protected abstract void saveActivatedWebhook(IWebhook webhook);

    @Override
    public void activateWebhook(IWebhook webhook) {
        checkRequiredAttribute(webhook);
        String webhookId = webhook.getWebhookId();
        synchronized (webhookId.intern()) {
            webhook.setWebhookStatus(WebhookStatus.PRODUCTION);
            // persist webhook
            saveActivatedWebhook(webhook);
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.WEBHOOK, RegistryChangeMessage.ChangeType.ACTIVATE);
            message.setPublisherId(webhook.getPublisherId());
            message.setSubscriberId(webhook.getSubscriberId());
            message.setWebhookId(webhookId);
            notifyWebhookEngine(message);
        }
    }

    /**
     * Change the webhook status to {@link WebhookStatus#INACTIVE} and save to
     * persistence (in-memory, JPA, or other)
     * 
     * @param webhook The webhook to deactivate
     */
    protected abstract void saveDeactivatedWebhook(IWebhook webhook);

    @Override
    public void deactivateWebhook(IWebhook webhook) {
        checkRequiredAttribute(webhook);
        String webhookId = webhook.getWebhookId();
        synchronized (webhookId.intern()) {
            webhook.setWebhookStatus(WebhookStatus.INACTIVE);
            // persist webhook
            saveDeactivatedWebhook(webhook);
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.WEBHOOK, RegistryChangeMessage.ChangeType.DEACTIVATE);
            message.setPublisherId(webhook.getPublisherId());
            message.setSubscriberId(webhook.getSubscriberId());
            message.setWebhookId(webhookId);
            notifyWebhookEngine(message);
        }
    }

    @Override
    public void subscribeEvent(IWebhook webhook, IEvent event) {
        List<IEvent> events = new ArrayList<>();
        events.add(event);
        subscribeEvent(webhook, events);
    }

    @Override
    public void unsubscribeEvent(IWebhook webhook, IEvent event) {
        List<IEvent> events = new ArrayList<>();
        events.add(event);
        unsubscribeEvent(webhook, events);
    }

    /**
     * Save the subscribed events for the specific webhook to persistence
     * (in-memory, JPA, or other)
     * 
     * @param webhook The webhook which subscribe the events
     * @param events  The events subscribed
     */
    protected abstract void saveEventSubscribed(IWebhook webhook, List<IEvent> events);

    @Override
    public void subscribeEvent(IWebhook webhook, List<IEvent> events) {
        String webhookId = webhook.getWebhookId();
        synchronized (webhookId.intern()) {
            // persist events subscribed
            saveEventSubscribed(webhook, events);
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.EVENT_SUBSCRIBE,
                    RegistryChangeMessage.ChangeType.SUBSCRIBE);
            message.setPublisherId(webhook.getPublisherId());
            message.setSubscriberId(webhook.getSubscriberId());
            message.setWebhookId(webhookId);
            Set<String> ids = new LinkedHashSet<>();
            for (IEvent event : events) {
                ids.add(event.getEventId());
            }
            message.setRegistryId(ids);
            notifyWebhookEngine(message);
        }
    }

    /**
     * Remove the unsubscribed events from the specific webhook to persistence
     * (in-memory, JPA, or other)
     * 
     * @param webhook The webhook which subscribed the events
     * @param events  The events to unsubscribe
     */
    protected abstract void saveEventUnsubscribed(IWebhook webhook, List<IEvent> events);

    @Override
    public void unsubscribeEvent(IWebhook webhook, List<IEvent> events) {
        String webhookId = webhook.getWebhookId();
        synchronized (webhookId.intern()) {
            // persist events subscribed
            saveEventUnsubscribed(webhook, events);
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.EVENT_SUBSCRIBE,
                    RegistryChangeMessage.ChangeType.UNSUBSCRIBE);
            message.setPublisherId(webhook.getPublisherId());
            message.setSubscriberId(webhook.getSubscriberId());
            message.setWebhookId(webhookId);
            Set<String> ids = new LinkedHashSet<>();
            for (IEvent event : events) {
                ids.add(event.getEventId());
            }
            message.setRegistryId(ids);
            notifyWebhookEngine(message);
        }
    }

    @Override
    public void subscribeDataGroup(IWebhook webhook, IDataGroup dataGroup) {
        List<IDataGroup> dataGroups = new ArrayList<>();
        dataGroups.add(dataGroup);
        subscribeDataGroup(webhook, dataGroups);
    }

    /**
     * Save the subscribed data groups for the specific webhook to persistence
     * (in-memory, JPA, or other)
     * 
     * @param webhook    The webhook which subscribe the data groups
     * @param dataGroups The data groups to subscribe
     */
    protected abstract void saveDataGroupSubscribed(IWebhook webhook, List<IDataGroup> dataGroups);

    @Override
    public void subscribeDataGroup(IWebhook webhook, List<IDataGroup> dataGroups) {
        String webhookId = webhook.getWebhookId();
        synchronized (webhookId.intern()) {
            // persist data groups subscribed
            saveDataGroupSubscribed(webhook, dataGroups);
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.DATAGROUP_SUBSCRIBE,
                    RegistryChangeMessage.ChangeType.SUBSCRIBE);
            message.setPublisherId(webhook.getPublisherId());
            message.setSubscriberId(webhook.getSubscriberId());
            message.setWebhookId(webhookId);
            Set<String> ids = new LinkedHashSet<>();
            for (IDataGroup dataGroup : dataGroups) {
                ids.add(dataGroup.getDataGroupId());
            }
            message.setRegistryId(ids);
            notifyWebhookEngine(message);
        }
    }

    @Override
    public void unsubscribeDataGroup(IWebhook webhook, IDataGroup dataGroup) {
        List<IDataGroup> dataGroups = new ArrayList<>();
        dataGroups.add(dataGroup);
        unsubscribeDataGroup(webhook, dataGroups);
    }

    /**
     * Remove the subscribed data groups from the specific webhook to persistence
     * (in-memory, JPA, or other)
     * 
     * @param webhook    The webhook which subscribed the data groups
     * @param dataGroups The data groups to unsubscribe
     */
    protected abstract void saveDataGroupUnsubscribed(IWebhook webhook, List<IDataGroup> dataGroups);

    @Override
    public void unsubscribeDataGroup(IWebhook webhook, List<IDataGroup> dataGroups) {
        String webhookId = webhook.getWebhookId();
        synchronized (webhookId.intern()) {
            // persist data groups subscribed
            saveDataGroupUnsubscribed(webhook, dataGroups);
            // notify webhook engine
            RegistryChangeMessage message = new RegistryChangeMessage(RegistryChangeMessage.RegistryType.DATAGROUP_SUBSCRIBE,
                    RegistryChangeMessage.ChangeType.UNSUBSCRIBE);
            message.setPublisherId(webhook.getPublisherId());
            message.setSubscriberId(webhook.getSubscriberId());
            message.setWebhookId(webhookId);
            Set<String> ids = new LinkedHashSet<>();
            for (IDataGroup dataGroup : dataGroups) {
                ids.add(dataGroup.getDataGroupId());
            }
            message.setRegistryId(ids);
            notifyWebhookEngine(message);
        }
    }

    private void notifyWebhookEngine(RegistryChangeMessage message) {
        IMessageServiceFactory<RegistryChangeMessage> messageFactory = IMessageServiceFactory
                .getFactoryInstance(com.plantssoil.common.mq.simple.MessageServiceFactory.class);
        try (IMessagePublisher<RegistryChangeMessage> publisher = messageFactory.createMessagePublisher()
                .channelName(AbstractEngine.REGISTRY_CHANGE_MESSAGE_QUEUE_NAME).channelType(ChannelType.TOPIC)) {
            publisher.publish(message);
        } catch (Exception e) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20007, e);
        }
    }
}
