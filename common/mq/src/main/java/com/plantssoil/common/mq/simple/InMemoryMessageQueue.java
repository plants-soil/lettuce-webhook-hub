package com.plantssoil.common.mq.simple;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.plantssoil.common.mq.AbstractMessageSubscriber;

class InMemoryMessageQueue {
    private volatile Map<String, LinkedBlockingQueue<String>> messageQueues;
    private volatile Map<String, MessageConsumer> messageConsumers;

    InMemoryMessageQueue() {
        this.messageQueues = new ConcurrentHashMap<>();
        this.messageConsumers = new ConcurrentHashMap<>();
    }

    void close() {
        for (Entry<String, MessageConsumer> consumer : messageConsumers.entrySet()) {
            consumer.getValue().stop();
        }
        this.messageQueues.clear();
        this.messageConsumers.clear();
    }

    private LinkedBlockingQueue<String> getMessageQueue(String queueName) {
        LinkedBlockingQueue<String> mq = this.messageQueues.get(queueName);
        if (mq == null) {
            synchronized ("getMessageQueue") {
                mq = this.messageQueues.get(queueName);
                if (mq == null) {
                    mq = new LinkedBlockingQueue<>();
                    this.messageQueues.put(queueName, mq);
                    MessageConsumer messageConsumer = new MessageConsumer(mq);
                    this.messageConsumers.put(queueName, messageConsumer);
                    new Thread(messageConsumer).start();
                }
            }
        }
        return mq;
    }

    void publish(String queueName, String message) throws InterruptedException {
        LinkedBlockingQueue<String> mq = getMessageQueue(queueName);
        mq.put(message);
    }

    void subscribe(String queueName, AbstractMessageSubscriber subscriber) {
        getMessageQueue(queueName);
        MessageConsumer messageConsumer = this.messageConsumers.get(queueName);
        messageConsumer.addSubscriber(subscriber);
    }
}
