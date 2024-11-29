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
     * post webhook event with payload, the subscribers on the Webhook Event will
     * receive this event.
     * 
     * @param event     the webhook event, which includes all event related
     *                  information, mandatory and should not be null
     * @param dataGroup the data group used to separate data between business units
     *                  or merchants, could be null if don't have multi-datagroups
     * @param requestId the identifier of one specific request, will be considered
     *                  as duplicated request if requests have same requestId,
     *                  mandatory and should not be null
     * @param payload   the business information to publish, mostly defined in JSON
     *                  or XML format, mandatory and should not be null
     * 
     * @return completable future (asynchronized, in order to know success or not)
     */
    public CompletableFuture<Void> post(IWebhookEvent event, String dataGroup, String requestId, String payload);
}
