package com.plantssoil.common.mq.active;

import javax.jms.Session;

import com.plantssoil.common.mq.AbstractMessageSubscriber;

/**
 * The IMessageSubscriber implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:37:13 pm
 */
public class MessageSubscriber extends AbstractMessageSubscriber {
    private Session session;

    /**
     * Constructor mandatory, used for factory to initiate
     * 
     * @param pool
     */
    protected MessageSubscriber(Session session) {
        this.session = session;
    }

    @Override
    public void subscribe() {
        String queueName = String.format("QUEUE-%s-%s-%s", this.getPublisherId(), this.getVersion(),
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
        MessageReceiver mc = new MessageReceiver(this.session, queueName, this.getPublisherId(), this.getVersion(), this.getDataGroup(), this.getConsumerId(),
                this.getListeners());
        new Thread(mc, String.format("MQ Subscriber: %s", queueName)).start();
    }

}
