package com.plantssoil.common.mq.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import com.plantssoil.common.mq.AbstractMessageConsumer;
import com.plantssoil.common.mq.IMessageListener;

/**
 * The message runnable implementation base on in-memory message queue<br/>
 * Subscribers could consume the new messages come up from in-memory message
 * queue<br/>
 * If there are multiple consumers on one queue, will randomly assign one of the
 * consumers to consume the message<br/>
 * 
 * @author danialdy
 * @Date 11 Nov 2024 10:38:01 pm
 */
class MessageRunnable<T> implements Runnable {
    private LinkedBlockingQueue<T> mq;
    private List<AbstractMessageConsumer<T>> consumers = new ArrayList<>();
    private AtomicBoolean running = new AtomicBoolean(true);

    MessageRunnable(LinkedBlockingQueue<T> mq) {
        this.mq = mq;
    }

    @Override
    public void run() {
        while (this.running.get()) {
            try {
                if (consumers.size() <= 0) {
                    Thread.sleep(100);
                    continue;
                }
                T message = mq.take();
                int i = ThreadLocalRandom.current().nextInt(consumers.size());
                AbstractMessageConsumer<T> subscriber = consumers.get(i);
                for (IMessageListener<T> l : subscriber.getListeners()) {
                    l.onMessage(message, subscriber.getConsumerId());
                }
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stop consuming message from message queue
     */
    void stop() {
        this.running = new AtomicBoolean(false);
    }

    /**
     * Add consumer to consume the message queue<br/>
     * If there are multiple consumers on one queue, will randomly assign one of the
     * consumers to consume the message<br/>
     * 
     * @param consumer consumer
     */
    void addConsumer(AbstractMessageConsumer<T> consumer) {
        consumers.add(consumer);
    }

}
