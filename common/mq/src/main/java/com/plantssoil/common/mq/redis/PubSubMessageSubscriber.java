package com.plantssoil.common.mq.redis;

import com.plantssoil.common.mq.AbstractMessageSubscriber;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.IMessageSubscriber;

import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;

/**
 * The IMessageSubscriber implementation base on Redis PubSub
 * 
 * @author danialdy
 * @Date 6 Nov 2024 10:04:25 pm
 */
class PubSubMessageSubscriber extends AbstractMessageSubscriber {
    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";
    private RedisPubSubAsyncCommands<String, String> command;
    private PubSubMessageDispatcher dispatcher;

    protected PubSubMessageSubscriber(RedisPubSubAsyncCommands<String, String> command, PubSubMessageDispatcher dispatcher) {
        this.command = command;
        this.dispatcher = dispatcher;
    }

    @Override
    public IMessageSubscriber addMessageListener(IMessageListener listener) {
        super.addMessageListener(listener);
        this.dispatcher.addListener(this.getPublisherId(), this.getVersion(), this.getDataGroup(), this.getConsumerId(), listener);
        return this;
    }

    private String createRoutingKey() {
        return String.format("%s%s%s%s%s", this.getPublisherId(), ROUTING_KEY_SEPARATOR, this.getVersion(), ROUTING_KEY_SEPARATOR,
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
    }

    @Override
    public void subscribe() {
        this.command.subscribe(createRoutingKey());
    }

}
