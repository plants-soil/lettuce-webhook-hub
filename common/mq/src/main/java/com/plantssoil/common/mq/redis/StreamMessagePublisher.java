package com.plantssoil.common.mq.redis;

import java.util.LinkedHashMap;
import java.util.Map;

import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.exception.MessageQueueException;

import io.lettuce.core.api.async.RedisAsyncCommands;

/**
 * The IMessagePublisher implementation base on Redis Stream
 * 
 * @author danialdy
 * @Date 6 Nov 2024 4:34:30 pm
 */
public class StreamMessagePublisher extends AbstractMessagePublisher {
    private RedisAsyncCommands<String, String> command;
    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";
    private final static String PUBLISHER_ID = "publisherId";
    private final static String VERSION = "version";
    private final static String DATA_GROUP = "dataGroup";
    private final static String PAYLOAD = "payload";

    /**
     * Constructor mandatory
     * 
     * @param command Redis command - to execute publish command
     */
    protected StreamMessagePublisher(RedisAsyncCommands<String, String> command) {
        this.command = command;
    }

    private String createRoutingKey() {
        return String.format("%s%s%s%s%s", this.getPublisherId(), ROUTING_KEY_SEPARATOR, this.getVersion(), ROUTING_KEY_SEPARATOR,
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
    }

    private Map<String, String> createMessageBody(String message) {
        Map<String, String> messageBody = new LinkedHashMap<>();
        messageBody.put(PUBLISHER_ID, this.getPublisherId());
        messageBody.put(VERSION, this.getVersion());
        messageBody.put(DATA_GROUP, this.getDataGroup());
        messageBody.put(PAYLOAD, message);
        return messageBody;
    }

    @Override
    public void publish(String message) {
        if (this.getPublisherId() == null || this.getVersion() == null) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15008, "The [publisherId] or [version] should not be null!");
        }
        String channel = createRoutingKey();
        this.command.xadd(channel, createMessageBody(message));
    }
}
