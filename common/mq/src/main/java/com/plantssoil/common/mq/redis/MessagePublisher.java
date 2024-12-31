package com.plantssoil.common.mq.redis;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.ChannelType;
import com.plantssoil.common.mq.exception.MessageQueueException;

import io.lettuce.core.api.async.RedisAsyncCommands;

/**
 * The IMessagePublisher implementation base on Redis List
 * 
 * @author danialdy
 * @Date 6 Nov 2024 4:34:30 pm
 */
class MessagePublisher<T> extends AbstractMessagePublisher<T> {
    private final static String LETTUCE_QUEUE_NOTIFICATION_CHANNEL = "com.plantssoil.mq.notification.channel";
    private RedisAsyncCommands<String, String> command;
//    private RedisPubSubReactiveCommands<String, String> pubsubCommand;

    /**
     * Constructor mandatory
     * 
     * @param command Redis command - to execute publish command
     */
    protected MessagePublisher(RedisAsyncCommands<String, String> command/* , RedisPubSubReactiveCommands<String, String> pubsubCommand */) {
        this.command = command;
//        this.pubsubCommand = pubsubCommand;
    }

    @Override
    public void publish(T message) {
        if (this.getChannelName() == null) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15008, "The [queueName] should not be null!");
        }
        String channel = getChannelName();
        if (ChannelType.TOPIC == getChannelType()) {
            this.command.publish(channel, ObjectJsonSerializer.getInstance().serialize(message));
//            this.pubsubCommand.publish(channel, ObjectJsonSerializer.getInstance().serialize(message)).doOnNext((result) -> {
//            }).doOnError((ex) -> {
//            }).subscribe();
        } else {
            // push message to the channel left end
            this.command.lpush(channel, ObjectJsonSerializer.getInstance().serialize(message));
            this.command.publish(LETTUCE_QUEUE_NOTIFICATION_CHANNEL, channel);
//            // notification
//            this.pubsubCommand.publish(LETTUCE_QUEUE_NOTIFICATION_CHANNEL, channel).doOnNext((result) -> {
//            }).doOnError((ex) -> {
//            }).subscribe();
        }
    }

    @Override
    public void close() throws Exception {
        // nothing need to be clean for redis
    }
}
