package com.plantssoil.common.mq;

/**
 * Listen the message comes from subscribed MQ topic<br/>
 * Will process the messages<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 5:09:28 pm
 */
public interface IMessageListener {
    /**
     * Get the consumer id<br/>
     * This will be null if not set consumerId via
     * {@link IMessageSubscriber#setConsumerId(String)} when subscribe messages
     * 
     * @return
     */
    public String getConsumerId();

    /**
     * Set the consumer id<br/>
     * 
     * @param consumerId This will be null if not set consumerId via
     *                   {@link IMessageSubscriber#setConsumerId(String)} when
     *                   subscribe messages
     * @return
     */
    public void setConsumerId(String consumerId);

    /**
     * MQ will call this method after message received<br/>
     * Could process the messages in this method<br/>
     * 
     * @param message message comes from publisher
     */
    public void onMessage(IMessage message);
}
