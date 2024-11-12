package com.plantssoil.common.mq.rabbit;

import java.io.IOException;

import com.plantssoil.common.mq.AbstractMessageConsumer;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.SimpleMessage;
import com.plantssoil.common.mq.exception.MessageQueueException;
import com.rabbitmq.client.Channel;

/**
 * The IMessageConsumer implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:37:13 pm
 */
class MessageConsumer extends AbstractMessageConsumer {
    private final static String EXCHANGE_NAME = "com.plantssoil.message.exchange";
    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";
    private Channel channel;

    /**
     * Constructor mandatory
     * 
     * @param channel channel to consume messages
     */
    protected MessageConsumer(Channel channel) {
        this.channel = channel;
    }

    protected String createRoutingKey() {
        return String.format("%s%s%s%s%s", this.getPublisherId(), ROUTING_KEY_SEPARATOR, this.getVersion(), ROUTING_KEY_SEPARATOR,
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
    }

    protected String[] decreateRoutingKey(String routingKey) {
        return routingKey.split(ROUTING_KEY_SEPARATOR);
    }

    @Override
    public void consume() {
        String routingKey = createRoutingKey();
        // create exchange for every publiserId + version + dataGroup
        try {
            this.channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            // declare queue and bind with exchange
            String queueName = String.format("QUEUE-%s-%s-%s", this.getPublisherId(), this.getVersion(),
                    this.getDataGroup() == null ? "NULL" : this.getDataGroup());
            this.channel.queueDeclare(queueName, false, false, false, null);
            this.channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

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
                SimpleMessage msg = new SimpleMessage(this.getPublisherId(), this.getVersion(), this.getDataGroup(), this.getConsumerId(),
                        new String(message.getBody(), "UTF-8"));
                for (IMessageListener listener : this.getListeners()) {
                    listener.onMessage(msg);
                }
            }, (consumerTag) -> {
            });
        } catch (IOException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15011, e);
        }
    }

}
