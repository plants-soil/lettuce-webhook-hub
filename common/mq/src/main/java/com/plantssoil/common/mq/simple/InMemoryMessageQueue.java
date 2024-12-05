package com.plantssoil.common.mq.simple;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.plantssoil.common.mq.AbstractMessageConsumer;

class InMemoryMessageQueue<T> {
    private volatile Map<String, LinkedBlockingQueue<T>> messageQueues;
    private volatile Map<String, MessageRunnable<T>> messageConsumers;

    InMemoryMessageQueue() {
        this.messageQueues = new ConcurrentHashMap<>();
        this.messageConsumers = new ConcurrentHashMap<>();
    }

    void close() {
        for (Entry<String, MessageRunnable<T>> consumer : messageConsumers.entrySet()) {
            consumer.getValue().stop();
        }
        this.messageQueues.clear();
        this.messageConsumers.clear();
    }

    void removeConsumer(String queueName, AbstractMessageConsumer<T> consumer) {
        MessageRunnable<T> mr = messageConsumers.get(queueName);
        if (mr != null) {
            mr.removeConsumer(consumer);
        }
    }

    private LinkedBlockingQueue<T> getMessageQueue(String queueName) {
        LinkedBlockingQueue<T> mq = this.messageQueues.get(queueName);
        if (mq == null) {
            synchronized ("getMessageQueue") {
                mq = this.messageQueues.get(queueName);
                if (mq == null) {
                    mq = new LinkedBlockingQueue<>();
                    this.messageQueues.put(queueName, mq);
                    MessageRunnable<T> messageConsumer = new MessageRunnable<>(mq);
                    this.messageConsumers.put(queueName, messageConsumer);
                    new Thread(messageConsumer).start();
                }
            }
        }
        return mq;
    }

    void publish(String queueName, T message) throws InterruptedException {
        // discard message if there is no consumer to avoid memory exhausted
        if (!messageConsumers.containsKey(queueName)) {
            return;
        }
        // put message into queue
        LinkedBlockingQueue<T> mq = getMessageQueue(queueName);
        mq.put(message);
    }

    void consume(String queueName, AbstractMessageConsumer<T> consumer) {
        getMessageQueue(queueName);
        MessageRunnable<T> messageConsumer = this.messageConsumers.get(queueName);
        messageConsumer.addConsumer(consumer);
    }
}
