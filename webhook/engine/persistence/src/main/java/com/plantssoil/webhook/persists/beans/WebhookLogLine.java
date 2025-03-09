package com.plantssoil.webhook.persists.beans;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.plantssoil.webhook.core.IWebhookLogLine;

@Entity
@Table(name = "LETTUCE_WEBHOOKLOGLINE", indexes = { @Index(name = "idx_wll_requestid", columnList = "requestId"),
        @Index(name = "idx_wll_subscriberId", columnList = "subscriberId"), @Index(name = "idx_wll_webhookId", columnList = "webhookId") })
public class WebhookLogLine implements IWebhookLogLine, java.io.Serializable {
    private static final long serialVersionUID = 5368565382734058399L;
    @Id
    private String logLineId;
    private String subscriberId;
    private String webhookId;
    private String requestId;
    private String logType;
    @Lob
    private String information;
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTime;
    private int tryTime;

    public WebhookLogLine() {
    }

    public String getLogLineId() {
        return logLineId;
    }

    public void setLogLineId(String logLineId) {
        this.logLineId = logLineId;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getWebhookId() {
        return webhookId;
    }

    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    @Override
    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    @Override
    public int getTryTime() {
        return tryTime;
    }

    public void setTryTime(int tryTime) {
        this.tryTime = tryTime;
    }

}
