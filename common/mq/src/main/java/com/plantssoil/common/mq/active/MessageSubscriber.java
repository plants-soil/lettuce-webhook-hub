package com.plantssoil.common.mq.active;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Session;

import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.IMessageSubscriber;

/**
 * The IMessageSubscriber implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:37:13 pm
 */
public class MessageSubscriber implements IMessageSubscriber {
    private Session session;
    private String consumerId;
    private List<IMessageListener> listeners = new ArrayList<>();

    /**
     * Constructor mandatory, used for factory to initiate
     * 
     * @param pool
     */
    protected MessageSubscriber(Session session) {
        this.session = session;
    }

    @Override
    public void subscribe(String publisherId, String version, String dataGroup) {
        String queueName = String.format("QUEUE-%s-%s-%s", publisherId, version, dataGroup == null ? "NULL" : dataGroup);
        MessageReceiver mc = new MessageReceiver(this.session, this.consumerId, queueName, listeners);
        new Thread(mc, String.format("MQ Subscriber: %s", queueName)).start();
    }

    @Override
    public void addMessageListener(IMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

}
