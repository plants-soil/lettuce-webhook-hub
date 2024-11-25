package com.plantssoil.common.mq.active;

import javax.jms.Session;

import com.plantssoil.common.mq.AbstractMessageConsumer;

/**
 * The IMessageConsumer implementation base on Active MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:37:13 pm
 */
class MessageConsumer<T> extends AbstractMessageConsumer<T> {
    private Session session;

    /**
     * Constructor mandatory, used for factory to initiate
     * 
     * @param pool
     */
    protected MessageConsumer(Session session) {
        this.session = session;
    }

    @Override
    public void consume(Class<T> clazz) {
        MessageReceiver<T> mc = new MessageReceiver<T>(this.session, getQueueName(), getConsumerId(), getListeners(), clazz);
        new Thread(mc, String.format("MQ Subscriber: %s", getQueueName())).start();
    }

}
