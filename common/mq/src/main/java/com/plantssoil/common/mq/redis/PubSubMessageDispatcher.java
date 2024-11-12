package com.plantssoil.common.mq.redis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.SimpleMessage;

import io.lettuce.core.pubsub.RedisPubSubListener;

/**
 * Redis Lettuce defines subscription listener on connection, means this
 * dispatcher receives all messages comes from all channels of the subscription
 * connection, it's base on Redis PubSub<br/>
 * Need dispatch messages to the corresponding listeners, base on publisherId +
 * version + dataGroup<br/>
 * 
 * @author danialdy
 * @Date 6 Nov 2024 9:04:36 pm
 */
class PubSubMessageDispatcher implements RedisPubSubListener<String, String> {
    private final static String ROUTING_KEY_SEPARATOR = "#R#K#";
    private Map<String, LinkedHashMap<String, List<IMessageListener>>> listeners = new ConcurrentHashMap<>();
    private Map<String, String[]> channelMap = new ConcurrentHashMap<>();

    private String createRoutingKey(String publisherId, String version, String dataGroup) {
        return String.format("%s%s%s%s%s", publisherId, ROUTING_KEY_SEPARATOR, version, ROUTING_KEY_SEPARATOR, dataGroup == null ? "NULL" : dataGroup);
    }

    /**
     * Add consumer (identified by consumerId) listeners to receive message from
     * publisherId + version + dataGroup
     * 
     * @param publisherId the publisher id where message comes from
     * @param version     the version where message comes from
     * @param dataGroup   the data group where message comes from
     * @param consumerId  the consumer where the message will go to
     * @param listener    consumer message processor (listener)
     */
    public synchronized void addListener(String publisherId, String version, String dataGroup, String consumerId, IMessageListener listener) {
        String channel = createRoutingKey(publisherId, version, dataGroup);
        LinkedHashMap<String, List<IMessageListener>> listenerMap = this.listeners.get(channel);
        if (listenerMap == null) {
            listenerMap = new LinkedHashMap<String, List<IMessageListener>>();
            this.listeners.put(channel, listenerMap);
            ArrayList<IMessageListener> list = new ArrayList<>();
            list.add(listener);
            listenerMap.put(consumerId, list);
        } else {
            List<IMessageListener> list = listenerMap.get(consumerId);
            if (list == null) {
                list = new ArrayList<>();
                list.add(listener);
                listenerMap.put(consumerId, list);
            } else {
                boolean found = false;
                for (IMessageListener l : list) {
                    if (l.getClass().getName().equals(listener.getClass().getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    list.add(listener);
                }
            }
        }
        if (!channelMap.containsKey(channel)) {
            channelMap.put(channel, new String[] { publisherId, version, dataGroup });
        }
    }

    @Override
    public void message(String channel, String message) {
        LinkedHashMap<String, List<IMessageListener>> listenerMap = listeners.get(channel);
        if (listenerMap == null) {
            return;
        }
        String[] publisherInfo = this.channelMap.get(channel);
        for (Entry<String, List<IMessageListener>> entry : listenerMap.entrySet()) {
            String consumerId = entry.getKey();
            SimpleMessage msg = new SimpleMessage(publisherInfo[0], publisherInfo[1], publisherInfo[2], consumerId, message);
            for (IMessageListener l : entry.getValue()) {
                l.onMessage(msg);
            }
        }
    }

    @Override
    public void message(String pattern, String channel, String message) {
        // nothing to do
    }

    @Override
    public void subscribed(String channel, long count) {
        // nothing to do
    }

    @Override
    public void psubscribed(String pattern, long count) {
        // nothing to do
    }

    @Override
    public void unsubscribed(String channel, long count) {
        // nothing to do
    }

    @Override
    public void punsubscribed(String pattern, long count) {
        // nothing to do
    }

}
