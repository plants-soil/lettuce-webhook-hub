package com.plantssoil.common.mq.simple;

import com.plantssoil.common.mq.AbstractMessagePublisher;
import com.plantssoil.common.mq.exception.MessageQueueException;

/**
 * Simple Message Publisher implementation base on in-memory message queue
 * 
 * @author danialdy
 * @Date 11 Nov 2024 10:37:19 pm
 */
class MessagePublisher extends AbstractMessagePublisher {
    private InMemoryMessageQueue messageQueue;

    MessagePublisher(InMemoryMessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void publish(String message) {
        String queueName = String.format("QUEUE-%s-%s-%s", this.getPublisherId(), this.getVersion(),
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
        try {
            this.messageQueue.publish(queueName, message);
        } catch (InterruptedException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15002, e);
        }
    }

}
