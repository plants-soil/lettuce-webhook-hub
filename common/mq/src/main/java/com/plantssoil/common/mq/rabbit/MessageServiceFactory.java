package com.plantssoil.common.mq.rabbit;

import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.common.mq.IMessageSubscriber;
import com.rabbitmq.client.Channel;

/**
 * The IMessageServiceFactory implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:45:20 am
 */
public class MessageServiceFactory implements IMessageServiceFactory {
    private ConnectionPool publisherPool;
    private ConnectionPool subscriberPool;
    private PooledConnection subscriberConnection;

    /**
     * Constructor<br/>
     * Initialize connection pools for publisher and subscriber<br/>
     */
    public MessageServiceFactory() {
        this.publisherPool = new ConnectionPool();
        this.subscriberPool = new ConnectionPool();
        // create initial subscriber connection
        this.subscriberConnection = this.subscriberPool.getConnection();
    }

    @Override
    public void close() throws Exception {
        if (publisherPool != null) {
            publisherPool.close();
        }
        if (subscriberPool != null) {
            subscriberPool.close();
        }
    }

    @Override
    public IMessagePublisher createMessagePublisher() {
        return new MessagePublisher(publisherPool);
    }

    /**
     * Get MQ Channel.<br/>
     * Subscription Connections & Channels should not be closed, consumer will
     * ALWAYS listening for incoming message.<br/>
     * Consumer will not receive message if Connection/Channel closed.<br/>
     * <br/>
     * The new subscriber will re-use current connection to create channel.<br/>
     * Will retrieve new connection from subscriber connection pool if active
     * channels exceed maximum number configured, and the previous connection will
     * be returned back into subscriber connection pool.<br/>
     * <br/>
     * The whole subscriber capacity in one JVM should be: max connections * max
     * sessions per connection. e.g:<br/>
     * Default configuration: Max Connection: 18, Max Session / Connection: 500<br/>
     * Capacity = 18 * 500 = 9,000<br/>
     * 
     * @return Channel used to consume messages from MQ
     */
    private Channel getSubscriberChannel() {
        if (this.subscriberConnection.getActiveChannels() >= this.subscriberPool.getMaxSessionsPerConnection()) {
            synchronized (this) {
                if (this.subscriberConnection.getActiveChannels() >= this.subscriberPool.getMaxSessionsPerConnection()) {
                    this.subscriberPool.returnConnection(this.subscriberConnection);
                    this.subscriberConnection = this.subscriberPool.getConnection();
                    return this.subscriberConnection.createChannel();
                }
            }
        }

        return this.subscriberConnection.createChannel();
    }

    @Override
    public IMessageSubscriber createMessageSubscriber() {
        // don't close connection and channel, in order to receive message from server
        // continuously
        return new MessageSubscriber(getSubscriberChannel());
    }

}
