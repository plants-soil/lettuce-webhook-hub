package com.plantssoil.webhook.core.impl;

import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.ISubscriber;

/**
 * The abstract registry implementation<br/>
 * Will notify webhook engine when add/update/remove publisher or
 * add/update/remove subscriber<br/>
 * 
 * @author danialdy
 * @Date 3 Dec 2024 4:49:06 pm
 */
public abstract class AbstractRegistry implements IRegistry {
    final static int PAGE_SIZE = 50;

    private AbstractEngine engine;

    void setEngine(AbstractEngine engine) {
        this.engine = engine;
    }

    AbstractEngine getEngine() {
        return this.engine;
    }

    @Override
    public void addPublisher(IPublisher publisher) {
        // add publisher into map
        this.engine.loadPublisher(publisher);
    }

    @Override
    public void updatePublisher(IPublisher publisher) {
        this.engine.reloadPublisher(publisher);
    }

    @Override
    public void removePublisher(IPublisher publisher) {
        this.engine.unloadPublisher(publisher);
    }

    @Override
    public void addSubscriber(ISubscriber subscriber) {
        this.engine.loadSubscriber(subscriber);
    }

    @Override
    public void updateSubscriber(ISubscriber subscriber) {
        this.engine.reloadSubscriber(subscriber);
    }

    @Override
    public void removeSubscriber(ISubscriber subscriber) {
        this.engine.unloadSubscriber(subscriber);
    }
}
