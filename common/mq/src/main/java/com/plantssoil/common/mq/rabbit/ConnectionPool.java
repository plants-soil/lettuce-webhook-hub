package com.plantssoil.common.mq.rabbit;

import java.io.Closeable;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.mq.exception.MessageQueueException;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Rabbit MQ connection pool<br/>
 * Maintain the connections from configuration
 * {@link LettuceConfiguration#MESSAGE_SERVICE_URI}<br/>
 * The max connection is from the configuration
 * {@link LettuceConfiguration#MESSAGE_SERVICE_MAX_CONNECTIONS}, default to
 * 18<br/>
 * The connection timeout is from the configuration
 * {@link LettuceConfiguration#MESSAGE_SERVICE_CONNECTION_TIMEOUT} with
 * milliseconds, default to 30 seconds<br/>
 * 
 * Can get message queue connection from this pool<br/>
 * 
 * <pre>
 * try (ConnectionPool pool = new ConnectionPool(); PooledConnection connection = pool.getConnection(); Channel channel = connection.createChannel()) {
 *     String message = "Message from connection pool.";
 *     channel.exchangeDeclare("ExchangeName", "direct");
 *     channel.basicPublish("ExchangeName", "", null, message.getBytes("UTF-8"));
 * }
 * </pre>
 * 
 * @author danialdy
 * @Date 5 Nov 2024 6:05:29 pm
 */
class ConnectionPool implements Closeable {
    private ConnectionFactory factory;
    private BlockingQueue<PooledConnection> connections;
    private int maxConnections;
    private int maxSessionsPerConnection;
    private int connectionTimeout;
    private boolean closed = false;

    /**
     * Constructor of the pool
     */
    public ConnectionPool() {
        IConfiguration configuration = ConfigFactory.getInstance().getConfiguration();

        // initiate the connection factory
        if (configuration.containsKey(LettuceConfiguration.MESSAGE_SERVICE_URI)) {
            // initialize subscriber & publisher factory
            String uri = ConfigFactory.getInstance().getConfiguration().getString(LettuceConfiguration.MESSAGE_SERVICE_URI);
            this.factory = new ConnectionFactory();
            try {
                this.factory.setUri(uri);
            } catch (KeyManagementException e) {
                throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15001, e);
            } catch (NoSuchAlgorithmException e) {
                throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15002, e);
            } catch (URISyntaxException e) {
                throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15003, e);
            }
        } else {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15004,
                    String.format("Don't find configuration '%s'!", LettuceConfiguration.MESSAGE_SERVICE_URI));
        }

        // max pooled connections, defaults to 18
        this.maxConnections = configuration.getInt(LettuceConfiguration.MESSAGE_SERVICE_MAX_CONNECTIONS, 18);

        // max sessions per connection, defaults to 500
        this.maxSessionsPerConnection = configuration.getInt(LettuceConfiguration.MESSAGE_SERVICE_MAX_SESSIONS_PER_CONNECTION, 500);

        // Sets the connection timeout value for getting Connections from this pool in
        // Milliseconds,defaults to 30 seconds.
        this.connectionTimeout = configuration.getInt(LettuceConfiguration.MESSAGE_SERVICE_CONNECTION_TIMEOUT, 30 * 1000);
        this.factory.setConnectionTimeout(this.connectionTimeout);

        this.connections = new LinkedBlockingQueue<>(this.maxConnections);
        for (int i = 0; i < this.maxConnections; i++) {
            this.connections.add(new PooledConnection(this));
        }
    }

    /**
     * Get Pooled Connection
     * 
     * @return Pooled Connection instance
     */
    public PooledConnection getConnection() {
        // can't get connection from a closed pool
        if (this.closed) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15005,
                    "Message Queue connection pool close, can't get connection anymore!");
        }
        // poll connection from pool
        try {
            return this.connections.poll(this.connectionTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15006, "Message Queue connection times out!", e);
        }
    }

    /**
     * The maximum connections in this pool
     * 
     * @return maximum connections
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * The maximum sessions (channels) in each connection
     * 
     * @return maximum sessions (channels)
     */
    public int getMaxSessionsPerConnection() {
        return maxSessionsPerConnection;
    }

    /**
     * The connection time out (in milliseconds)
     * 
     * @return connection time out
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Is this pool close or not
     * 
     * @return true - closed, false - not closed
     */
    public boolean isClosed() {
        return closed;
    }

    protected void returnConnection(PooledConnection connection) {
        // no need put back the connection if current pool is closed
        if (this.closed) {
            return;
        }
        // put back the connection
        try {
            this.connections.offer(connection, this.connectionTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15007, "Message Queue connection pool full!", e);
        }
    }

    /**
     * Get the connection factory this pool used
     * 
     * @return connection factory
     */
    protected ConnectionFactory getConnectionFactory() {
        return this.factory;
    }

    @Override
    public void close() {
        for (PooledConnection connection : this.connections) {
            connection.closeConnection();
        }
        this.closed = true;
    }
}