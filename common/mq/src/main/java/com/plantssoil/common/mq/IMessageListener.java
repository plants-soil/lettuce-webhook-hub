package com.plantssoil.common.mq;

/**
 * Listen the message comes from subscribed Message Queue<br/>
 * Will process the messages<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 5:09:28 pm
 */
public interface IMessageListener {
    /**
     * MQ will call this method after message received<br/>
     * Could process the messages in this method<br/>
     * 
     * @param message message comes from publisher
     */
    public void onMessage(IMessage message);
}
