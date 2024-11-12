package com.plantssoil.common.mq.redis;

import java.util.List;
import java.util.function.Consumer;

import com.plantssoil.common.mq.AbstractMessageSubscriber;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.SimpleMessage;

import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs.StreamOffset;
import io.lettuce.core.api.reactive.RedisReactiveCommands;

/**
 * The IMessageSubscriber implementation base on Redis Stream
 * 
 * @author danialdy
 * @Date 6 Nov 2024 10:04:25 pm
 */
class StreamMessageSubscriber extends AbstractMessageSubscriber {
    private final static String ID_READ_GROUP = "stream-subscriber-group";
    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";
    private final static String PUBLISHER_ID = "publisherId";
    private final static String VERSION = "version";
    private final static String DATA_GROUP = "dataGroup";
    private final static String PAYLOAD = "payload";
    private RedisReactiveCommands<String, String> command;

    protected StreamMessageSubscriber(RedisReactiveCommands<String, String> command) {
        this.command = command;
    }

    private String createRoutingKey() {
        return String.format("%s%s%s%s%s", this.getPublisherId(), ROUTING_KEY_SEPARATOR, this.getVersion(), ROUTING_KEY_SEPARATOR,
                this.getDataGroup() == null ? "NULL" : this.getDataGroup());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void subscribe() {
        String key = createRoutingKey();
//        commands.xgroupCreate(StreamOffset.latest(key), ID_READ_GROUP);
//        commands.xreadgroup(io.lettuce.core.Consumer.from(ID_READ_GROUP, this.getConsumerId()), XReadArgs.Builder.noack(false), StreamOffset.lastConsumed(key))
//                .subscribe(System.out::println, Throwable::printStackTrace);

        this.command.xreadgroup(io.lettuce.core.Consumer.from(ID_READ_GROUP, this.getConsumerId()), StreamOffset.lastConsumed(key))
                .subscribe(new Consumer<StreamMessage<String, String>>() {
                    @Override
                    public void accept(StreamMessage<String, String> t) {
                        List<IMessageListener> listeners = getListeners();
                        SimpleMessage msg = new SimpleMessage(t.getBody().get(PUBLISHER_ID), t.getBody().get(VERSION), t.getBody().get(DATA_GROUP),
                                getConsumerId(), t.getBody().get(PAYLOAD));
                        for (IMessageListener l : listeners) {
                            l.onMessage(msg);
                        }
                    }
                });

    }

}
