package com.plantssoil.common.mq;

/**
 * Message Publisher<br/>
 * Message sender could publish message to message queue<br/>
 * Messages are separated by {@link IMessage#getPublisherId()} +
 * {@link IMessage#getVersion()} {@link IMessage#getDataGroup()}<br/>
 * Subscribers will subscribe messages from different organization + version +
 * dataGroup binding<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 4:48:32 pm
 */
public interface IMessagePublisher {
    /**
     * Send message to message queue<br/>
     * Messages are separated by {@link IMessage#getPublisherId()} +
     * {@link IMessage#getVersion()} {@link IMessage#getDataGroup()}<br/>
     * Subscribers will subscribe messages from different organization + version +
     * dataGroup binding<br/>
     * 
     * @param message informations to be sent
     */
    public void publish(IMessage message);
}
