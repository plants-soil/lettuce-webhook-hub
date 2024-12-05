package com.plantssoil.webhook.core;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

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

    /**
     * Post message to webhook (this method mostly used for logging & internal
     * retry)
     * 
     * @param message webhook message
     * @param webhook the destination to post the message
     */
    public CompletableFuture<HttpResponse<String>> post(Message message, IWebhook webhook);
}
