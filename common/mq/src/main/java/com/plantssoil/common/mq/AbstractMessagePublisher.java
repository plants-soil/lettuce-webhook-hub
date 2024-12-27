package com.plantssoil.common.mq;

/**
 * The abstract message publisher which hold channel name & channel type
 * 
 * @author danialdy
 * @Date 6 Nov 2024 7:37:19 pm
 */
public abstract class AbstractMessagePublisher<T> implements IMessagePublisher<T> {
    private String channelName;
    private ChannelType channelType = ChannelType.QUEUE;

    @Override
    public IMessagePublisher<T> channelName(String channelName) {
        this.channelName = channelName;
        return this;
    }

    @Override
    public IMessagePublisher<T> channelType(ChannelType channelType) {
        this.channelType = channelType;
        return this;
    }

    public String getChannelName() {
        return channelName;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

}
