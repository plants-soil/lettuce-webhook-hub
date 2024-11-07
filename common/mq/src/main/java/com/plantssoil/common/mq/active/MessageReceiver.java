package com.plantssoil.common.mq.active;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.SimpleMessage;
import com.plantssoil.common.mq.exception.MessageQueueException;

class MessageReceiver implements Runnable {
    private Session session;
    private String queueName;
    private String publisherId;
    private String version;
    private String dataGroup;
    private String consumerTag;
    private List<IMessageListener> listeners;

    public MessageReceiver(Session session, String queueName, String publisherId, String version, String dataGroup, String consumerTag,
            List<IMessageListener> listeners) {
        this.session = session;
        this.queueName = queueName;
        this.publisherId = publisherId;
        this.version = version;
        this.dataGroup = dataGroup;
        this.consumerTag = consumerTag;
        this.listeners = listeners;
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
                String text = ((TextMessage) msg).getText();

                SimpleMessage message = new SimpleMessage(this.publisherId, this.version, this.dataGroup, this.consumerTag, text);
                for (IMessageListener listener : listeners) {
                    listener.onMessage(message);
                }
            }
        } catch (JMSException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15003, e);
        }
    }
}
