package com.plantssoil.common.mq.simple;

import com.plantssoil.common.mq.AbstractMessageConsumer;

/**
 * Simple Message Consumer implementation base on in-memory message queue
 * 
 * @author danialdy
 * @Date 11 Nov 2024 10:36:21 pm
 */
class MessageConsumer extends AbstractMessageConsumer {
    private InMemoryMessageQueue messageQueue;

    MessageConsumer(InMemoryMessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void consume() {
        String queueName = String.format("QUEUE-%s-%s-%s", this.getPublisherId(), this.getVersion(),
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());

        messageQueue.consume(queueName, this);
    }

}
