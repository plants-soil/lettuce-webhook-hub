package com.plantssoil.common.mq.active;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.exception.MessageQueueException;

class MessageReceiver<T> implements Runnable {
    private Session session;
    private String queueName;
    private String consumerId;
    private List<IMessageListener<T>> listeners;
    private Class<T> clazz;

    public MessageReceiver(Session session, String queueName, String consumerId, List<IMessageListener<T>> listeners, Class<T> clazz) {
        this.session = session;
        this.queueName = queueName;
        this.consumerId = consumerId;
        this.listeners = listeners;
        this.clazz = clazz;
    }

    @Override
    public void run() {
        try (MessageConsumer consumer = this.session.createConsumer(this.session.createQueue(this.queueName))) {
            while (true) {
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
                            break;
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
}
