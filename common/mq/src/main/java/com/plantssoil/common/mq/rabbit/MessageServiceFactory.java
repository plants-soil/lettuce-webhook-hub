package com.plantssoil.common.mq.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.rabbitmq.client.Channel;

/**
 * The IMessageServiceFactory implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:45:20 am
 */
public class MessageServiceFactory<T> implements IMessageServiceFactory<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageServiceFactory.class.getName());
    private ConnectionPool publisherPool;
    private ConnectionPool consumerPool;
    private PooledConnection currentConsumerConnection;
    private PooledConnection currentPublisherConnection;

    /**
     * Constructor<br/>
     * Initialize connection pools for publisher and consumer<br/>
     */
    public MessageServiceFactory() {
        LOGGER.info("Loading RabbitMQ as the message service...");
        // create initial consumer connection
        this.consumerPool = new ConnectionPool();
        this.currentConsumerConnection = this.consumerPool.getConnection();
        // create initial publisher connection
        this.publisherPool = new ConnectionPool();
        this.currentPublisherConnection = this.publisherPool.getConnection();
        LOGGER.info("RabbitMQ connected and loaded.");
    }

    @Override
    public void close() throws Exception {
        if (this.publisherPool != null) {
            this.publisherPool.close();
        }
        if (this.consumerPool != null) {
            this.consumerPool.close();
        }
    }

    /**
     * Get MQ Channel.<br/>
     * Consumer Connections & Channels should not be closed, consumer will ALWAYS
     * listening for incoming message.<br/>
     * Consumer will not receive message if Connection/Channel closed.<br/>
     * <br/>
     * The new publisher/consumer will re-use current connection to create
     * channel.<br/>
     * Will create new connection from connection pool if active channels exceed
     * maximum number configured, and the previous connection will be returned back
     * into connection pool.<br/>
     * <br/>
     * The whole publisher/consumer capacity in one JVM should be: max connections *
     * max sessions per connection. e.g:<br/>
     * Default configuration: Max Connection: 18, Max Session / Connection: 500<br/>
     * Capacity = 18 * 500 = 9,000<br/>
     * 
     * @return Channel used to publish/consume messages from MQ
     */
    private Channel getChannel(ConnectionPool pool, PooledConnection currentConnection) {
        if (currentConnection.getActiveChannels() >= pool.getMaxSessionsPerConnection()) {
            synchronized (this) {
                if (currentConnection.getActiveChannels() >= pool.getMaxSessionsPerConnection()) {
                    LOGGER.info("Current Rabbit MQ connection fulled with channels(%d), creating new connection...", pool.getMaxSessionsPerConnection());
                    pool.returnConnection(currentConnection);
                    currentConnection = pool.getConnection();
                    Channel channel = currentConnection.createChannel();
                    LOGGER.info("Created session on the new connection.");
                    return channel;
                }
            }
        }

        return currentConnection.createChannel();
    }

    @Override
    public IMessageConsumer<T> createMessageConsumer() {
        // don't close connection and channel, in order to receive message from server
        // continuously
        return new MessageConsumer<T>(getChannel(this.consumerPool, this.currentConsumerConnection));
    }

    @Override
    public IMessagePublisher<T> createMessagePublisher() {
        return new MessagePublisher<T>(getChannel(this.publisherPool, this.currentPublisherConnection));
    }

}
