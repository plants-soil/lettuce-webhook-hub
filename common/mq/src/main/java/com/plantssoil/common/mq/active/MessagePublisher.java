package com.plantssoil.common.mq.active;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.ChannelType;
import com.plantssoil.common.mq.exception.MessageQueueException;

/**
 * The IMessagePublisher implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:51:37 am
 */
class MessagePublisher<T> extends AbstractMessagePublisher<T> {
    private Session session;
    private MessageProducer producer;

    /**
     * Constructor mandatory, used for factory to initiate
     * 
     * @param pool MQ Connection Pool
     */
    protected MessagePublisher(Session session) {
        this.session = session;
    }

    private MessageProducer getMessageProducer() throws JMSException {
        if (this.producer == null) {
            synchronized (this) {
                if (this.producer == null) {
                    Destination d = ChannelType.TOPIC == getChannelType() ? this.session.createTopic(getChannelName())
                            : this.session.createQueue(getChannelName());
                    this.producer = this.session.createProducer(d);
                }
            }
        }
        return this.producer;
    }

    @Override
    public void publish(T message) {
        if (this.getChannelName() == null) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15001, "The [channelName] should not be null!");
        }
        try (MessageProducer producer = getMessageProducer()) {
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            TextMessage tm = this.session.createTextMessage(ObjectJsonSerializer.getInstance().serialize(message));
            producer.send(tm);
        } catch (JMSException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15002, e);
        }
    }

    @Override
    public void close() throws Exception {
        if (this.producer == null) {
            this.producer.close();
        }
        this.session.close();
    }
}
