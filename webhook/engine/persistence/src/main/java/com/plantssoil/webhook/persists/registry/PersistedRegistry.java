package com.plantssoil.webhook.persists.registry;

import java.util.List;

import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.impl.AbstractRegistry;

public class PersistedRegistry extends AbstractRegistry {

    @Override
    public IPublisher findPublisher(String publisherId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IPublisher> findAllPublishers(int page, int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ISubscriber findSubscriber(String subscriberId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ISubscriber> findAllSubscribers(int page, int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void saveNewPublisher(IPublisher publisher) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void saveUpdatedPublisher(IPublisher publisher) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void deletePublisher(IPublisher publisher) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void saveNewSubscriber(ISubscriber subscriber) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void saveUpdatedSubscriber(ISubscriber subscriber) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void deleteSubscriber(ISubscriber subscriber) {
        // TODO Auto-generated method stub

    }

}
