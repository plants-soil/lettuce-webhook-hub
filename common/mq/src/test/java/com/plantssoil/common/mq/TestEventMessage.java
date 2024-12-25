package com.plantssoil.common.mq;

public class TestEventMessage {
    private String eventType;
    private String requestId;
    private String payload;

    public TestEventMessage() {
    }

    public TestEventMessage(String eventType, String requestId, String payload) {
        super();
        this.eventType = eventType;
        this.requestId = requestId;
        this.payload = payload;
    }

    public String getEventType() {
        return eventType;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getPayload() {
        return payload;
    }

}
