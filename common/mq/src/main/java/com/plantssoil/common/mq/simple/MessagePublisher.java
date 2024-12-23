package com.plantssoil.common.mq.simple;

import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.exception.MessageQueueException;

/**
 * Simple Message Publisher implementation base on in-memory message queue
 * 
 * @author danialdy
 * @Date 11 Nov 2024 10:37:19 pm
 */
class MessagePublisher<T> extends AbstractMessagePublisher<T> {
    private InMemoryMessageQueue<T> messageQueue;

    MessagePublisher(InMemoryMessageQueue<T> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void publish(T message) {
        String queueName = getQueueName();
        try {
            this.messageQueue.publish(queueName, message);
        } catch (InterruptedException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15002, e);
        }
    }

    @Override
    public void close() throws Exception {
        // nothing need to be clean for InMemoryMessageQueue
    }

}
