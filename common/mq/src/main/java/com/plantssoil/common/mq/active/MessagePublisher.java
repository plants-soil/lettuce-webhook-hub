package com.plantssoil.common.mq.active;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.exception.MessageQueueException;

/**
 * The IMessagePublisher implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:51:37 am
 */
class MessagePublisher<T> extends AbstractMessagePublisher<T> {
    private Session session;

    /**
     * Constructor mandatory, used for factory to initiate
     * 
     * @param pool MQ Connection Pool
     */
    protected MessagePublisher(Session session) {
        this.session = session;
    }

    @Override
    public void publish(T message) {
        if (this.getQueueName() == null) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15001, "The [queueName] should not be null!");
        }
        try (MessageProducer producer = session.createProducer(session.createQueue(this.getQueueName()))) {
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            TextMessage tm = session.createTextMessage(ObjectJsonSerializer.getInstance().serialize(message));
            producer.send(tm);
        } catch (JMSException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15002, e);
        }
    }
}
