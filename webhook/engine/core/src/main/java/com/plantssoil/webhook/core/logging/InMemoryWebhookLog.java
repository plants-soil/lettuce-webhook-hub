package com.plantssoil.webhook.core.logging;

import java.util.Date;

import com.plantssoil.webhook.core.ClonableBean;
import com.plantssoil.webhook.core.IWebhookLog;

public class InMemoryWebhookLog extends ClonableBean implements IWebhookLog, java.io.Serializable {
    private static final long serialVersionUID = 4036647805497080463L;

    private String publisherId;
    private String version;
    private String dataGroup;
    private String eventType;
    private String eventTag;
    private String contentType;
    private String charset;
    private String requestId;
    private String payload;
    private String webhookStatus;
    private Date triggerTime;

    public InMemoryWebhookLog() {
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

    public String getDataGroup() {
        return dataGroup;
    }

    public void setDataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String getEventTag() {
        return eventTag;
    }

    public void setEventTag(String eventTag) {
        this.eventTag = eventTag;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String getWebhookStatus() {
        return webhookStatus;
    }

    public void setWebhookStatus(String webhookStatus) {
        this.webhookStatus = webhookStatus;
    }

    @Override
    public Date getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Date triggerTime) {
        this.triggerTime = triggerTime;
    }

}
