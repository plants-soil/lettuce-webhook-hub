package com.plantssoil.webhook.persists.beans;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.plantssoil.webhook.core.IWebhookLog;

@Entity
@Table(name = "LETTUCE_WEBHOOKLOG", indexes = { @Index(name = "idx_wl_pubid", columnList = "publisherId"),
        @Index(name = "idx_wl_eventType", columnList = "eventType") })
public class WebhookLog implements IWebhookLog, java.io.Serializable {
    private static final long serialVersionUID = 4036647805497080463L;
    private String publisherId;
    private String version;
    private String dataGroup;
    private String eventType;
    private String eventTag;
    private String contentType;
    private String charset;
    @Id
    private String requestId;
    private String payload;
    private String webhookStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Date triggerTime;

    public WebhookLog() {
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
