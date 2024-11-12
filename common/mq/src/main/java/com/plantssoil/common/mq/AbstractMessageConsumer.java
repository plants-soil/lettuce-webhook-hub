package com.plantssoil.common.mq;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract message consumer which hold publisher id + version + data group
 * + consumer id, and listeners
 * 
 * @author danialdy
 * @Date 6 Nov 2024 7:42:02 pm
 */
public abstract class AbstractMessageConsumer implements IMessageConsumer {
    private String publisherId;
    private String version;
    private String dataGroup;
    private String consumerId;
    private List<IMessageListener> listeners = new ArrayList<>();

    @Override
    public IMessageConsumer publisherId(String publisherId) {
        this.publisherId = publisherId;
        return this;
    }

    @Override
    public IMessageConsumer version(String version) {
        this.version = version;
        return this;
    }

    @Override
    public IMessageConsumer dataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
        return this;
    }

    @Override
    public IMessageConsumer addMessageListener(IMessageListener listener) {
        this.listeners.add(listener);
        return this;
    }

    @Override
    public IMessageConsumer consumerId(String consumerId) {
        this.consumerId = consumerId;
        return this;
    }

    /**
     * Get the publisher id (organization id)
     * 
     * @return publisher id
     */
    public String getPublisherId() {
        return publisherId;
    }

    /**
     * Get the publisher version
     * 
     * @return publisher version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get data group, some organizations need separate data from business unit or
     * merchants
     * 
     * @return data group
     */
    public String getDataGroup() {
        return dataGroup;
    }

    /**
     * Get consumer id, if provided
     * 
     * @return consumer id
     */
    public String getConsumerId() {
        return consumerId;
    }

    /**
     * Get message listeners registered to the subscriber
     * 
     * @return message listeners
     */
    public List<IMessageListener> getListeners() {
        return listeners;
    }
}
