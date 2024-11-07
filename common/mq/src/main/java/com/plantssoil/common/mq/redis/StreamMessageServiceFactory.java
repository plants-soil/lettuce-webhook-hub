package com.plantssoil.common.mq.redis;

import java.time.Duration;

import org.apache.commons.configuration.Configuration;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.common.mq.IMessageSubscriber;
import com.plantssoil.common.mq.exception.MessageQueueException;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

/**
 * The IMessageServiceFactory implementation base on Redis Stream.<br/>
 * Referenced Redis Lettuce documentation, redis-lettuce does't need connection
 * pool for non-blocking commands.<br/>
 * There are only 2 connections in this factory, one for publishing and another
 * for subscribing.<br/>
 * 
 * @author danialdy
 * @Date 6 Nov 2024 3:44:21 pm
 */
public class StreamMessageServiceFactory implements IMessageServiceFactory {
    private RedisClient client;
    private long connectionTimeout = 30 * 1000;
    private StatefulRedisConnection<String, String> publisherConnection;
    private StatefulRedisConnection<String, String> subscriberConnection;

    /**
     * Constructor<br/>
     * Initialize connection pools for publisher and subscriber<br/>
     */
    public StreamMessageServiceFactory() {
        Configuration configuration = ConfigFactory.getInstance().getConfiguration();

        // initiate the RedisClient
        if (configuration.containsKey(LettuceConfiguration.MESSAGE_SERVICE_URI)) {
            // initialize subscriber & publisher factory
            String uri = ConfigFactory.getInstance().getConfiguration().getString(LettuceConfiguration.MESSAGE_SERVICE_URI);
            this.client = RedisClient.create(uri);
        } else {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15004,
                    String.format("Don't find configuration '%s'!", LettuceConfiguration.MESSAGE_SERVICE_URI));
        }
        // Sets the connection timeout value for getting Connections and following
        // Commands in
        // Milliseconds, defaults to 30 seconds.
        if (configuration.containsKey(LettuceConfiguration.MESSAGE_SERVICE_CONNECTION_TIMEOUT)) {
            this.connectionTimeout = configuration.getLong(LettuceConfiguration.MESSAGE_SERVICE_CONNECTION_TIMEOUT);
        }

        // create publisher connection
        this.publisherConnection = this.client.connect();
        this.publisherConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));

        // create subscriber connection
        this.subscriberConnection = this.client.connect();
        this.subscriberConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));
    }

    @Override
    public void close() throws Exception {
        if (this.publisherConnection != null) {
            this.publisherConnection.close();
        }
        if (this.subscriberConnection != null) {
            this.subscriberConnection.close();
        }
    }

    @Override
    public IMessagePublisher createMessagePublisher() {
        return new StreamMessagePublisher(this.publisherConnection.async());
    }

    @Override
    public IMessageSubscriber createMessageSubscriber() {
        return new StreamMessageSubscriber(this.subscriberConnection.reactive());
    }

}
