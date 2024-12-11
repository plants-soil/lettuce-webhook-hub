package com.plantssoil.webhook.core.impl;

import com.plantssoil.webhook.core.IEvent;

public class SimpleEvent implements IEvent {
    private String eventId;
    private String eventType;
    private String eventTag;
    private String contentType;
    private String charset;

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String getEventTag() {
        return eventTag;
    }

    @Override
    public void setEventTag(String eventTag) {
        this.eventTag = eventTag;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }

}
