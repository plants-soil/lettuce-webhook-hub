package com.plantssoil.common.mq.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.common.mq.exception.MessageQueueException;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

/**
 * The IMessageServiceFactory implementation base on Redis List.<br/>
 * Referenced Redis Lettuce documentation, redis-lettuce does't need connection
 * pool for non-blocking commands.<br/>
 * There are only 2 connections in this factory, one for publishing and another
 * for subscribing.<br/>
 * 
 * @author danialdy
 * @Date 6 Nov 2024 3:44:21 pm
 */
public class ListMessageServiceFactory implements IMessageServiceFactory {
    private RedisClient client;
    private long connectionTimeout;
    private StatefulRedisConnection<String, String> publisherConnection;
    private StatefulRedisConnection<String, String> consumerConnection;
    private List<ListMessageConsumer> consumers;

    /**
     * Constructor<br/>
     * Initialize connection pools for publisher and consumer<br/>
     */
    public ListMessageServiceFactory() {
        IConfiguration configuration = ConfigFactory.getInstance().getConfiguration();

        // initiate the RedisClient
        if (configuration.containsKey(LettuceConfiguration.MESSAGE_SERVICE_URI)) {
            // initialize consumer & publisher factory
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
        this.publisherConnection = this.client.connect();
        this.publisherConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));

        // create consumer connection
        this.consumerConnection = this.client.connect();
        this.consumerConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));

        // create consumers collection
        this.consumers = new ArrayList<>();
    }

    @Override
    public void close() throws Exception {
        if (this.consumers != null) {
            for (ListMessageConsumer consumer : this.consumers) {
                consumer.stop();
            }
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
    public IMessagePublisher createMessagePublisher() {
        return new ListMessagePublisher(this.publisherConnection.async());
    }

    @Override
    public IMessageConsumer createMessageConsumer() {
        ListMessageConsumer consumer = new ListMessageConsumer(this.consumerConnection.async());
        this.consumers.add(consumer);
        return consumer;
    }

}
