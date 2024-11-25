package com.plantssoil.common.mq.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.exception.MessageQueueException;
import com.rabbitmq.client.Channel;

/**
 * The IMessagePublisher implementation base on Rabbit MQ
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:51:37 am
 */
class MessagePublisher<T> extends AbstractMessagePublisher<T> {
    private final static String EXCHANGE_NAME = "com.plantssoil.message.exchange";
//    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";
    private ConnectionPool pool;
    private PooledConnection connection;

    /**
     * Constructor mandatory, used for factory to initiate
     * 
     * @param pool MQ Connection Pool
     */
    protected MessagePublisher(ConnectionPool pool) {
        this.pool = pool;
        this.connection = this.pool.getConnection();
        this.pool.returnConnection(this.connection);
    }

//    private String createRoutingKey() {
//        return String.format("%s%s%s%s%s", this.getPublisherId(), ROUTING_KEY_SEPARATOR, this.getVersion(), ROUTING_KEY_SEPARATOR,
//                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
//    }

    /**
     * Get MQ channel from connection<br/>
     * Will return current connection back into connection pool if active channels
     * exceed the maximum limitation<br/>
     * Otherwise will use current connection to create channel<br/>
     * 
     * @return Channel available
     */
    private Channel getChannel() {
        if (this.connection.getActiveChannels() >= this.pool.getMaxSessionsPerConnection()) {
            synchronized (this) {
                if (this.connection.getActiveChannels() >= this.pool.getMaxSessionsPerConnection()) {
                    this.pool.returnConnection(this.connection);
                    this.connection = this.pool.getConnection();
                    this.pool.returnConnection(this.connection);
                    return this.connection.createChannel();
                }
            }
        }

        return this.connection.createChannel();
    }

    @Override
    public void publish(T message) {
        if (getQueueName() == null) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15008, "The [queueName] should not be null!");
        }
        try (Channel channel = this.getChannel()) {
            // create exchange for every publiserId + version
            String routingKey = getQueueName();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, ObjectJsonSerializer.getInstance().serialize(message).getBytes("UTF-8"));
        } catch (IOException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15009, e);
        } catch (TimeoutException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15010, e);
        }
    }
}
