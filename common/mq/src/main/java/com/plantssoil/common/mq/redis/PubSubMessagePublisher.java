package com.plantssoil.common.mq.redis;

import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.exception.MessageQueueException;

import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;

/**
 * The IMessagePublisher implementation base on Redis PubSub
 * 
 * @author danialdy
 * @Date 6 Nov 2024 4:34:30 pm
 */
public class PubSubMessagePublisher extends AbstractMessagePublisher {
    private RedisPubSubAsyncCommands<String, String> command;
    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";

    /**
     * Constructor mandatory
     * 
     * @param command Redis command - to execute publish command
     */
    protected PubSubMessagePublisher(RedisPubSubAsyncCommands<String, String> command) {
        this.command = command;
    }

    private String createRoutingKey() {
        return String.format("%s%s%s%s%s", this.getPublisherId(), ROUTING_KEY_SEPARATOR, this.getVersion(), ROUTING_KEY_SEPARATOR,
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
    }

    @Override
    public void publish(String message) {
        if (this.getPublisherId() == null || this.getVersion() == null) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15008, "The [publisherId] or [version] should not be null!");
        }
        String channel = createRoutingKey();
        this.command.publish(channel, message);
    }
}
