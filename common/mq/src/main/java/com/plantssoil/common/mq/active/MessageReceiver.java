package com.plantssoil.common.mq.active;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.exception.MessageQueueException;

class MessageReceiver implements Runnable {
    private Session session;
    private String consumerTag;
    private String queueName;
    private List<IMessageListener> listeners;

    public MessageReceiver(Session session, String consumerTag, String queueName, List<IMessageListener> listeners) {
        this.session = session;
        this.consumerTag = consumerTag;
        this.queueName = queueName;
        this.listeners = listeners;
    }

    @Override
    public void run() {
        try (MessageConsumer consumer = session.createConsumer(session.createQueue(queueName))) {
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
                if (msg == null || !(msg instanceof ObjectMessage)) {
                    continue;
                }
                java.io.Serializable serializable = ((ObjectMessage) msg).getObject();
                if (serializable == null || !(serializable instanceof Message)) {
                    continue;
                }

                Message message = (Message) serializable;
                for (IMessageListener listener : listeners) {
                    listener.setConsumerId(consumerTag);
                    listener.onMessage(message);
                }
            }
        } catch (JMSException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15003, e);
        }
    }
}
