package com.plantssoil.webhook.core;

/**
 * Webhook Poster
 * 
 * @author danialdy
 * @Date 27 Nov 2024 10:33:57 am
 */
public interface IWebhookPoster {
    /**
     * Post webbhok with message & webhook
     * 
     * @param message message to post
     * @param webhook the destination to post the message
     */
    public void postWebhook(final Message message, final IWebhook webhook);
}
