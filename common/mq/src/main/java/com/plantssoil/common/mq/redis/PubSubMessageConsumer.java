package com.plantssoil.common.mq.redis;

import com.plantssoil.common.mq.AbstractMessageConsumer;
import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessageListener;

import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;

/**
 * The IMessageConsumer implementation base on Redis PubSub
 * 
 * @author danialdy
 * @Date 6 Nov 2024 10:04:25 pm
 */
class PubSubMessageConsumer extends AbstractMessageConsumer {
    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";
    private RedisPubSubAsyncCommands<String, String> command;
    private PubSubMessageDispatcher dispatcher;

    protected PubSubMessageConsumer(RedisPubSubAsyncCommands<String, String> command, PubSubMessageDispatcher dispatcher) {
        this.command = command;
        this.dispatcher = dispatcher;
    }

    @Override
    public IMessageConsumer addMessageListener(IMessageListener listener) {
        super.addMessageListener(listener);
        this.dispatcher.addListener(this.getPublisherId(), this.getVersion(), this.getDataGroup(), this.getConsumerId(), listener);
        return this;
    }

    private String createRoutingKey() {
        return String.format("%s%s%s%s%s", this.getPublisherId(), ROUTING_KEY_SEPARATOR, this.getVersion(), ROUTING_KEY_SEPARATOR,
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
    }

    @Override
    public void consume() {
        this.command.subscribe(createRoutingKey());
    }

}
