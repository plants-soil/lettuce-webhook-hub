package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SubmissionPublisher;

import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;

/**
 * The in-memory registry implementation<br/>
 * <p>
 * In-memory registry should load publishers / subscribers when application
 * startup, and all data will be lost when application shutdown
 * </p>
 * 
 * @author danialdy
 * @Date 3 Dec 2024 4:49:06 pm
 */
class InMemoryRegistry extends AbstractRegistry {
    /**
     * key - The DefaultPublisherKey(publisherId + version + dataGroup), value -
     * SubmissionPublisher<DefaultMessage>
     */
    private Map<PublisherKey, SubmissionPublisher<Message>> submissionPublishers = new ConcurrentHashMap<>();
    /**
     * key - the ISubscriber.getSubscriberId(), value - list of InMemorySubscriber
     */
    private Map<String, List<InMemorySubscriber>> submissionSubscribers = new ConcurrentHashMap<>();

    @Override
    public void addPublisher(IPublisher publisher) {
        // don't need add again if already added
        if (findSubscriber(publisher.getPublisherId()) != null) {
            return;
        }
        super.addPublisher(publisher);
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
    public void removePublisher(String publisherId) {
        IPublisher publisher = findPublisher(publisherId);
        if (publisher == null) {
            return;
        }

        removeSubmissionPublisher(publisher);
        super.removePublisher(publisherId);
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

    SubmissionPublisher<Message> getPublisher(String publisherId, String version, String dataGroup) {
        PublisherKey key = new PublisherKey(publisherId, version, dataGroup);
        return this.submissionPublishers.get(key);
    }

    @Override
    public void addSubscriber(ISubscriber subscriber) {
        // don't need add again if already added
        if (findSubscriber(subscriber.getSubscriberId()) != null) {
            return;
        }
        super.addSubscriber(subscriber);
        // construct and load subscriber
        loadSubmissionSubscriber(subscriber);
    }

    private void loadSubmissionSubscriber(ISubscriber subscriber) {
        List<InMemorySubscriber> ssubscribers = this.submissionSubscribers.get(subscriber.getSubscriberId());
        if (ssubscribers == null) {
            synchronized (subscriber.getSubscriberId()) {
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
        List<String> dataGroups = webhook.findSubscribedDataGroups(page, PAGE_SIZE);
        while (dataGroups != null && dataGroups.size() > 0) {
            for (String dataGroup : dataGroups) {
                loadSubmissionSubscriber(ssubscribers, webhook, dataGroup);
            }
            if (dataGroups.size() < PAGE_SIZE) {
                break;
            }
            page++;
            dataGroups = webhook.findSubscribedDataGroups(page, PAGE_SIZE);
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
    public void removeSubscriber(String subscriberId) {
        List<InMemorySubscriber> ssubscribers = this.submissionSubscribers.get(subscriberId);
        if (ssubscribers == null) {
            return;
        }

        for (InMemorySubscriber ssubscriber : ssubscribers) {
            ssubscriber.unsubscribe();
        }
        ssubscribers.clear();
        this.submissionSubscribers.remove(subscriberId);
        super.removeSubscriber(subscriberId);
    }
}
