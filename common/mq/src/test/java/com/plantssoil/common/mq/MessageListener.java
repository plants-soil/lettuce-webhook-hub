package com.plantssoil.common.mq;

public class MessageListener implements IMessageListener<TestEventMessage> {
    @Override
    public void onMessage(TestEventMessage message, String consumerId) {
        String info = String.format("%s || %s || %s || %s", consumerId, message.getRequestId(), message.getEventType(), message.getPayload());
        System.out.println(info);
    }
}
