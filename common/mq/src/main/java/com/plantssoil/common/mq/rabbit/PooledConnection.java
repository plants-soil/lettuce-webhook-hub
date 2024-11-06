package com.plantssoil.common.mq.rabbit;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.plantssoil.common.mq.exception.MessageQueueException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Proxy of the message queue connection and implements {@link Closeable}, means
 * need close the instance or just put it into the try clause<br/>
 * 
 * <pre>
 * <code>
 * try (PooledConnection connection = pool.getConnection()) {
 *   Channel channel = connection.createChannel;
 *   ......
 * }
 * </code>
 * </pre>
 * 
 * @author danialdy
 * @Date 5 Nov 2024 7:05:11 pm
 */
public class PooledConnection implements Closeable {
    private Connection connection;
    private ConnectionPool pool;
    private AtomicInteger channels = new AtomicInteger(0);
    private ChannelShutdownListener channelShutdownListener;

    protected PooledConnection(ConnectionPool pool) {
        this.pool = pool;
    }

    private Connection getConnection() {
        if (!isConnected()) {
            synchronized (this) {
                if (!isConnected()) {
                    try {
                        this.connection = this.pool.getConnectionFactory().newConnection();
                        channelShutdownListener = new ChannelShutdownListener();
                    } catch (IOException e) {
                        throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15012, e);
                    } catch (TimeoutException e) {
                        throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15013, e);
                    }
                }
            }
        }
        return this.connection;
    }

    /**
     * The connection is connected or not
     * 
     * @return true - Connected, false - Don't connected yet
     */
    public boolean isConnected() {
        if (this.connection != null) {
            return true;
        }
        return false;
    }

    private void checkChannelCount() {
        if (this.channels.get() > this.pool.getMaxSessionsPerConnection() + 1) {
            String err = String.format("Channels created on current connection exceed (%d)!", this.pool.getMaxSessionsPerConnection());
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15014, err);
        }
    }

    private class ChannelShutdownListener implements com.rabbitmq.client.ShutdownListener {
        @Override
        public void shutdownCompleted(ShutdownSignalException cause) {
            channels.decrementAndGet();
        }
    }

    /**
     * Create a new channel, using an internally allocated channel number. If
     * <a href="https://www.rabbitmq.com/api-guide.html#recovery">automatic
     * connection recovery</a> is enabled, the channel returned by this method will
     * be {@link Recoverable}.
     * <p>
     * Use {@link #openChannel()} if you want to use an {@link Optional} to deal
     * with a {@null} value.
     *
     * @return a new channel descriptor, or null if none is available
     */
    public Channel createChannel() {
        this.channels.incrementAndGet();
        checkChannelCount();
        try {
            Channel channel = getConnection().createChannel();
            channel.addShutdownListener(channelShutdownListener);
            return channel;
        } catch (IOException e) {
            this.channels.decrementAndGet();
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15004, e);
        }
    }

    /**
     * Create a new channel, using the specified channel number if possible.
     * <p>
     * Use {@link #openChannel(int)} if you want to use an {@link Optional} to deal
     * with a {@null} value.
     *
     * @param channelNumber the channel number to allocate
     * @return a new channel descriptor, or null if this channel number is already
     *         in use
     */
    public Channel createChannel(int channelNumber) {
        this.channels.incrementAndGet();
        checkChannelCount();
        try {
            Channel channel = getConnection().createChannel(channelNumber);
            channel.addShutdownListener(channelShutdownListener);
            return channel;
        } catch (IOException e) {
            this.channels.decrementAndGet();
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15004, e);
        }
    }

    /**
     * Create a new channel wrapped in an {@link Optional}. The channel number is
     * allocated internally.
     * <p>
     * If <a href="https://www.rabbitmq.com/api-guide.html#recovery">automatic
     * connection recovery</a> is enabled, the channel returned by this method will
     * be {@link Recoverable}.
     * <p>
     * Use {@link #createChannel()} to return directly a {@link Channel} or
     * {@code null}.
     *
     * @return an {@link Optional} containing the channel; never {@code null} but
     *         potentially empty if no channel is available
     * @see #createChannel()
     */
    public Optional<Channel> openChannel() {
        this.channels.incrementAndGet();
        checkChannelCount();
        try {
            Optional<Channel> channel = getConnection().openChannel();
            if (channel.isPresent()) {
                channel.get().addShutdownListener(channelShutdownListener);
            }
            return channel;
        } catch (IOException e) {
            this.channels.decrementAndGet();
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15004, e);
        }
    }

    /**
     * Create a new channel, using the specified channel number if possible.
     * <p>
     * Use {@link #createChannel(int)} to return directly a {@link Channel} or
     * {@code null}.
     *
     * @param channelNumber the channel number to allocate
     * @return an {@link Optional} containing the channel, never {@code null} but
     *         potentially empty if this channel number is already in use
     * @see #createChannel(int)
     */
    public Optional<Channel> openChannel(int channelNumber) {
        this.channels.incrementAndGet();
        checkChannelCount();
        try {
            Optional<Channel> channel = getConnection().openChannel(channelNumber);
            if (channel.isPresent()) {
                channel.get().addShutdownListener(channelShutdownListener);
            }
            return channel;
        } catch (IOException e) {
            this.channels.decrementAndGet();
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15004, e);
        }
    }

    /**
     * Get the active channel count in current connection
     * 
     * @return active channel count
     */
    public int getActiveChannels() {
        return this.channels.get();
    }

    protected void closeConnection() {
        if (this.isConnected()) {
            try {
                this.connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        if (this.connection != null) {
            this.pool.returnConnection(this);
        }
    }
}