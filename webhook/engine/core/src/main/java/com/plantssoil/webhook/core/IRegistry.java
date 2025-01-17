package com.plantssoil.webhook.core;

import java.util.List;

import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;

/**
 * The registry of publishers & subscribers:
 * <ul>
 * <li>Could add/update publisher and events/data groups (one publisher may has
 * multiple events and multiple data groups)</li>
 * <li>Can't remove publisher, maybe it already been subscribed</li>
 * <li>Could Add/update/remove subscriber and webhooks (one subscriber may has
 * multiple webhooks)</li>
 * <li>Webhook could subscribe / unsubscribe events from publisher (one - many),
 * could subscribe / unsubscribe events from publisher(one - many)</li>
 * <li>Could find publisher(s) and event(s)/data group(s), could find
 * subscriber(s) and webhook(s) and all subscriptions</li>
 * </ul>
 * Notice:
 * <ul>
 * <li>Need add publisher before add / update subscribers on it</li>
 * <li>Will remove all subscriptions on one publisher before it's removed</li>
 * <li>All the subscriptions will be removed before one subscriber is
 * removed</li>
 * </ul>
 * 
 * @author danialdy
 * @Date 29 Nov 2024 11:36:05 am
 */
public interface IRegistry extends IConfigurable {
    /**
     * Add new organization into the webhook engine<br/>
     * Exception will happen if organization id already exists<br/>
     * 
     * @param organization New organization to add
     * @see IOrganization
     */
    public void addOrganization(IOrganization organization);

    /**
     * Update an existing organization into the webhook engine<br/>
     * 
     * @param organization Existing organization to update
     * @see IOrganization
     */
    public void updateOrganizationId(IOrganization organization);

    /**
     * Add new publisher into the webhook engine<br/>
     * Exception will happen if publisher id already exists
     * <ul>
     * <li>The "support data group" attribute can't be changed once the publisher
     * saved</li>
     * <li>The "version" attribute should be unique within one organization</li>
     * </ul>
     * 
     * @param publisher New publisher to add
     * @see IPublisher
     */
    public void addPublisher(IPublisher publisher);

    /**
     * Update an existing publisher in the webhook engine<br/>
     * The "support data group" and "version" attribute can't be changed<br/>
     * The publisher id should NOT be changed<br/>
     * 
     * @param publisher Existing publisher to update
     * @see IPublisher
     */
    public void updatePublisher(IPublisher publisher);

    /**
     * Add event to the specific publisher<br/>
     * The event type should be unique within one publisher<br/>
     * 
     * @param publisherId The publisher id which event belongs to
     * @param event       Event to add
     * @see IEvent
     */
    public void addEvent(String publisherId, IEvent event);

    /**
     * Add events to the specific publisher<br/>
     * The event type of each event should be unique within one publisher<br/>
     * 
     * @param publisherId The publisher id which event belongs to
     * @param events      Events to add
     * @see IEvent
     */
    public void addEvent(String publisherId, List<IEvent> events);

    /**
     * Add data group to the specific publisher, if the publisher support data
     * group<br/>
     * The data group name should be unique within one publisher<br/>
     * 
     * @param publisherId The publisher id which event belongs to
     * @param dataGroup   Data group to add
     * @see IDataGroup
     */
    public void addDataGroup(String publisherId, IDataGroup dataGroup);

    /**
     * Add data groups to the specific publisher, if the publisher has data
     * group<br/>
     * The data group name should be unique within one publisher<br/>
     * 
     * @param publisherId The publisher id which event belongs to
     * @param dataGroups  Data groups to add
     * @see IDataGroup
     */
    public void addDataGroup(String publisherId, List<IDataGroup> dataGroups);

    /**
     * Add new subscriber into the webhook engine<br/>
     * Exception will happen if subscriber id already exists
     * 
     * @param subscriber New subscriber to add
     * @see ISubscriber
     */
    public void addSubscriber(ISubscriber subscriber);

    /**
     * Update an existing subscriber in the webhook engine<br/>
     * The subscriber id should NOT be changed<br/>
     * 
     * @param subscriber Existing subscriber to update
     * @see ISubscriber
     */
    public void updateSubscriber(ISubscriber subscriber);

