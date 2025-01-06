package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SubmissionPublisher;

import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;
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
    public void trigger(Message message) {
        PublisherKey key = new PublisherKey(message.getPublisherId(), message.getVersion(), message.getDataGroup());
        SubmissionPublisher<Message> publisher = this.submissionPublishers.get(key);
        if (publisher == null) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20002,
                    String.format("Can't found the publisher (publisherId: %s, version: %s, dataGroup: %s)", message.getPublisherId(), message.getVersion(),
                            message.getDataGroup() == null ? "NULL" : message.getDataGroup()));
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
        // load submission publisher
        loadSubmissionPublisher(publisher);
    }

    private void loadSubmissionPublisher(IPublisher publisher) {
        if (publisher.isSupportDataGroup()) {
            int page = 0;
            List<String> dataGroups = publisher.findDataGroups(page, PAGE_SIZE);
            while (dataGroups != null && dataGroups.size() > 0) {
                for (String dataGroup : dataGroups) {
                    this.submissionPublishers.put(new PublisherKey(publisher.getPublisherId(), publisher.getVersion(), dataGroup),
                            new SubmissionPublisher<Message>());
                }
                if (dataGroups.size() < PAGE_SIZE) {
                    break;
                }
                page++;
                dataGroups = publisher.findDataGroups(page, PAGE_SIZE);
            }
        } else {
            this.submissionPublishers.put(new PublisherKey(publisher.getPublisherId(), publisher.getVersion(), null), new SubmissionPublisher<Message>());
        }
    }

    @Override
    void unloadPublisher(IPublisher publisher) {
        removeSubmissionPublisher(publisher);
        this.publishersLoaded.remove(publisher.getPublisherId());
    }

    private void removeSubmissionPublisher(IPublisher publisher) {
        if (publisher.isSupportDataGroup()) {
            int page = 0;
            List<String> dataGroups = publisher.findDataGroups(page, PAGE_SIZE);
            while (dataGroups != null && dataGroups.size() > 0) {
                for (String dataGroup : dataGroups) {
                    removeSubmissionPublisher(new PublisherKey(publisher.getPublisherId(), publisher.getVersion(), dataGroup));
                }
                if (dataGroups.size() < PAGE_SIZE) {
                    break;
                }
                page++;
                dataGroups = publisher.findDataGroups(page, PAGE_SIZE);
            }
        } else {
            removeSubmissionPublisher(new PublisherKey(publisher.getPublisherId(), publisher.getVersion(), null));
        }
    }

    private void removeSubmissionPublisher(PublisherKey key) {
        SubmissionPublisher<Message> pub = this.submissionPublishers.get(key);
        if (pub != null) {
            pub.close();
            this.submissionPublishers.remove(key);
        }
    }

    @Override
    void loadSubscriber(ISubscriber subscriber) {
        // don't need add again if already added
        if (this.submissionSubscribers.containsKey(subscriber.getSubscriberId())) {
            return;
        }
        // construct and load subscriber
        loadSubmissionSubscriber(subscriber);
    }

    private void loadSubmissionSubscriber(ISubscriber subscriber) {
        List<InMemorySubscriber> ssubscribers = this.submissionSubscribers.get(subscriber.getSubscriberId());
        if (ssubscribers == null) {
            synchronized (subscriber.getSubscriberId().intern()) {
                ssubscribers = this.submissionSubscribers.get(subscriber.getSubscriberId());
                if (ssubscribers == null) {
                    ssubscribers = new ArrayList<InMemorySubscriber>();
                    this.submissionSubscribers.put(subscriber.getSubscriberId(), ssubscribers);
                }
            }
        }
        int page = 0;
        List<IWebhook> webhooks = subscriber.findWebhooks(page, PAGE_SIZE);
        while (webhooks != null && webhooks.size() > 0) {
            for (IWebhook webhook : webhooks) {
                loadSubmissionSubscriber(ssubscribers, webhook);
            }
            if (webhooks.size() < PAGE_SIZE) {
                break;
            }
            page++;
            webhooks = subscriber.findWebhooks(page, PAGE_SIZE);
        }
    }

    private void loadSubmissionSubscriber(List<InMemorySubscriber> ssubscribers, IWebhook webhook) {
        int page = 0;
        boolean hasDataGroup = false;
        List<IDataGroup> dataGroups = webhook.findSubscribedDataGroups(page, PAGE_SIZE);
        while (dataGroups != null && dataGroups.size() > 0) {
            if (!hasDataGroup) {
                hasDataGroup = true;
            }
            for (IDataGroup dataGroup : dataGroups) {
                loadSubmissionSubscriber(ssubscribers, webhook, dataGroup.getDataGroup());
            }
            if (dataGroups.size() < PAGE_SIZE) {
                break;
            }
            page++;
            dataGroups = webhook.findSubscribedDataGroups(page, PAGE_SIZE);
        }
        if (!hasDataGroup) {
            loadSubmissionSubscriber(ssubscribers, webhook, null);
        }
    }

    private void loadSubmissionSubscriber(List<InMemorySubscriber> ssubscribers, IWebhook webhook, String dataGroup) {
        PublisherKey key = new PublisherKey(webhook.getPublisherId(), webhook.getPublisherVersion(), dataGroup);
        SubmissionPublisher<Message> publisher = submissionPublishers.get(key);
        if (publisher != null) {
            InMemorySubscriber ssub = new InMemorySubscriber(webhook);
            publisher.subscribe(ssub);
            ssubscribers.add(ssub);
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
}
