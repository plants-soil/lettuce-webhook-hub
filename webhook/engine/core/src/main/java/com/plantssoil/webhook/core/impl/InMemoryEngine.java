package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SubmissionPublisher;

import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * The in-memory implementation of webhook engine<br/>
 * Could get this webhook engine instance via {@link IEngineFactory}<br/>
 * e.g:
 * 
 * <pre>
 * <code>
 *   IEngineFactory factory = IEngineFactory.getFactoryInstance();
 *   IEngine engine = factory.getEngine();
 *   ...
 * </code>
 * </pre>
 * 
 * @author danialdy
 * @Date 16 Nov 2024 9:06:04 pm
 */
class InMemoryEngine extends AbstractEngine implements IEngine {
    /**
     * key - the publisher id loaded, value - the publisher id loaded
     */
    private Map<String, String> publishersLoaded = new ConcurrentHashMap<>();
    /**
     * key - The DefaultPublisherKey(publisherId + version + dataGroup), value -
     * SubmissionPublisher<DefaultMessage>
     */
    private Map<PublisherKey, SubmissionPublisher<Message>> submissionPublishers = new ConcurrentHashMap<>();
    /**
     * key - the ISubscriber.getSubscriberId(), value - list of InMemorySubscriber
     */
    private Map<String, List<InMemorySubscriber>> submissionSubscribers = new ConcurrentHashMap<>();

    public InMemoryEngine() {
        super();
    }

