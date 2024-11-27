package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.plantssoil.webhook.core.IWebhookSubscriber;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * The retry webhook queue which to hold retry webhook tasks
 * 
 * @author danialdy
 * @Date 27 Nov 2024 11:56:18 am
 */
class RetryWebhookQueue {
    private ConcurrentLinkedQueue<RetryWebhookTask> queue = new ConcurrentLinkedQueue<>();
    private int capacity;

    /**
     * The constructor of the retry queue
     * 
     * @param capacity The maximum retry webhook queue capacity, exception will
     *                 happen when add task instance into the queue and the capacity
     *                 exceeds
     */
    RetryWebhookQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Add message & subscriber & milliseconds as the RetryWebhookTask into the
     * retry queue
     * 
     * @param message             The webhook event message
     * @param subscriber          The subscriber to receive the message
     * @param executeMilliseconds The milliseconds at which the retry will execute
     */
    void add(DefaultWebhookMessage message, IWebhookSubscriber subscriber, long executeMilliseconds) {
        if (this.queue.size() >= this.capacity) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20005,
                    String.format("Two many tasks in retry queue (maximum: %d), can't re-try!", this.capacity));
        }
        queue.offer(new RetryWebhookTask(message, subscriber, executeMilliseconds));
    }

    /**
     * Get RetryWebhookTask list which need to execute
     * 
     * @return RetryWebhookTask list
     */
    List<RetryWebhookTask> webhookTasksTimeUp() {
        List<RetryWebhookTask> list = new ArrayList<>();
        long current = System.currentTimeMillis();
        int qty = 0;
        for (RetryWebhookTask task : this.queue) {
            if (task.getExecuteMilliseconds() <= current) {
                qty++;
            } else {
                break;
            }
        }
        for (int i = 0; i < qty; i++) {
            RetryWebhookTask task = this.queue.poll();
            list.add(task);
        }
        return list;
    }
}
