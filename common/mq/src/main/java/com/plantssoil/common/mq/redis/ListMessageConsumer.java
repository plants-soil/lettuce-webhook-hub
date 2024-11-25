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
//    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";
    private RedisAsyncCommands<String, String> command;
    private boolean running = true;

    ListMessageConsumer(RedisAsyncCommands<String, String> command) {
        this.command = command;
    }

//    private String createRoutingKey() {
//        return String.format("%s%s%s%s%s", this.getPublisherId(), ROUTING_KEY_SEPARATOR, this.getVersion(), ROUTING_KEY_SEPARATOR,
//                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
//    }

    void stop() {
        this.running = false;
    }

    @Override
    public void consume(Class<T> clazz) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    command.rpop(getQueueName()).thenAccept(t -> {
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

}
