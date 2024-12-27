package com.plantssoil.common.mq.active;

import javax.jms.JMSException;
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
    private MessageReceiver<T> mc;

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
        this.mc = new MessageReceiver<T>(this.session, getChannelName(), getChannelType(), getConsumerId(), getListeners(), clazz);
        new Thread(this.mc, String.format("MQ Subscriber: %s", getChannelName())).start();
    }

    @Override
    public void close() {
        this.mc.close();
        try {
            this.session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
