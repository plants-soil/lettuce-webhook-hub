package com.plantssoil.common.mq.redis;

import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.common.mq.AbstractMessageConsumer;
import com.plantssoil.common.mq.IMessageListener;

import io.lettuce.core.api.async.RedisAsyncCommands;

/**
 * The IMessageConsumer implementation base on Redis Stream
 * 
 * @author danialdy
 * @Date 6 Nov 2024 10:04:25 pm
 */
class ListMessageConsumer<T> extends AbstractMessageConsumer<T> {
    private RedisAsyncCommands<String, String> command;
    private boolean running = true;

    ListMessageConsumer(RedisAsyncCommands<String, String> command) {
        this.command = command;
    }

    @Override
    public void consume(Class<T> clazz) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    command.rpop(getChannelName()).thenAccept(t -> {
                        if (t == null) {
                            return;
                        }
                        T message = ObjectJsonSerializer.getInstance().unserialize(t, clazz);
                        for (IMessageListener<T> l : getListeners()) {
                            l.onMessage(message, getConsumerId());
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    @Override
    public void close() {
        this.running = false;
    }

}
