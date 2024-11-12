package com.plantssoil.common.mq.rabbit;

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
public class MessageServiceFactory implements IMessageServiceFactory {
    private ConnectionPool publisherPool;
    private ConnectionPool consumerPool;
    private PooledConnection consumerConnection;

    /**
     * Constructor<br/>
     * Initialize connection pools for publisher and consumer<br/>
     */
    public MessageServiceFactory() {
        this.publisherPool = new ConnectionPool();
        this.consumerPool = new ConnectionPool();
        // create initial consumer connection
        this.consumerConnection = this.consumerPool.getConnection();
    }

    @Override
    public void close() throws Exception {
        if (publisherPool != null) {
            publisherPool.close();
        }
        if (consumerPool != null) {
            consumerPool.close();
        }
    }

    @Override
    public IMessagePublisher createMessagePublisher() {
        return new MessagePublisher(publisherPool);
    }

    /**
     * Get MQ Channel.<br/>
     * Consumer Connections & Channels should not be closed, consumer will ALWAYS
     * listening for incoming message.<br/>
     * Consumer will not receive message if Connection/Channel closed.<br/>
     * <br/>
     * The new consumer will re-use current connection to create channel.<br/>
     * Will create new connection from consumer connection pool if active channels
     * exceed maximum number configured, and the previous connection will be
     * returned back into consumer connection pool.<br/>
     * <br/>
     * The whole consumer capacity in one JVM should be: max connections * max
     * sessions per connection. e.g:<br/>
     * Default configuration: Max Connection: 18, Max Session / Connection: 500<br/>
     * Capacity = 18 * 500 = 9,000<br/>
     * 
     * @return Channel used to consume messages from MQ
     */
    private Channel getConsumerChannel() {
        if (this.consumerConnection.getActiveChannels() >= this.consumerPool.getMaxSessionsPerConnection()) {
            synchronized (this) {
                if (this.consumerConnection.getActiveChannels() >= this.consumerPool.getMaxSessionsPerConnection()) {
                    this.consumerPool.returnConnection(this.consumerConnection);
                    this.consumerConnection = this.consumerPool.getConnection();
                    return this.consumerConnection.createChannel();
                }
            }
        }

        return this.consumerConnection.createChannel();
    }

    @Override
    public IMessageConsumer createMessageConsumer() {
        // don't close connection and channel, in order to receive message from server
        // continuously
        return new MessageConsumer(getConsumerChannel());
    }

}