    /**
     * Remove an existing subscriber from the webhook engine, all belongs (webhooks
     * / events subscribed / data groups subscribed) will be removed as well
     * 
     * @param subscriber Existing subscriber to remove
     * @see ISubscriber
     * @see IWebhook
     */
    public void removeSubscriber(ISubscriber subscriber);

    /**
     * Add webhook, the subscriber / publisher should exist first<br/>
     * 
     * @param webhook Webhook to add
     * @see IWebhook
     */
    public void addWebhook(IWebhook webhook);

    /**
     * Update existing webhook<br/>
     * The webhook id should NOT be changed<br/>
     * the publisher id & publisher version & subscriber id can't be changed<br/>
     * 
     * @param webhook webhook to update
     * @see IWebhook
     */
    public void updateWebhook(IWebhook webhook);

    /**
     * Activate the wehbook<br/>
     * The webhook status should not be {@link WebhookStatus#PRODUCTION}<br/>
     * Will load existing subscribed events / subscribed data groups<br/>
     * 
     * @param webhook The webhook to activate
     * @see IWebhook
     */
    public void activateWebhook(IWebhook webhook);

    /**
     * Deactivate existing webhook<br/>
     * The webhook status should not be {@link WebhookStatus#INACTIVE}<br/>
     * Will unload the webhook and subscribed event / subscribed data groups<br/>
     * 
     * @param webhook The webhook to deactivate
     * @see IWebhook
     */
    public void deactivateWebhook(IWebhook webhook);

    /**
     * Subscribe the event for the specific webhook
     * 
     * @param webhook The webhook who will subscribe the event
     * @param event   Event to subscribe
     * @see IWebhook
     * @see IEvent
     */
    public void subscribeEvent(IWebhook webhook, IEvent event);

    /**
     * Unsubscribe the event from the specific webhook
     * 
     * @param webhook The webhook who subscribed the event
     * @param event   Event to unsubscribe
     * @see IWebhook
     * @see IEvent
     */
    public void unsubscribeEvent(IWebhook webhook, IEvent event);

    /**
     * Subscribe the events for the specific webhooks
     * 
     * @param webhook The webhook who will subscribe the events
     * @param events  Events to subscribe
     * @see IWebhook
     * @see IEvent
     */
    public void subscribeEvent(IWebhook webhook, List<IEvent> events);

    /**
     * Unsubscribe the events from the specific webhook
     * 
     * @param webhook The webhook who subscribed the events
     * @param events  Events to unsubscribe
     * @see IWebhook
     * @see IEvent
     */
    public void unsubscribeEvent(IWebhook webhook, List<IEvent> events);

    /**
     * Subscribe data group for the specific webhook if the subscribed publisher
     * support multiple data groups
     * 
     * @param webhook   The webhook who will subscribe event
     * @param dataGroup The specific data group to subscribe
     * @see IWebhook
     * @see IDataGroup
     */
    public void subscribeDataGroup(IWebhook webhook, IDataGroup dataGroup);

    /**
     * Subscribe data groups for the specific webhook if the subscribed publisher
     * support multiple data groups
     * 
     * @param webhook   the webhook who will subscribe the event
     * @param dataGroup the specific data groups who trigger event
     * @see IWebhook
     * @see IDataGroup
     */
    public void subscribeDataGroup(IWebhook webhook, List<IDataGroup> dataGroups);

    /**
     * Unsubscribe data group from the specific webhook if subscribed
     * 
     * @param webhook   The webhook who subscribed the data group
     * @param dataGroup The subscribed data group to unsubscribe
     * @see IWebhook
     * @see IDataGroup
     */
    public void unsubscribeDataGroup(IWebhook webhook, IDataGroup dataGroup);

    /**
     * Unsubscribe data groups from the specific webhook if subscribed
     * 
     * @param webhook   The webhook who subscribed the data groups
     * @param dataGroup The subscribed data groups to unsubscribe
     * @see IWebhook
     * @see IDataGroup
     */
    public void unsubscribeDataGroup(IWebhook webhook, List<IDataGroup> dataGroups);

