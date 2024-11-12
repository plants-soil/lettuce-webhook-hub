package com.plantssoil.common.mq.simple;

import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.common.mq.IMessageConsumer;

/**
 * This is the default Message Service Factory implementation (for Single JVM),
 * can't used in distributed system.<br/>
 * It uses in-memory collection and message queue to publish & subscribe
 * message, so all data will be cleared after the JVM stop<br/>
 * 
 * @author danialdy
 * @Date 11 Nov 2024 8:12:20 pm
 */
public class MessageServiceFactory implements IMessageServiceFactory {
    private InMemoryMessageQueue messageQueue;

    public MessageServiceFactory() {
        this.messageQueue = new InMemoryMessageQueue();
    }

    @Override
    public void close() throws Exception {
        this.messageQueue.close();
    }

    @Override
    public IMessagePublisher createMessagePublisher() {
        return new MessagePublisher(messageQueue);
    }

    @Override
    public IMessageConsumer createMessageConsumer() {
        return new MessageConsumer(messageQueue);
    }

}
