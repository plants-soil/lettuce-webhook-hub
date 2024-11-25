package com.plantssoil.common.mq;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract message consumer which hold queue name & consumer id & listeners
 * 
 * @author danialdy
 * @Date 6 Nov 2024 7:42:02 pm
 */
public abstract class AbstractMessageConsumer<T> implements IMessageConsumer<T> {
    private String queueName;
    private String consumerId;
    private List<IMessageListener<T>> listeners = new ArrayList<>();

    @Override
    public IMessageConsumer<T> queueName(String queueName) {
        this.queueName = queueName;
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

    /**
     * Get queue name
     * 
     * @return queue name
     */
    public String getQueueName() {
        return queueName;
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
    public List<IMessageListener<T>> getListeners() {
        return listeners;
    }
}
