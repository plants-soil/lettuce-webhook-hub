package com.plantssoil.webhook.core;

/**
 * The message used to publish & consume by message service
 * 
 * @author danialdy
 * @Date 25 Nov 2024 12:04:11 pm
 */
public class Message {
    private String publisherId;
    private String version;
    private String eventType;
    private String eventTag;
    private String contentType;
    private String charset;
    private String dataGroup;
    private String requestId;
    private String payload;

    public Message() {
    }

    public Message(String publisherId, String version, String eventType, String eventTag, String contentType, String charset, String dataGroup,
            String requestId, String payload) {
        super();
        this.publisherId = publisherId;
        this.version = version;
        this.eventType = eventType;
        this.eventTag = eventTag;
        this.contentType = contentType;
        this.charset = charset;
        this.dataGroup = dataGroup;
        this.requestId = requestId;
        this.payload = payload;
    }

    public Message publisherId(String publisherId) {
        this.publisherId = publisherId;
        return this;
    }

    public Message version(String version) {
        this.version = version;
        return this;
    }

    public Message eventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public Message eventTag(String eventTag) {
        this.eventTag = eventTag;
        return this;
    }

    public Message contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Message charset(String charset) {
        this.charset = charset;
        return this;
    }

    public Message dataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
        return this;
    }

    public Message requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public Message payload(String payload) {
        this.payload = payload;
        return this;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventTag() {
        return eventTag;
    }

    public void setEventTag(String eventTag) {
        this.eventTag = eventTag;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getDataGroup() {
        return dataGroup;
    }

    public void setDataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
