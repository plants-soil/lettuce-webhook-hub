package com.plantssoil.common.mq.redis;

import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.exception.MessageQueueException;

import io.lettuce.core.api.async.RedisAsyncCommands;

/**
 * The IMessagePublisher implementation base on Redis List
 * 
 * @author danialdy
 * @Date 6 Nov 2024 4:34:30 pm
 */
class ListMessagePublisher extends AbstractMessagePublisher {
    private RedisAsyncCommands<String, String> command;
    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";

    /**
     * Constructor mandatory
     * 
     * @param command Redis command - to execute publish command
     */
    protected ListMessagePublisher(RedisAsyncCommands<String, String> command) {
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
        this.command.lpush(channel, message);
    }
}
