package com.plantssoil.webhook.core;

/**
 * The webhook engine, the core of webhook<br/>
 * Webhook event producer could register and produce webhook events via this
 * API, the subscriber also could subscribe webhook events from the producers
 * via this API as well.<br/>
 * 
 * @author danialdy
 * @Date 12 Nov 2024 10:53:36 am
 */
public interface IWebhookEngine {
    /**
     * Get the version of current webhook engine
     * 
     * @return webhook engine version
     */
    public String getVersion();

    /**
     * Get the publisher registry<br/>
     * Could register webhook events, and manage webhook events<br/>
     * 
     * @return publisher registry
     */
    public IPublisherRegistry getPublisherRegistry();

    /**
     * Get the subscriber registry<br/>
     * 
     * @return subscriber registry
     */
    public ISubscriberRegistry getSubscriberRegistry();

    /**
     * publish webhook event
     * 
     * @param publisherId publisher identity
     * @param version     webhook version
     * @param dataGroup   webhook data group
     * @param event       webhook event
     */
    public void publish(String publisherId, String version, String dataGroup, IWebhookEvent event);

    /**
     * subscriber subscribe events from publisher
     * 
     * @param subscriberId subscriber identity
     * @param publisherId  publisher to subscribe
     * @param version      publisher webhook to subscribe
     * @param dataGroup    publisher data group to subscribe
     */
    public void subscribe(String subscriberId, String publisherId, String version, String dataGroup);
}
