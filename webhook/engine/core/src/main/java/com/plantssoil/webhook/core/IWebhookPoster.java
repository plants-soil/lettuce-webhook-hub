package com.plantssoil.webhook.core;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.plantssoil.webhook.core.impl.DefaultWebhookMessage;

/**
 * Webhook Poster
 * 
 * @author danialdy
 * @Date 27 Nov 2024 10:33:57 am
 */
public interface IWebhookPoster {
    /**
     * Post webbhok with message & event
     * 
     * @param message message to post
     * @param event   the message event
     */
    public void postWebhook(final DefaultWebhookMessage message, final IWebhookEvent event);

    /**
     * Post webhook message to subscriber (this method mostly used for logging)
     * 
     * @param message    webhook message
     * @param subscriber the destination to post the message
     */
    public CompletableFuture<HttpResponse<String>> post(DefaultWebhookMessage message, IWebhookSubscriber subscriber);
}
