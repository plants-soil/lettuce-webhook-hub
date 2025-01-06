package com.plantssoil.common.mq.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.common.mq.exception.MessageQueueException;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * The IMessageServiceFactory implementation base on Redis.<br/>
 * Referenced Redis Lettuce documentation, redis-lettuce does't need connection
 * pool for non-blocking commands.<br/>
 * There are only 2 connections in this factory, one for publishing and another
 * for subscribing.<br/>
 * 
 * @author danialdy
 * @Date 6 Nov 2024 3:44:21 pm
 */
public class MessageServiceFactory<T extends java.io.Serializable> implements IMessageServiceFactory<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageServiceFactory.class.getName());
    private RedisClient client;
    private long connectionTimeout;
    private StatefulRedisPubSubConnection<String, String> pubsubPublisherConnection;
    private StatefulRedisPubSubConnection<String, String> pubsubConsumerConnection;
    private StatefulRedisConnection<String, String> publisherConnection;
    private StatefulRedisConnection<String, String> consumerConnection;
    private List<MessageConsumer<T>> consumers;

    /**
     * Constructor<br/>
     * Initialize Redis Client and connections for publisher and consumer<br/>
     */
    public MessageServiceFactory() {
        LOGGER.info("Loading Redis List as the message service...");
        IConfiguration configuration = ConfigFactory.getInstance().getConfiguration();

        // initiate the RedisClient
        if (configuration.containsKey(LettuceConfiguration.MESSAGE_SERVICE_URI)) {
            String uri = ConfigFactory.getInstance().getConfiguration().getString(LettuceConfiguration.MESSAGE_SERVICE_URI);
            this.client = RedisClient.create(uri);
        } else {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15004,
                    String.format("Don't find configuration '%s'!", LettuceConfiguration.MESSAGE_SERVICE_URI));
        }
        // Sets the connection timeout value for getting Connections and following
        // commands in milliseconds, defaults to 30 seconds.
        this.connectionTimeout = configuration.getLong(LettuceConfiguration.MESSAGE_SERVICE_CONNECTION_TIMEOUT, 30 * 1000);

        // create publisher connection
        this.pubsubPublisherConnection = this.client.connectPubSub();
        this.pubsubPublisherConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));

        // create consumer connection
        this.pubsubConsumerConnection = this.client.connectPubSub();
        this.pubsubConsumerConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));

        // create publisher connection
        this.publisherConnection = this.client.connect();
        this.publisherConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));

        // create consumer connection
        this.consumerConnection = this.client.connect();
        this.consumerConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));

        // create consumers collection
        this.consumers = new ArrayList<>();
        LOGGER.info("Redis Client connected and loaded.");
    }

    @Override
    public void close() throws Exception {
        if (this.consumers != null) {
            for (MessageConsumer<T> consumer : this.consumers) {
                consumer.close();
            }
        }
        if (this.pubsubConsumerConnection != null) {
            this.pubsubConsumerConnection.close();
        }
        if (this.publisherConnection != null) {
            this.publisherConnection.close();
        }
        if (this.consumerConnection != null) {
            this.consumerConnection.close();
        }
        this.client.shutdown();
    }

    @Override
    public IMessagePublisher<T> createMessagePublisher() {
        return new MessagePublisher<T>(this.publisherConnection.async(), this.pubsubPublisherConnection.async());
    }

    @Override
    public IMessageConsumer<T> createMessageConsumer() {
        MessageConsumer<T> consumer = new MessageConsumer<T>(this.pubsubConsumerConnection.reactive(), this.consumerConnection);
        this.consumers.add(consumer);
        return consumer;
    }

}
