package com.plantssoil.common.mq.redis;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.exception.MessageQueueException;

import io.lettuce.core.api.async.RedisAsyncCommands;

/**
 * The IMessagePublisher implementation base on Redis List
 * 
 * @author danialdy
 * @Date 6 Nov 2024 4:34:30 pm
 */
class ListMessagePublisher<T> extends AbstractMessagePublisher<T> {
    private RedisAsyncCommands<String, String> command;

    /**
     * Constructor mandatory
     * 
     * @param command Redis command - to execute publish command
     */
    protected ListMessagePublisher(RedisAsyncCommands<String, String> command) {
        this.command = command;
    }

    @Override
    public void publish(T message) {
        if (this.getChannelName() == null) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15008, "The [queueName] should not be null!");
        }
        String channel = getChannelName();
        this.command.lpush(channel, ObjectJsonSerializer.getInstance().serialize(message));
    }

    @Override
    public void close() throws Exception {
        // nothing need to be clean for redis
    }
}
