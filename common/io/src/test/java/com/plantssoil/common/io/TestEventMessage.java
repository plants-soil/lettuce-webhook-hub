package com.plantssoil.common.io;

public class TestEventMessage implements java.io.Serializable {
    private static final long serialVersionUID = 6061756585306724607L;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TestEventMessage)) {
            return false;
        }
        TestEventMessage compare = (TestEventMessage) obj;
        if (compare.getRequestId() == null && getRequestId() != null) {
            return false;
        } else if (!compare.getRequestId().equals(getRequestId())) {
            return false;
        }
        if (compare.getEventType() == null && getEventType() != null) {
            return false;
        } else if (!compare.getEventType().equals(getEventType())) {
            return false;
        }
        if (compare.getPayload() == null && getPayload() != null) {
            return false;
        } else if (!compare.getPayload().equals(getPayload())) {
            return false;
        }

        return true;
    }

}
