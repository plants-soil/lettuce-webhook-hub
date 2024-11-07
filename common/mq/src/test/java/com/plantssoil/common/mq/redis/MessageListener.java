package com.plantssoil.common.mq.redis;

import com.plantssoil.common.mq.IMessage;
import com.plantssoil.common.mq.IMessageListener;

public class MessageListener implements IMessageListener {
    @Override
    public void onMessage(IMessage message) {
        String info = String.format("%s || %s || %s || %s", message.getConsumerId(), message.getPublisherId(), message.getVersion(), message.getMessage());
        System.out.println(info);
    }
}
