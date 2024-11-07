package com.plantssoil.common.mq;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract message subscriber which hold publisher id + version + data
 * group + consumer id, and listeners
 * 
 * @author danialdy
 * @Date 6 Nov 2024 7:42:02 pm
 */
public abstract class AbstractMessageSubscriber implements IMessageSubscriber {
    private String publisherId;
    private String version;
    private String dataGroup;
    private String consumerId;
    private List<IMessageListener> listeners = new ArrayList<>();

    @Override
    public IMessageSubscriber publisherId(String publisherId) {
        this.publisherId = publisherId;
        return this;
    }

    @Override
    public IMessageSubscriber version(String version) {
        this.version = version;
        return this;
    }

    @Override
    public IMessageSubscriber dataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
        return this;
    }

    @Override
    public IMessageSubscriber addMessageListener(IMessageListener listener) {
        this.listeners.add(listener);
        return this;
    }

    @Override
    public IMessageSubscriber consumerId(String consumerId) {
        this.consumerId = consumerId;
        return this;
    }

    /**
     * Get the publisher id (organization id)
     * 
     * @return publisher id
     */
    protected String getPublisherId() {
        return publisherId;
    }

    /**
     * Get the publisher version
     * 
     * @return publisher version
     */
    protected String getVersion() {
        return version;
    }

    /**
     * Get data group, some organizations need separate data from business unit or
     * merchants
     * 
     * @return data group
     */
    protected String getDataGroup() {
        return dataGroup;
    }

    /**
     * Get consumer id, if provided
     * 
     * @return consumer id
     */
    protected String getConsumerId() {
        return consumerId;
    }

    /**
     * Get message listeners registered to the subscriber
     * 
     * @return message listeners
     */
    protected List<IMessageListener> getListeners() {
        return listeners;
    }
}
