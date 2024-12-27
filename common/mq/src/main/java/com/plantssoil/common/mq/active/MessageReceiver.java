package com.plantssoil.common.mq.active;

import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.ChannelType;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.exception.MessageQueueException;

class MessageReceiver<T> implements Runnable {
    private Session session;
    private String channelName;
    private ChannelType channelType;
    private String consumerId;
    private List<IMessageListener<T>> listeners;
    private Class<T> clazz;
    private boolean closed = false;

    public MessageReceiver(Session session, String channelName, ChannelType channelType, String consumerId, List<IMessageListener<T>> listeners,
            Class<T> clazz) {
        this.session = session;
        this.channelName = channelName;
        this.channelType = channelType;
        this.consumerId = consumerId;
        this.listeners = listeners;
        this.clazz = clazz;
    }

    @Override
    public void run() {
        Destination d;
        try {
            d = ChannelType.TOPIC == this.channelType ? this.session.createTopic(this.channelName) : this.session.createQueue(this.channelName);
        } catch (JMSException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15003, e);
        }
        try (MessageConsumer consumer = this.session.createConsumer(d)) {
            while (!this.closed) {
                javax.jms.Message msg = null;
                // try to receive message from consumer
                // will re-try 3 times if connection is broken and exit if still can't
                // re-connect
                try {
                    msg = consumer.receive();
                } catch (javax.jms.IllegalStateException e) {
                    try {
                        Thread.sleep(1000);
                        msg = consumer.receive();
                    } catch (InterruptedException | javax.jms.IllegalStateException e1) {
                        try {
                            Thread.sleep(3000);
                            msg = consumer.receive();
                        } catch (InterruptedException | javax.jms.IllegalStateException e2) {
                            continue;
                        }
                    }
                }
                if (msg == null || !(msg instanceof TextMessage)) {
                    continue;
                }
                T message = ObjectJsonSerializer.getInstance().unserialize(((TextMessage) msg).getText(), clazz);
                for (IMessageListener<T> listener : listeners) {
                    listener.onMessage(message, consumerId);
                }
            }
        } catch (JMSException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15003, e);
        }
    }

    public void close() {
        this.closed = true;
    }
}
