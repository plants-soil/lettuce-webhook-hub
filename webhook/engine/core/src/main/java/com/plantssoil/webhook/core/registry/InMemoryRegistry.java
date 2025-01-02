package com.plantssoil.webhook.core.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.impl.AbstractRegistry;

/**
 * The in-memory registry implementation<br/>
 * <p>
 * In-memory registry will keep publishers / subscribers in memory, and all data
 * will be lost when JVM shutdown<br/>
 * It's only for demonstration purpose, SHOULD AVOID be used in production
 * environment<br/>
 * </p>
 * 
 * @author danialdy
 * @Date 3 Dec 2024 4:49:06 pm
 */
public class InMemoryRegistry extends AbstractRegistry {
    /**
     * key - The IPublisher.getPublisherId(), value - IPublisher
     */
    private Map<String, IPublisher> publishers = new ConcurrentHashMap<>();
    /**
     * key - The ISubscriber.getSubscriberId(), value - ISubscriber
     */
    private Map<String, ISubscriber> subscribers = new ConcurrentHashMap<>();

    @Override
    public void addPublisher(IPublisher publisher) {
        // add publisher into map
        this.publishers.put(publisher.getPublisherId(), publisher);
        super.addPublisher(publisher);
    }

    @Override
    public void updatePublisher(IPublisher publisher) {
        removePublisher(publisher);
        addPublisher(publisher);
        super.updatePublisher(publisher);
    }

    @Override
    public void removePublisher(IPublisher publisher) {
        this.publishers.remove(publisher.getPublisherId());
        super.removePublisher(publisher);
    }

    @Override
    public IPublisher findPublisher(String publisherId) {
        return this.publishers.get(publisherId);
    }

    @Override
    public List<IPublisher> findAllPublishers(int page, int pageSize) {
        List<IPublisher> list = new ArrayList<>();
        int i = 0;
        int startIndex = page * pageSize;
        int endIndex = startIndex + pageSize;
        for (Entry<String, IPublisher> entry : this.publishers.entrySet()) {
            if (i >= startIndex && i < endIndex) {
                list.add(entry.getValue());
            }
            i++;
        }
        return list;
    }

    @Override
    public void addSubscriber(ISubscriber subscriber) {
        // add subscriber into map
        this.subscribers.put(subscriber.getSubscriberId(), subscriber);
        super.addSubscriber(subscriber);
    }

    @Override
    public void updateSubscriber(ISubscriber subscriber) {
        removeSubscriber(subscriber);
        addSubscriber(subscriber);
        super.updateSubscriber(subscriber);
    }

    @Override
    public void removeSubscriber(ISubscriber subscriber) {
        this.subscribers.remove(subscriber.getSubscriberId());
        super.removeSubscriber(subscriber);
    }

    @Override
    public ISubscriber findSubscriber(String subscriberId) {
        return this.subscribers.get(subscriberId);
    }

    @Override
    public List<ISubscriber> findAllSubscribers(int page, int pageSize) {
        List<ISubscriber> list = new ArrayList<>();
        int i = 0;
        int startIndex = page * pageSize;
        int endIndex = startIndex + pageSize;
        for (Entry<String, ISubscriber> entry : this.subscribers.entrySet()) {
            if (i >= startIndex && i < endIndex) {
                list.add(entry.getValue());
            }
            i++;
        }
        return list;
    }
}
