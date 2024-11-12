package com.plantssoil.common.mq.active;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.configuration.Configuration;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.common.mq.exception.MessageQueueException;

/**
 * The IMessageServiceFactory implementation base on Active MQ<br/>
 * This factory is singleton managed by
 * {@link ConfigurableLoader#createSingleton(String)}
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:45:20 am
 */
public class MessageServiceFactory implements IMessageServiceFactory {
    private ActiveMQConnectionFactory consumerFactory;
    private PooledConnectionFactory publisherFactory;
    private List<Connection> consumerConnectionPool;
    private AtomicInteger nextConsumerSessionIndex = new AtomicInteger(0);
    private int maxConnections = 18;
    private int maxSessionsPerConnection = 500;

    /**
     * Constructor<br/>
     * Need setup the configuration {@link LettuceConfiguration#MESSAGE_SERVICE_URI}
     * first<br/>
     * Could setup the MQ connection pool size via configuration
     * ({@link LettuceConfiguration#MESSAGE_SERVICE_POOL_MAXSIZE} [default 18],
     * {@link LettuceConfiguration#MESSAGE_SERVICE_POOL_MAXIDLE} [default 6],
     * {@link LettuceConfiguration#MESSAGE_SERVICE_POOL_MINIDLE} [default 2])
     */
    public MessageServiceFactory() {
        Configuration configuration = ConfigFactory.getInstance().getConfiguration();
        if (configuration.containsKey(LettuceConfiguration.MESSAGE_SERVICE_URI)) {
            // initialize consumer & publisher factory
            String uri = ConfigFactory.getInstance().getConfiguration().getString(LettuceConfiguration.MESSAGE_SERVICE_URI);
            this.consumerFactory = new ActiveMQConnectionFactory(uri);
            this.publisherFactory = new PooledConnectionFactory(new ActiveMQConnectionFactory(uri));

            // ActiveMQ need the package declaration for security reason
            this.consumerFactory.setTrustedPackages(new ArrayList<String>(Arrays.asList("com.plantssoil.common.mq.active".split(","))));
        } else {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15004,
                    String.format("Don't find configuration '%s'!", LettuceConfiguration.MESSAGE_SERVICE_URI));
        }

        // max pooled connections
        if (configuration.containsKey(LettuceConfiguration.MESSAGE_SERVICE_MAX_CONNECTIONS)) {
            this.maxConnections = configuration.getInt(LettuceConfiguration.MESSAGE_SERVICE_MAX_CONNECTIONS);
        }
        this.publisherFactory.setMaxConnections(this.maxConnections);

        // max sessions per connection
        if (configuration.containsKey(LettuceConfiguration.MESSAGE_SERVICE_MAX_SESSIONS_PER_CONNECTION)) {
            this.maxSessionsPerConnection = configuration.getInt(LettuceConfiguration.MESSAGE_SERVICE_MAX_SESSIONS_PER_CONNECTION);
        }
        this.publisherFactory.setMaximumActiveSessionPerConnection(this.maxSessionsPerConnection);

        // Sets the connection timeout value for getting Connections from this pool in
        // Milliseconds,defaults to 30 seconds.
        if (configuration.containsKey(LettuceConfiguration.MESSAGE_SERVICE_CONNECTION_TIMEOUT)) {
            this.publisherFactory.setConnectionTimeout(configuration.getInt(LettuceConfiguration.MESSAGE_SERVICE_CONNECTION_TIMEOUT));
        }

        // Sets the idle timeout value for Connection's that are created by this pool in
        // Milliseconds,defaults to 30 seconds.
        if (configuration.containsKey(LettuceConfiguration.MESSAGE_SERVICE_IDLE_TIMEOUT)) {
            this.publisherFactory.setIdleTimeout(configuration.getInt(LettuceConfiguration.MESSAGE_SERVICE_IDLE_TIMEOUT));
        }

        // allow connections to expire, irrespective of load or idle time. This is
        // useful with failover to force a reconnect from the pool, to reestablish load
        // balancing or use of the master post recovery
        if (configuration.containsKey(LettuceConfiguration.MESSAGE_SERVICE_EXPIRY_TIMEOUT)) {
            this.publisherFactory.setExpiryTimeout(configuration.getLong(LettuceConfiguration.MESSAGE_SERVICE_EXPIRY_TIMEOUT));
        }

        // initialize consumer pool and add 1 connection into it
        this.consumerConnectionPool = new ArrayList<>();
        addConsumerConnection();
    }

    @Override
    public void close() throws Exception {
        if (this.publisherFactory != null) {
            this.publisherFactory.stop();
        }
        for (Connection connection : this.consumerConnectionPool) {
            connection.stop();
            connection.close();
        }
    }

    private void addConsumerConnection() {
        try {
            // create connection
            Connection connection = this.consumerFactory.createConnection();
            // Starts (or restarts) a connection's delivery of incoming messages. A call to
            // start on a connection that has already been started is ignored.
            connection.start();
            this.consumerConnectionPool.add(connection);
        } catch (JMSException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15005, e);
        }
    }

    private Session getConsumerSession() {
        // next consumer session index ++
        // put this on the first line is essential
        // it's atomic to avoid multiple threads mis-determine
        this.nextConsumerSessionIndex.incrementAndGet();

        if (this.nextConsumerSessionIndex.get() > this.maxSessionsPerConnection) {
            synchronized (this) {
                if (this.nextConsumerSessionIndex.get() > this.maxSessionsPerConnection) {
                    if (this.consumerConnectionPool.size() >= this.maxConnections) {
                        throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15006,
                                String.format("Subscriber connections exceed maximium pool size (%d)!", this.maxConnections));
                    }
                    addConsumerConnection();

                    // need acquire the first session within the synchronized block
                    Connection connection = this.consumerConnectionPool.get(this.consumerConnectionPool.size() - 1);
                    try {
                        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        this.nextConsumerSessionIndex.getAndSet(1);
                        return session;
                    } catch (JMSException e) {
                        throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15007, e);
                    }
                } else {
                    this.nextConsumerSessionIndex.incrementAndGet();
                }
            }
        }

        Connection connection = this.consumerConnectionPool.get(this.consumerConnectionPool.size() - 1);
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            return session;
        } catch (JMSException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15007, e);
        }
    }

    @Override
    public IMessagePublisher createMessagePublisher() {
        try {
            return new MessagePublisher(this.publisherFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE));
        } catch (JMSException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15008, e);
        }
    }

    @Override
    public IMessageConsumer createMessageConsumer() {
        return new MessageConsumer(getConsumerSession());
    }

}
