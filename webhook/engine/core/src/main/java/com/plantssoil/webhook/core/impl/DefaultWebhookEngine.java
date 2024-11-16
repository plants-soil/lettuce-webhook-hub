package com.plantssoil.webhook.core.impl;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.webhook.core.IWebhookEngine;
import com.plantssoil.webhook.core.IWebhookEvent;
import com.plantssoil.webhook.core.IWebhookRegistry;

public class DefaultWebhookEngine implements IWebhookEngine {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultWebhookEngineFactory.class.getName());

    public DefaultWebhookEngine() {
        // TODO initialize and prepare persistence if needed
        LOGGER.info("Initializing persistence...");
        // TODO prepare persistence
        LOGGER.info("Preparing persistence...");
        // TODO prepare message queue if needed
        LOGGER.info("Preparing message service...");
        // TODO AOP log
        LOGGER.info("Preparing AOP logging...");
        // TODO load all defined & persisted webhooks
        LOGGER.info("Loading existing webhooks...");
        // TODO load all consumer on the webhooks
        LOGGER.info("Loading all consumers on existing webhooks...");
        // TODO start all subscription on existing webhooks
        LOGGER.info("Starting all subscription on existing webhooks...");
    }

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
