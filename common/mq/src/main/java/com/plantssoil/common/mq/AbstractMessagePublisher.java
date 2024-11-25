package com.plantssoil.common.mq;

/**
 * The abstract message publisher which hold queue name
 * 
 * @author danialdy
 * @Date 6 Nov 2024 7:37:19 pm
 */
public abstract class AbstractMessagePublisher<T> implements IMessagePublisher<T> {
    private String queueName;

    @Override
    public IMessagePublisher<T> queueName(String queueName) {
        this.queueName = queueName;
        return this;
    }

    public String getQueueName() {
        return queueName;
    }

}
