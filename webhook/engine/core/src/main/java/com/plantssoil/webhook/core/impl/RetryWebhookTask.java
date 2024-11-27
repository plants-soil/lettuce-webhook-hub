package com.plantssoil.webhook.core.impl;

import com.plantssoil.webhook.core.IWebhookSubscriber;

/**
 * The retry webhook task which holds webhook message & subscriber information &
 * execute milliseconds
 * 
 * @author danialdy
 * @Date 27 Nov 2024 11:52:16 am
 */
class RetryWebhookTask {
    private long executeMilliseconds;
    private DefaultWebhookMessage message;
    private IWebhookSubscriber subscriber;

    /**
     * The constructor
     * 
     * @param message            the webhook message
     * @param subscriber         the subscriber to receive message
     * @param executeMillseconds the milliseconds at which to retry the webhook post
     */
    RetryWebhookTask(DefaultWebhookMessage message, IWebhookSubscriber subscriber, long executeMillseconds) {
        this.message = message;
        this.subscriber = subscriber;
        this.executeMilliseconds = executeMillseconds;
    }

    /**
     * Get the milliseconds at which to retry the webhook post
     * 
     * @return the milliseconds
     */
    long getExecuteMilliseconds() {
        return this.executeMilliseconds;
    }

    /**
     * Get the webhook message
     * 
     * @return the webhook message
     */
    DefaultWebhookMessage getMessage() {
        return this.message;
    }

    /**
     * get the subscriber to receive message
     * 
     * @return the subscriber
     */
    IWebhookSubscriber getSubscriber() {
        return this.subscriber;
    }
}
