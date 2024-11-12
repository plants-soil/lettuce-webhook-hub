package com.plantssoil.common.mq.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import com.plantssoil.common.mq.AbstractMessageConsumer;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.SimpleMessage;

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
class MessageRunnable implements Runnable {
    private LinkedBlockingQueue<String> mq;
    private List<AbstractMessageConsumer> consumers = new ArrayList<>();
    private AtomicBoolean running = new AtomicBoolean(true);

    MessageRunnable(LinkedBlockingQueue<String> mq) {
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
                String message = mq.take();
                int i = ThreadLocalRandom.current().nextInt(consumers.size());
                AbstractMessageConsumer subscriber = consumers.get(i);
                SimpleMessage simpleMessage = new SimpleMessage(subscriber.getPublisherId(), subscriber.getVersion(), subscriber.getDataGroup(),
                        subscriber.getConsumerId(), message);
                for (IMessageListener l : subscriber.getListeners()) {
                    l.onMessage(simpleMessage);
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
    void addConsumer(AbstractMessageConsumer consumer) {
        consumers.add(consumer);
    }

}
