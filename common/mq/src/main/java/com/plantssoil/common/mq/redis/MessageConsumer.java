package com.plantssoil.common.mq.redis;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.AbstractMessageConsumer;
import com.plantssoil.common.mq.ChannelType;
import com.plantssoil.common.mq.IMessageListener;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;

/**
 * The IMessageSubscriber implementation base on Redis PubSub & List
 * 
 * @param <T> the message type
 * @author danialdy
 * @Date 4 Jan 2025 4:09:26 pm
 */
class MessageConsumer<T> extends AbstractMessageConsumer<T> {
    private final static String LETTUCE_QUEUE_NOTIFICATION_CHANNEL = "com.plantssoil.mq.notification.channel";
    private RedisPubSubReactiveCommands<String, String> command;
    private StatefulRedisConnection<String, String> consumerConnection;

    MessageConsumer(RedisPubSubReactiveCommands<String, String> command, StatefulRedisConnection<String, String> consumerConnection) {
        this.command = command;
        this.consumerConnection = consumerConnection;
    }

    @Override
    public void consume(Class<T> clazz) {
        if (ChannelType.TOPIC == getChannelType()) {
            this.command.observeChannels().doOnNext((message) -> {
                if (message.getMessage() == null || !message.getChannel().equals(getChannelName())) {
                    return;
                }
                consumeMessage(message.getMessage(), clazz);
            }).doOnError(ex -> {
                ex.printStackTrace();
            }).subscribe();
            this.command.subscribe(getChannelName()).subscribe();
        } else {
            this.command.observeChannels().doOnNext((message) -> {
                if (!message.getChannel().equals(LETTUCE_QUEUE_NOTIFICATION_CHANNEL)) {
                    return;
                }
                String listName = message.getMessage();
                if (listName == null || !listName.equals(getChannelName())) {
                    return;
                }
                consumeListMessage(clazz, listName);
            }).doOnError(ex -> {
                ex.printStackTrace();
            }).subscribe();
            this.command.subscribe(LETTUCE_QUEUE_NOTIFICATION_CHANNEL).subscribe();
        }
    }

    private void consumeListMessage(Class<T> clazz, String listName) {
        String v = this.consumerConnection.sync().rpop(listName);
        while (v != null) {
            consumeMessage(v, clazz);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            v = this.consumerConnection.sync().rpop(listName);
        }
    }

    private void consumeMessage(String t, Class<T> clazz) {
        T message = ObjectJsonSerializer.getInstance().unserialize(t, clazz);
        for (IMessageListener<T> l : getListeners()) {
            l.onMessage(message, getConsumerId());
        }
    }

    @Override
    public void close() {
    }

}
