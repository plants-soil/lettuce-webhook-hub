package com.plantssoil.webhook.core;

import java.util.List;

/**
 * The registry of publishers & subscribers<br/>
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
public interface IRegistry {
    /**
     * Add new publisher into the webhook engine<br/>
     * Exception will happen if publisher id already exists
     * 
     * @param publisher new publisher to add
     */
    public void addPublisher(IPublisher publisher);

    /**
     * Update an existing publisher in the webhook engine (if publisher's data group
     * or event changed, should call this function)<br/>
     * The publisher id should NOT be changed<br/>
     * 
     * @param publisher existing publisher to update
     * @see IPublisher
     */
    public void updatePublisher(IPublisher publisher);

    /**
     * Remove an existing publisher from the webhook engine
     * 
     * @param publisherId existing publisher (with publisherId) to remove
     */
    public void removePublisher(String publisherId);

    /**
     * Find existing publisher by publisher id
     * 
     * @param publisherId publisher id
     * @return the publisher object
     */
    public IPublisher findPublisher(String publisherId);

    /**
     * Find all existing publishers, support pagination
     * 
     * @param page     page index
     * @param pageSize maximum publishers on current page
     * @return Publishers on current page
     */
    public List<IPublisher> findAllPublishers(int page, int pageSize);

    /**
     * Add new subscriber into the webhook engine<br/>
     * Exception will happen if subscriber id already exists
     * 
     * @param subscriber new subscriber to add
     */
    public void addSubscriber(ISubscriber subscriber);

    /**
     * Update an existing subscriber in the webhook engine (if subscriber's any
     * property changed, especially the security strategy / request headers /
     * webhook id / webhook secret / events subscribed / etc. changed, should call
     * this method)<br/>
     * Exception will happen if subscriber id does not exist
     * 
     * @param subscriber existing subscriber to update
     * @see ISubscriber
     */
    public void updateSubscriber(ISubscriber subscriber);

    /**
     * Remove an existing subscriber from the webhook engine
     * 
     * @param subscriberId existing subscriber (with subscriberId) to remove
     */
    public void removeSubscriber(String subscriberId);

    /**
     * Find existing subscriber by subscriber id
     * 
     * @param subscriberId subscriber id
     * @return the subscriber object
     */
    public ISubscriber findSubscriber(String subscriberId);

    /**
     * Find all existing subscribers, support pagination
     * 
     * @param page     page index
     * @param pageSize maximum subscribers on current page
     * @return Subscribers on current page
     */
    public List<ISubscriber> findAllSubscribers(int page, int pageSize);

    /**
     * Find existing webhooks who subscribed the specific publisher's events,
     * support pagination
     * 
     * @param publisherId publisher id
     * @param page        page index
     * @param pageSize    maximum webhooks on current page
     * 
     * @return webhook list
     */
//    public List<IWebhook> findWebhooksByPublisher(String publisherId, int page, int pageSize);
}
