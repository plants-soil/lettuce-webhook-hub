package com.plantssoil.common.mq;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract message consumer which hold channel name & channel type &
 * consumer id & listeners
 * 
 * @author danialdy
 * @Date 6 Nov 2024 7:42:02 pm
 */
public abstract class AbstractMessageConsumer<T> implements IMessageConsumer<T> {
    private String channelName;
    private ChannelType channelType = ChannelType.QUEUE;
    private String consumerId;
    private List<IMessageListener<T>> listeners = new ArrayList<>();

    @Override
    public IMessageConsumer<T> channelName(String channelName) {
        this.channelName = channelName;
        return this;
    }

    @Override
    public IMessageConsumer<T> channelType(ChannelType channelType) {
        this.channelType = channelType;
        return this;
    }

    @Override
    public IMessageConsumer<T> addMessageListener(IMessageListener<T> listener) {
        this.listeners.add(listener);
        return this;
    }

    @Override
    public IMessageConsumer<T> consumerId(String consumerId) {
        this.consumerId = consumerId;
        return this;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public ChannelType getChannelType() {
        return channelType;
    }

    @Override
    public String getConsumerId() {
        return consumerId;
    }

    @Override
    public List<IMessageListener<T>> getListeners() {
        return listeners;
    }
}
