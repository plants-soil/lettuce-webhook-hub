package com.plantssoil.webhook.core.impl;

import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;

/**
 * The retry webhook task which holds webhook message & webhook information &
 * execute milliseconds
 * 
 * @author danialdy
 * @Date 27 Nov 2024 11:52:16 am
 */
class RetryWebhookTask {
    private long executeMilliseconds;
    private Message message;
    private IWebhook webhook;

    /**
     * The constructor
     * 
     * @param message            the webhook message
     * @param webhook            the webhook to receive message
     * @param executeMillseconds the milliseconds at which to retry the webhook post
     */
    RetryWebhookTask(Message message, IWebhook webhook, long executeMillseconds) {
        this.message = message;
        this.webhook = webhook;
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
    Message getMessage() {
        return this.message;
    }

    /**
     * get the webhook to receive message
     * 
     * @return the webhook
     */
    IWebhook getWebhook() {
        return this.webhook;
    }
}
