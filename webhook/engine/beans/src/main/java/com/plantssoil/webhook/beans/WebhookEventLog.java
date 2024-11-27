package com.plantssoil.webhook.beans;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LETTUCE_WEBHOOKEVENTLOG")
public class WebhookEventLog implements java.io.Serializable {
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

    public String getWebhookStatus() {
        return webhookStatus;
    }

    public void setWebhookStatus(String webhookStatus) {
        this.webhookStatus = webhookStatus;
    }

}
