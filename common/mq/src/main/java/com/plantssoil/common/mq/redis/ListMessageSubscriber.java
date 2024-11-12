package com.plantssoil.common.mq.redis;

import java.util.function.Consumer;

import com.plantssoil.common.mq.AbstractMessageSubscriber;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.SimpleMessage;

import io.lettuce.core.api.async.RedisAsyncCommands;

/**
 * The IMessageSubscriber implementation base on Redis Stream
 * 
 * @author danialdy
 * @Date 6 Nov 2024 10:04:25 pm
 */
class ListMessageSubscriber extends AbstractMessageSubscriber {
    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";
    private RedisAsyncCommands<String, String> command;
    private boolean running = true;

    ListMessageSubscriber(RedisAsyncCommands<String, String> command) {
        this.command = command;
    }

    private String createRoutingKey() {
        return String.format("%s%s%s%s%s", this.getPublisherId(), ROUTING_KEY_SEPARATOR, this.getVersion(), ROUTING_KEY_SEPARATOR,
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
    }

    void stop() {
        this.running = false;
    }

    @Override
    public void subscribe() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    command.rpop(createRoutingKey()).thenAccept(new Consumer<String>() {
                        @Override
                        public void accept(String t) {
                            if (t == null) {
                                return;
                            }
                            SimpleMessage message = new SimpleMessage(getPublisherId(), getVersion(), getDataGroup(), getConsumerId(), t);
                            for (IMessageListener l : getListeners()) {
                                l.onMessage(message);
                            }
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
