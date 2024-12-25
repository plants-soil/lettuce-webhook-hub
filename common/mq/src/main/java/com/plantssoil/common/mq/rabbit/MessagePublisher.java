package com.plantssoil.common.mq.rabbit;

import java.io.IOException;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.exception.MessageQueueException;
import com.rabbitmq.client.Channel;

/**
 * The IMessagePublisher implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:51:37 am
 */
class MessagePublisher<T> extends AbstractMessagePublisher<T> {
    private final static String EXCHANGE_NAME = "com.plantssoil.message.exchange";
    private Channel channel;

    /**
     * Constructor mandatory, used for factory to initiate
     * 
     * @param channel MQ Connection channel
     */
    protected MessagePublisher(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void publish(T message) {
        if (getQueueName() == null) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15008, "The [queueName] should not be null!");
        }
        try {
            // create exchange for every publiserId + version
            String routingKey = getQueueName();
            this.channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            this.channel.basicPublish(EXCHANGE_NAME, routingKey, null, ObjectJsonSerializer.getInstance().serialize(message).getBytes("UTF-8"));
        } catch (IOException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15009, e);
        }
    }

    @Override
    public void close() throws Exception {
        if (this.channel.isOpen()) {
            this.channel.close();
        }
    }
}
