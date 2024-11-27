package com.plantssoil.webhook.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The registry API for webhook registration, retire, subscribe, query, etc.
 * 
 * @author danialdy
 * @Date 12 Nov 2024 4:31:41 pm
 */
public interface IWebhookRegistry {
    /**
     * add data group to publisher
     * 
     * @param publisher the publisher owns the data group
     * @param dataGroup the data group identity
     * @return completable future (asynchronized, in order to know success or not)
     */
    public CompletableFuture<Void> addDataGroup(IWebhookPublisher publisher, String dataGroup);

    /**
     * add data groups to publisher
     * 
     * @param publisher  the publisher owns the data groups
     * @param dataGroups the data group identities
     * @return completable future (asynchronized, in order to know success or not)
     */
    public CompletableFuture<Void> addDataGroup(IWebhookPublisher publisher, String[] dataGroups);

    /**
     * Publisher publish webhook to be active
     * 
     * @param webhook webhook definition
     * @return completable future (asynchronized, in order to know success or not)
     */
    public CompletableFuture<Void> publishWebhook(IWebhookEvent webhook);

    /**
     * Publisher retire webhook to be inactive
     * 
     * @param webhook webhook definition
     * @return completable future (asynchronized, in order to know success or not)
     */
    public CompletableFuture<Void> retireWebhook(IWebhookEvent webhook);

    /**
     * Subscriber subscribe events from publisher, will get notification (via the
     * subscriber's callback URL) when new event comes from publisher
     * 
     * @param subscriberId subscriber identity
     * @param events       webhook events to subscribe
     * 
     * @return completable future (asynchronized, in order to know success or not)
     */
    public CompletableFuture<Void> subscribe(String subscriberId, List<IWebhookEvent> events);

    /**
     * Find all publishers, could pagination the result
     * 
     * @param page     page no
     * @param pageSize how many items on current page (the maximum items return)
     * 
     * @return completable future (asynchronized) with the publisher list
     */
    public CompletableFuture<List<IWebhookPublisher>> findPublishers(int page, int pageSize);

    /**
     * Find the data groups of one publisher, could pagination the result
     * 
     * @param publisherId publisher id
     * @param page        page no
     * @param pageSize    how many items on current page (the maximum items return)
     * @return completable future (asynchronized) with the publisher list
     */
    public CompletableFuture<List<String>> findDataGroups(String publisherId, int page, int pageSize);

    /**
     * Find the webhooks of one publisher, could pagination the result
     * 
     * @param publisherId publisher id
     * @param page        page no
     * @param pageSize    how many items on current page (the maximum items return)
     * 
     * @return completable future (asynchronized) with the webhook list
     */
    public CompletableFuture<List<IWebhookEvent>> findWebhooks(String publisherId, int page, int pageSize);

    /**
     * Find the subscribers of one webhook, could pagination the result
     * 
     * @param webhook  webhook definition
     * @param page     page no
     * @param pageSize how many items on current page (the maximum items return)
     * 
     * @return completable future (asynchronized) with the subscriber list
     */
    public CompletableFuture<List<IWebhookSubscriber>> findSubscribers(IWebhookEvent webhook, int page, int pageSize);

}
