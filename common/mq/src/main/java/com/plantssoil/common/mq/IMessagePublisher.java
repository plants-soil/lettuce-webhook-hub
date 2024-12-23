package com.plantssoil.common.mq;

/**
 * Message Publisher<br/>
 * Message sender could publish message to message queue<br/>
 * This Publisher will choose which channel should be used to send message by
 * the queueName {@link IMessagePublisher#queueName(String)}<br/>
 * Subscribers will subscribe messages from the same channel named by
 * queueName<br/>
 * Need put IMessagePublisher object into try-catch-resource clause to ensure
 * the object is closed properly after all operations done:<br/>
 * 
 * <pre>
 * <code>
 * IMessageServiceFactory<Message> f = IMessageServiceFactory.getFactoryInstance();
 * try (IMessagePublisher<Message> publisher = f.createMessagePublisher().queueName("PUBLISHER-ID-01-V1.0")) {
 *   ...
 * }
 * catch (Exception e) {
 *   ...
 * }
 * </code>
 * </pre>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 4:48:32 pm
 */
public interface IMessagePublisher<T> extends AutoCloseable {
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
     * the queueName {@link IMessagePublisher#queueName(String)}<br/>
     * Subscribers should subscribe messages from the same channel named by
     * queueName<br/>
     * 
     * @param message object to be sent
     */
    public void publish(T message);
}
