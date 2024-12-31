package com.plantssoil.common.mq.simple;

import com.plantssoil.common.mq.AbstractMessageConsumer;

/**
 * Simple Message Consumer implementation base on in-memory message queue
 * 
 * @author danialdy
 * @Date 11 Nov 2024 10:36:21 pm
 */
class MessageConsumer<T> extends AbstractMessageConsumer<T> {
    private InMemoryMessageQueue<T> messageQueue;

    MessageConsumer(InMemoryMessageQueue<T> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void consume(Class<T> clazz) {
        messageQueue.consume(getChannelName(), this);
    }

    @Override
    public void close() {
        messageQueue.removeConsumer(getChannelName(), this);
    }

}
