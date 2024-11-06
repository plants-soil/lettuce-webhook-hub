package com.plantssoil.common.mq.active;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.plantssoil.common.mq.IMessage;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.exception.MessageQueueException;

/**
 * The IMessagePublisher implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:51:37 am
 */
public class MessagePublisher implements IMessagePublisher {
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
    public void publish(IMessage message) {
        if (message.getPublisherId() == null || message.getVersion() == null) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15001, "The [publisherId] or [version] should not be null!");
        }
        String queueName = String.format("QUEUE-%s-%s-%s", message.getPublisherId(), message.getVersion(),
                message.getDataGroup() == null ? "NULL" : message.getDataGroup());
        try (MessageProducer producer = session.createProducer(session.createQueue(queueName))) {
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            ObjectMessage om = session.createObjectMessage(message);
            producer.send(om);
        } catch (JMSException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15002, e);
        }
    }
}
