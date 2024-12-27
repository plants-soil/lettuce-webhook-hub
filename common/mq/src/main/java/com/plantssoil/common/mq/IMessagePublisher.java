package com.plantssoil.common.mq;

/**
 * Message Publisher<br/>
 * Message sender could publish message to message channel<br/>
 * This Publisher will choose which channel should be used to send message by
 * the channelName {@link IMessagePublisher#channelName(String)}<br/>
 * Subscribers will subscribe messages from the same channel named by
 * channelName<br/>
 * Need put IMessagePublisher object into try-catch-resource clause to ensure
 * the object is closed properly after all operations done:<br/>
 * 
 * <pre>
 * <code>
 * IMessageServiceFactory<Message> f = IMessageServiceFactory.getFactoryInstance();
 * try (IMessagePublisher<Message> publisher = f.createMessagePublisher().channelName("PUBLISHER-ID-01-V1.0")) {
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
     * The channel name to send message
     * 
     * @param channelName channel name
     * @return current publisher instance
     */
    public IMessagePublisher<T> channelName(String channelName);

    /**
     * The channel type to send message, defaults to {@link ChannelType#QUEUE}
     * 
     * @param channelType channel type
     * @return current publisher instance
     */
    public IMessagePublisher<T> channelType(ChannelType channelType);

    /**
     * Send object message to message channel<br/>
     * This Publisher will choose which channel should be used to send message by
     * the channelName {@link IMessagePublisher#channelName(String)}<br/>
     * Subscribers should subscribe messages from the same channel named by
     * channelName<br/>
     * 
     * @param message object to be sent
     */
    public void publish(T message);
}
