package com.plantssoil.webhook.core;

import java.util.concurrent.CompletableFuture;

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
    public IWebhookRegistry getRegistry();

    /**
     * publish webhook event with payload, the subscribers on the Webhook Event will
     * receive this event.
     * 
     * @param event     the webhook event, which includes all event related
     *                  information
     * @param requestId the identifier of one specific request, will be considered
     *                  as duplicated request if requests have same requestId
     * @param payload   the business information to publish, mostly defined in JSON
     *                  or XML format
     * 
     * @return completable future (asynchronized, in order to know success or not)
     */
    public CompletableFuture<Void> publish(IWebhookEvent event, String requestId, String payload);
}
