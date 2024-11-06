package com.plantssoil.common.mq.rabbit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.IMessageSubscriber;
import com.plantssoil.common.mq.exception.MessageQueueException;
import com.rabbitmq.client.Channel;

/**
 * The IMessageSubscriber implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:37:13 pm
 */
public class MessageSubscriber implements IMessageSubscriber {
    private final static String EXCHANGE_NAME = "com.plantssoil.message.exchange";
    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";
    private Channel channel;
    private String consumerId;
    private List<IMessageListener> listeners = new ArrayList<>();

    /**
     * Constructor mandatory
     * 
     * @param channel channel to subscribe messages
     */
    protected MessageSubscriber(Channel channel) {
        this.channel = channel;
    }

    protected String createRoutingKey(String publisherId, String version, String dataGroup) {
        return String.format("%s%s%s%s%s", publisherId, ROUTING_KEY_SEPARATOR, version, ROUTING_KEY_SEPARATOR, dataGroup == null ? "NULL" : dataGroup);
    }

    protected String[] decreateRoutingKey(String routingKey) {
        return routingKey.split(ROUTING_KEY_SEPARATOR);
    }

    @Override
    public void subscribe(String publisherId, String version, String dataGroup) {
        String routingKey = createRoutingKey(publisherId, version, dataGroup);
        // create exchange for every publiserId + version + dataGroup
        try {
            this.channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            // declare queue and bind with exchange
            String queueName = String.format("QUEUE-%s-%s-%s", publisherId, version, dataGroup == null ? "NULL" : dataGroup);
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

            // consume message without auto-ack
            this.channel.basicConsume(queueName, true, this.consumerId, (consumerTag, message) -> {
                String[] vs = decreateRoutingKey(message.getEnvelope().getRoutingKey());
                Message msg = new Message(vs[0], vs[1], "NULL".equals(vs[2]) ? null : vs[2], new String(message.getBody(), "UTF-8"));
                for (IMessageListener listener : listeners) {
                    listener.setConsumerId(consumerTag);
                    listener.onMessage(msg);
                }
            }, (consumerTag) -> {
            });
        } catch (IOException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15011, e);
        }
    }

    @Override
    public void addMessageListener(IMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

}
