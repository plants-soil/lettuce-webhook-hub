package com.plantssoil.common.mq.active;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.exception.MessageQueueException;

/**
 * The IMessagePublisher implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:51:37 am
 */
class MessagePublisher extends AbstractMessagePublisher {
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
    public void publish(String message) {
        if (this.getPublisherId() == null || this.getVersion() == null) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15001, "The [publisherId] or [version] should not be null!");
        }
        String queueName = String.format("QUEUE-%s-%s-%s", this.getPublisherId(), this.getVersion(),
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
        try (MessageProducer producer = session.createProducer(session.createQueue(queueName))) {
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            TextMessage tm = session.createTextMessage(message);
            producer.send(tm);
        } catch (JMSException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15002, e);
        }
    }
}
