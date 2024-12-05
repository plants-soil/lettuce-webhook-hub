package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.ISubscriber;

/**
 * The abstract registry implementation<br/>
 * All publishers / subscribers should be added when application startup, and
 * all information will lost when application shutdown
 * 
 * @author danialdy
 * @Date 3 Dec 2024 4:49:06 pm
 */
abstract class AbstractRegistry implements IRegistry {
    final static int PAGE_SIZE = 50;

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
    }

    @Override
    public void updatePublisher(IPublisher publisher) {
        removePublisher(publisher.getPublisherId());
        addPublisher(publisher);
    }

    @Override
    public void removePublisher(String publisherId) {
        this.publishers.remove(publisherId);
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
    }

    @Override
    public void updateSubscriber(ISubscriber subscriber) {
        removeSubscriber(subscriber.getSubscriberId());
        addSubscriber(subscriber);
    }

    @Override
    public void removeSubscriber(String subscriberId) {
        this.subscribers.remove(subscriberId);
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
