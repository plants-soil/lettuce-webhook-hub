package com.plantssoil.common.mq.simple;

import com.plantssoil.common.mq.AbstractMessageSubscriber;

/**
 * Simple Message Subscriber implementation base on in-memory message queue
 * 
 * @author danialdy
 * @Date 11 Nov 2024 10:36:21 pm
 */
class MessageSubscriber extends AbstractMessageSubscriber {
    private InMemoryMessageQueue messageQueue;

    MessageSubscriber(InMemoryMessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void subscribe() {
        String queueName = String.format("QUEUE-%s-%s-%s", this.getPublisherId(), this.getVersion(),
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());

        messageQueue.subscribe(queueName, this);
    }

}