    /**
     * Find existing publisher by publisher id
     * 
     * @param publisherId The publisher id
     * @return The publisher object
     * @see IPublisher
     */
    public IPublisher findPublisher(String publisherId);

    /**
     * Find all existing publishers, support pagination
     * 
     * @param page     Page index
     * @param pageSize Maximum publishers on current page
     * @return Publishers on current page
     * @see IPublisher
     */
    public List<IPublisher> findAllPublishers(int page, int pageSize);

    /**
     * Find all events which belong to the specific publisher, support pagination
     * 
     * @param publisherId The publisher id which events belongs to
     * @param page        The page index
     * @param pageSize    Maximum events on current page
     * @return Events on current page
     * @see IEvent
     */
    public List<IEvent> findEvents(String publisherId, int page, int pageSize);

    /**
     * Find the specific event
     * 
     * @param eventId The event id to find
     * @return event found, null if not found
     * @see IEvent
     */
    public IEvent findEvent(String eventId);

    /**
     * Find all data groups which belong to the specific publisher, support
     * pagination
     * 
     * @param publisherId The publisher id which data groups belongs to
     * @param page        The page index
     * @param pageSize    Maximum data groups on current page
     * @return data groups on current page
     * @see IDataGroup
     */
    public List<IDataGroup> findDataGroups(String publisherId, int page, int pageSize);

    /**
     * Find data group
     * 
     * @param dataGroupId The data group id to find
     * @return Data group found, null if not found
     * @see IDataGroup
     */
    public IDataGroup findDataGroup(String dataGroupId);

    /**
     * Find data group by publisher id + data group name
     * 
     * @param publisherId The publisher id which data group belongs to
     * @param dataGroup   The data group name to find
     * @return Data group found, null if not found
     * @see IDataGroup
     */
    public IDataGroup findDataGroup(String publisherId, String dataGroup);

    /**
     * Find existing subscriber by subscriber id
     * 
     * @param subscriberId The subscriber id to find
     * @return The subscriber object, null if not found
     * @see ISubscriber
     */
    public ISubscriber findSubscriber(String subscriberId);

    /**
     * Find all existing subscribers, support pagination
     * 
     * @param page     Page index
     * @param pageSize Maximum subscribers on current page
     * @return Subscribers on current page
     * @see ISubscriber
     */
    public List<ISubscriber> findAllSubscribers(int page, int pageSize);

    /**
     * Find all webhooks which belong to the specific subscriber, support pagination
     * 
     * @param subscriberId The subscriber id which webhooks belongs to
     * @param page         The page index
     * @param pageSize     Maximum webhook on current page
     * @return Webhooks on current page
     * @see IWebhook
     */
    public List<IWebhook> findWebhooks(String subscriberId, int page, int pageSize);

    /**
     * Find webhook by webhook id
     * 
     * @param webhookId The webhook id to find
     * @return Webhook found, null if not found
     * @see IWebhook
     */
    public IWebhook findWebhook(String webhookId);

    /**
     * Find the subscribed events which belong to the specific webhook, support
     * pagination
     * 
     * @param webhookId The webhook id which subscribed events
     * @param page      Page index
     * @param pageSize  Maximum events on current page
     * @return Subscribed events on current page
     * @see IEvent
     */
    public List<IEvent> findSubscribedEvents(String webhookId, int page, int pageSize);

    /**
     * Find the subscribed data groups which belong to the specific webhook, support
     * pagination
     * 
     * @param webhookId The webhook id which subscribed data groups
     * @param page      Page index
     * @param pageSize  Maximum data groups on current page
     * @return Subscribed data groups on current page
     * @see IDataGroup
     */
    public List<IDataGroup> findSubscribedDataGroups(String webhookId, int page, int pageSize);

    /**
     * Find the subscribed data group with the specified data group name
     * 
     * @param webhookId The webhook id which subscribed the data group
     * @param dataGroup The data group name to find
     * @return The data group found
     * @see IDataGroup
     */
    public IDataGroup findSubscribedDataGroup(String webhookId, String dataGroup);
}
