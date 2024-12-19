package com.plantssoil.common.mq;

/**
 * Message Publisher<br/>
 * Message sender could publish message to message queue<br/>
 * This Publisher will choose which channel should be used to send message by
 * {@link IMessagePublisher#publisherId()} + {@link IMessagePublisher#version()}
 * + {@link IMessagePublisher#dataGroup()}<br/>
 * Subscribers will subscribe messages from different organization + version +
 * dataGroup binding<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 4:48:32 pm
 */
public interface IMessagePublisher<T> {
    /**
     * The queue name to be used to send message
     * 
     * @param queueName queue name
     * @return current publisher instance
     */
    public IMessagePublisher<T> queueName(String queueName);

    /**
     * Send object message to message queue<br/>
     * This Publisher will choose which channel should be used to send message by
     * {@link IMessagePublisher#publisherId()} + {@link IMessagePublisher#version()}
     * + {@link IMessagePublisher#dataGroup()}<br/>
     * Subscribers should subscribe messages from different organization + version +
     * dataGroup binding<br/>
     * 
     * @param message object to be sent
     */
    public void publish(T message);
}
