package com.plantssoil.webhook.core.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.plantssoil.webhook.core.IWebhookEvent;
import com.plantssoil.webhook.core.IWebhookPublisher;
import com.plantssoil.webhook.core.IWebhookRegistry;
import com.plantssoil.webhook.core.IWebhookSubscriber;

public class DefaultWebhookRegistry implements IWebhookRegistry {

    @Override
    public CompletableFuture<Void> publishWebhook(IWebhookEvent webhook) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<Void> retireWebhook(IWebhookEvent webhook) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<Void> subscribe(String subscriberId, List<IWebhookEvent> events) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<IWebhookPublisher>> findPublishers(int page, int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<IWebhookEvent>> findWebhooks(String publisherId, int page, int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<IWebhookSubscriber>> findSubscribers(IWebhookEvent webhook, int page, int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

}
