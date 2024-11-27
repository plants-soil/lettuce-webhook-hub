package com.plantssoil.webhook.core.impl;

/**
 * The message used to publish & consume by message service
 * 
 * @author danialdy
 * @Date 25 Nov 2024 12:04:11 pm
 */
public class DefaultWebhookMessage {
    private String publisherId;
    private String version;
    private String eventType;
    private String eventTag;
    private String contentType;
    private String charset;
    private String dataGroup;
    private String requestId;
    private String payload;

    public DefaultWebhookMessage() {
    }

    public DefaultWebhookMessage(String publisherId, String version, String eventType, String eventTag, String contentType, String charset, String dataGroup,
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

    public DefaultWebhookMessage publisherId(String publisherId) {
        this.publisherId = publisherId;
        return this;
    }

    public DefaultWebhookMessage version(String version) {
        this.version = version;
        return this;
    }

    public DefaultWebhookMessage eventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public DefaultWebhookMessage eventTag(String eventTag) {
        this.eventTag = eventTag;
        return this;
    }

    public DefaultWebhookMessage contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public DefaultWebhookMessage charset(String charset) {
        this.charset = charset;
        return this;
    }

    public DefaultWebhookMessage dataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
        return this;
    }

    public DefaultWebhookMessage requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public DefaultWebhookMessage payload(String payload) {
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