    @Override
    public void triggerMessage(Message message) {
        PublisherKey key = new PublisherKey(message.getPublisherId(), message.getVersion(), message.getDataGroup());
        SubmissionPublisher<Message> publisher = this.submissionPublishers.get(key);
        if (publisher == null) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20002,
                    String.format("The publisher (publisherId: %s, version: %s, dataGroup: %s) does not register yet!", message.getPublisherId(),
                            message.getVersion(), message.getDataGroup() == null ? "NULL" : message.getDataGroup()));
        }
        // don't need publish the message if there is no subscriber
        // otherwise publisher.submit() will block after the messages exceed the maximum
        // capacity
        if (publisher.getSubscribers().size() <= 0) {
            return;
        }
        // publish the message
        publisher.submit(message);
    }

    @Override
    void loadPublisher(IPublisher publisher) {
        // don't need add again if already added
        if (this.publishersLoaded.containsKey(publisher.getPublisherId())) {
            return;
        }
        this.publishersLoaded.put(publisher.getPublisherId(), publisher.getPublisherId());
        // load submission publisher when IPublisher does not support data group
        if (!publisher.getSupportDataGroup()) {
            this.submissionPublishers.put(new PublisherKey(publisher.getPublisherId(), publisher.getVersion(), null), new SubmissionPublisher<Message>());
        }
    }

    @Override
    void loadEvent(IPublisher publisher, IEvent event) {
        // nothing to do when load event
    }

    @Override
    void loadDataGroup(IPublisher publisher, IDataGroup dataGroup) {
        // load submission publisher when IPublisher supports data group
        if (publisher.getSupportDataGroup()) {
            this.submissionPublishers.put(new PublisherKey(publisher.getPublisherId(), publisher.getVersion(), dataGroup.getDataGroup()),
                    new SubmissionPublisher<Message>());
        }
    }

    @Override
    void loadSubscriber(ISubscriber subscriber) {
        // nothing to do when load subscriber
    }

    private List<InMemorySubscriber> getSubscriberList(String subscriberId) {
        List<InMemorySubscriber> ssubscribers = this.submissionSubscribers.get(subscriberId);
        if (ssubscribers == null) {
            synchronized (subscriberId.intern()) {
                ssubscribers = this.submissionSubscribers.get(subscriberId);
                if (ssubscribers == null) {
                    ssubscribers = new ArrayList<InMemorySubscriber>();
                    this.submissionSubscribers.put(subscriberId, ssubscribers);
                }
            }
        }
        return ssubscribers;
    }

    private void loadSubmissionSubscriber(List<InMemorySubscriber> ssubscribers, IWebhook webhook, String dataGroup) {
        PublisherKey key = new PublisherKey(webhook.getPublisherId(), webhook.getPublisherVersion(), dataGroup);
        SubmissionPublisher<Message> publisher = this.submissionPublishers.get(key);
        if (publisher != null) {
            InMemorySubscriber ssub = new InMemorySubscriber(webhook, dataGroup);
            publisher.subscribe(ssub);
            ssubscribers.add(ssub);
            if (!ssub.eventsLoaded()) {
                loadExistingEventsSubscribed(webhook);
            }
        }
    }

    @Override
    void unloadSubscriber(ISubscriber subscriber) {
        List<InMemorySubscriber> ssubscribers = this.submissionSubscribers.get(subscriber.getSubscriberId());
        if (ssubscribers == null) {
            return;
        }

        for (InMemorySubscriber ssubscriber : ssubscribers) {
            ssubscriber.unsubscribe();
        }
        ssubscribers.clear();
        this.submissionSubscribers.remove(subscriber.getSubscriberId());
    }

    @Override
    void loadWebhook(IWebhook webhook) {
        IPublisher publisher = getRegistry().findPublisher(webhook.getPublisherId());
        // load submission subscriber if IPublisher the webhook subscribed does not
        // support data group
        if (!publisher.getSupportDataGroup()) {
            loadSubmissionSubscriber(getSubscriberList(webhook.getSubscriberId()), webhook, null);
        }
    }

    @Override
    void unloadWebhook(IWebhook webhook) {
        List<InMemorySubscriber> ssubscribers = getSubscriberList(webhook.getSubscriberId());
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < ssubscribers.size(); i++) {
            InMemorySubscriber ssubscriber = ssubscribers.get(i);
            if (Objects.equals(webhook.getWebhookId(), ssubscriber.getWebhook().getWebhookId())) {
                indexes.add(i);
                ssubscriber.unsubscribe();
            }
        }
        for (Integer index : indexes) {
            ssubscribers.remove(index.intValue());
        }
    }

    @Override
    void loadSubscribedEvent(IWebhook webhook, List<IEvent> events) {
        List<InMemorySubscriber> ssubscribers = getSubscriberList(webhook.getSubscriberId());
        for (int i = 0; i < ssubscribers.size(); i++) {
            InMemorySubscriber ssubscriber = ssubscribers.get(i);
            if (Objects.equals(webhook.getWebhookId(), ssubscriber.getWebhook().getWebhookId())) {
                for (IEvent event : events) {
                    ssubscriber.addEventSubscribed(event.getEventType());
                }
            }
        }
    }

    @Override
    void unloadSubscribedEvent(IWebhook webhook, List<IEvent> events) {
        List<InMemorySubscriber> ssubscribers = getSubscriberList(webhook.getSubscriberId());
        for (int i = 0; i < ssubscribers.size(); i++) {
            InMemorySubscriber ssubscriber = ssubscribers.get(i);
            if (Objects.equals(webhook.getWebhookId(), ssubscriber.getWebhook().getWebhookId())) {
                for (IEvent event : events) {
                    ssubscriber.removeEventSubscribed(event.getEventType());
                }
            }
        }
    }

    @Override
    void loadSubscribedDataGroup(IWebhook webhook, IDataGroup dataGroup) {
        IPublisher publisher = getRegistry().findPublisher(webhook.getPublisherId());
        if (!publisher.getSupportDataGroup()) {
            return;
        }

        int existingIndex = -1;
        List<InMemorySubscriber> ssubscribers = getSubscriberList(webhook.getSubscriberId());
        for (int i = 0; i < ssubscribers.size(); i++) {
            InMemorySubscriber ssubscriber = ssubscribers.get(i);
            if (Objects.equals(webhook.getWebhookId(), ssubscriber.getWebhook().getWebhookId())
                    && Objects.equals(dataGroup.getDataGroup(), ssubscriber.getDataGroupSubscribed())) {
                existingIndex = i;
                break;
            }
        }
        if (existingIndex < 0) {
            loadSubmissionSubscriber(ssubscribers, webhook, dataGroup.getDataGroup());
        }
    }

    @Override
    void unloadSubscribedDataGroup(IWebhook webhook, IDataGroup dataGroup) {
        IPublisher publisher = getRegistry().findPublisher(webhook.getPublisherId());
        if (!publisher.getSupportDataGroup()) {
            return;
        }

        List<InMemorySubscriber> ssubscribers = getSubscriberList(webhook.getSubscriberId());
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < ssubscribers.size(); i++) {
            InMemorySubscriber ssubscriber = ssubscribers.get(i);
            if (Objects.equals(webhook.getWebhookId(), ssubscriber.getWebhook().getWebhookId())
                    && Objects.equals(dataGroup.getDataGroup(), ssubscriber.getDataGroupSubscribed())) {
                indexes.add(i);
                ssubscriber.unsubscribe();
            }
        }
        for (Integer index : indexes) {
            ssubscribers.remove(index.intValue());
        }
    }
}
