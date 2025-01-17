package com.plantssoil.webhook.core.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IRegistry;
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
    /**
     * event type subscribed by the webhook
     */
    private Set<String> eventsSubscribed;
    /**
     * data group subscribed by the webhook
     */
    private String dataGroupSubscribed;

    InMemorySubscriber(IWebhook webhook, String dataGroupSubscribed) {
        super();
        this.webhook = webhook;
        this.dataGroupSubscribed = dataGroupSubscribed;
    }

    IWebhook getWebhook() {
        return this.webhook;
    }

    String getDataGroupSubscribed() {
        return dataGroupSubscribed;
    }

    void setDataGroupSubscribed(String dataGroupSubscribed) {
        this.dataGroupSubscribed = dataGroupSubscribed;
    }

    void addEventSubscribed(String eventType) {
        getEventsSubscribed().add(eventType);
    }

    void removeEventSubscribed(String eventType) {
        getEventsSubscribed().remove(eventType);
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

    private Set<String> getEventsSubscribed() {
        if (this.eventsSubscribed == null) {
            synchronized (this) {
                this.eventsSubscribed = loadWebhookEventsSubscribed();
            }
        }
        return this.eventsSubscribed;
    }

    private Set<String> loadWebhookEventsSubscribed() {
        Set<String> es = new HashSet<>();
        int page = 0, pageSize = 20;
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        List<IEvent> events = r.findSubscribedEvents(this.webhook.getWebhookId(), page, pageSize);
        while (events.size() > 0) {
            for (IEvent event : events) {
                es.add(event.getEventType());
            }
            if (events.size() < pageSize) {
                break;
            }
            page++;
            events = r.findSubscribedEvents(this.webhook.getWebhookId(), page, pageSize);
        }
        return es;
    }

    @Override
    public void onNext(Message item) {
        if (!getEventsSubscribed().contains(item.getEventType())) {
            this.subscription.request(1);
            return;
        }
        if (Objects.equals(item.getDataGroup(), getDataGroupSubscribed())) {
            WebhookPoster.getInstance().postWebhook(item, this.webhook);
        }
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
