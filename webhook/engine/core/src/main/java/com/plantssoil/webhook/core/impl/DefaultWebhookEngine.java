package com.plantssoil.webhook.core.impl;

import java.util.concurrent.CompletableFuture;

import com.plantssoil.webhook.core.IWebhookEngine;
import com.plantssoil.webhook.core.IWebhookEvent;
import com.plantssoil.webhook.core.IWebhookRegistry;

public class DefaultWebhookEngine implements IWebhookEngine {

    @Override
    public String getVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IWebhookRegistry getRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<Void> publish(IWebhookEvent event, String payload) {
        // TODO Auto-generated method stub
        return null;
    }

}
