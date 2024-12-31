package com.plantssoil.common.mq.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.AbstractMessageConsumer;
import com.plantssoil.common.mq.ChannelType;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.exception.MessageQueueException;
import com.rabbitmq.client.Channel;

/**
 * The IMessageConsumer implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:37:13 pm
 */
class MessageConsumer<T> extends AbstractMessageConsumer<T> {
    private final static String EXCHANGE_NAME_DIRECT = "com.plantssoil.exchange.direct";
    private final static String EXCHANGE_NAME_TOPIC = "com.plantssoil.exchange.fanout";
    private Channel channel;

    /**
     * Constructor mandatory
     * 
     * @param channel channel to consume messages
     */
    protected MessageConsumer(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void consume(Class<T> clazz) {
        try {
            // declare queue and bind with exchange
            String exchangeName = null;
            String queueName = null;
            if (ChannelType.TOPIC == getChannelType()) {
                exchangeName = EXCHANGE_NAME_TOPIC;
                this.channel.exchangeDeclare(exchangeName, "fanout");
                queueName = this.channel.queueDeclare().getQueue();
            } else {
                exchangeName = EXCHANGE_NAME_DIRECT;
                queueName = getChannelName();
                this.channel.exchangeDeclare(exchangeName, "direct");
                this.channel.queueDeclare(queueName, false, false, false, null);
            }
            String routingKey = getChannelName();
            this.channel.queueBind(queueName, exchangeName, routingKey);

            // 每条消息的大小限制，0表示不限制, no limit message size
            int prefetchSize = 0;
            // MQ Server每次推送的消息的最大条数，0表示不限制, prefetch 1 message, to make consumer more
            // smoothly
            int prefetchCount = 1;
            // true 表示配置应用于整个通道，false表示只应用于消费级别, only affect current channel
            boolean global = false;
            channel.basicQos(prefetchSize, prefetchCount, global);

            // consume message with auto-ack
            this.channel.basicConsume(queueName, true, this.getConsumerId(), (consumerTag, message) -> {
                // receive message
                T msg = ObjectJsonSerializer.getInstance().unserialize(new String(message.getBody(), "UTF-8"), clazz);
                for (IMessageListener<T> listener : getListeners()) {
                    listener.onMessage(msg, getConsumerId());
                }
            }, (consumerTag) -> {
            });
        } catch (IOException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15011, e);
        }
    }

    @Override
    public void close() {
        try {
            this.channel.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}
