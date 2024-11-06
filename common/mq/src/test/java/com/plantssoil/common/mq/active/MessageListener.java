package com.plantssoil.common.mq.active;

import com.plantssoil.common.mq.IMessage;
import com.plantssoil.common.mq.IMessageListener;

public class MessageListener implements IMessageListener {
    private String consumerId;

    @Override
    public String getConsumerId() {
        return this.consumerId;
    }

    @Override
    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    @Override
    public void onMessage(IMessage message) {
        String info = String.format("%s || %s || %s || %s", consumerId, message.getPublisherId(), message.getVersion(), message.getMessage());
        System.out.println(info);
    }

}
