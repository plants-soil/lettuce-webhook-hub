package com.plantssoil.webhook.core.impl;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;

/**
 * The subscriber to receive message and handle message
 * 
 * @author danialdy
 * @Date 3 Dec 2024 4:43:16 pm
 */
class InMemorySubscriber implements Subscriber<Message> {
    private final static Logger LOGGER = LoggerFactory.getLogger(InMemorySubscriber.class.getName());
    private IWebhook webhook;
    private Subscription subscription;

    InMemorySubscriber(IWebhook webhook) {
        super();
        this.webhook = webhook;
    }

    void unsubscribe() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Webhook subscriber (publisherId: %s, version: %s) unsubscribed.", this.webhook.getPublisherId(),
                    this.webhook.getPublisherVersion()));
        }
        this.subscription.cancel();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Webhook subscriber (publisherId: %s, version: %s) subscribed.", this.webhook.getPublisherId(),
                    this.webhook.getPublisherVersion()));
        }
        this.subscription = subscription;
        this.subscription.request(1);
    }

    @Override
    public void onNext(Message item) {
        WebhookPoster.getInstance().postWebhook(item, this.webhook);
        this.subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        LOGGER.error(throwable.getMessage());
    }

    @Override
    public void onComplete() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Webhook subscriber (publisherId: %s, version: %s) completed.", this.webhook.getPublisherId(),
                    this.webhook.getPublisherVersion()));
        }
        this.webhook = null;
    }

}
