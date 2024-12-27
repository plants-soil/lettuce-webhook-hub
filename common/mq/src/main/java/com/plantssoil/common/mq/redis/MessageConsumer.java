package com.plantssoil.common.mq.redis;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.AbstractMessageConsumer;
import com.plantssoil.common.mq.ChannelType;
import com.plantssoil.common.mq.IMessageListener;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;

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
                if (message.getMessage() == null) {
                    return;
                }
                consumeMessage(message.getMessage(), clazz);
            }).doOnError(ex -> {
                ex.printStackTrace();
            }).subscribe();
            this.command.subscribe(getChannelName()).subscribe();
        } else {
            this.command.observeChannels().doOnNext((message) -> {
                String listName = message.getMessage();
                if (listName == null || !listName.equals(getChannelName())) {
                    return;
                }
                consumeListMessage(clazz);
            }).doOnError(ex -> {
                ex.printStackTrace();
            }).subscribe();
            this.command.subscribe(LETTUCE_QUEUE_NOTIFICATION_CHANNEL).subscribe();
        }
    }

    private void consumeListMessage(Class<T> clazz) {
        this.consumerConnection.async().rpop(getChannelName()).thenAccept(t -> {
            if (t == null) {
                return;
            }
            consumeMessage(t, clazz);
        });
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
