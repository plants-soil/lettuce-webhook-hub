package com.plantssoil.common.mq;

import java.util.List;

/**
 * Consume message from specific publisher + version + data group<br/>
 * Consumer could get messages from Message Queue Server which publisher created
 * (so the MQ Queue Name should be consistent with publisher's Queue Name)<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 4:55:17 pm
 */
public interface IMessageConsumer<T> {
    public IMessageConsumer<T> queueName(String queueName);

    /**
     * Add the listener which to receive and process messages from publisher<br/>
     * Could call this method multi-times to add multiple listeners<br/>
     * 
     * @param listener the message receiver and handler
     * @return current consumer instance
     */
    public IMessageConsumer<T> addMessageListener(IMessageListener<T> listener);

    /**
     * set the consumer id which will be brought to {@link IStringMessageListener}
     * <br/>
     * if not set consumer id via this method, null consumerTag will be passed to
     * {@link DeliverCallback#}<br/>
     * 
     * @param consumerId consumer id
     * @return current consumer instance
     */
    public IMessageConsumer<T> consumerId(String consumerId);

    /**
     * Consume the message from the specific queue<br/>
     * Consumer could get message from Message Queue Server which publisher created
     * (so the MQ queue name should be consistent with publisher)<br/>
     * 
     * @param clazz the Message Object class,
     *              {@link IMessageListener#onMessage(Object, String)} will receive
     *              the instance of this class as the first parameter
     * 
     */
    public void consume(Class<T> clazz);

    /**
     * Get queue name
     * 
     * @return queue name
     */
    public String getQueueName();

    /**
     * Get consumer id, if provided
     * 
     * @return consumer id
     */
    public String getConsumerId();

    /**
     * Get message listener list
     * 
     * @return message listener list
     */
    public List<IMessageListener<T>> getListeners();

    /**
     * Close the consumer and stop receive message from publisher
     */
    public void close();
}
