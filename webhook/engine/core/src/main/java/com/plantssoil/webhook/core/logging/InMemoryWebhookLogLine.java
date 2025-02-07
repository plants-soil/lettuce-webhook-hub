package com.plantssoil.webhook.core.logging;

import java.util.Date;

import com.plantssoil.webhook.core.ClonableBean;
import com.plantssoil.webhook.core.IWebhookLogLine;

public class InMemoryWebhookLogLine extends ClonableBean implements IWebhookLogLine, java.io.Serializable {
    private static final long serialVersionUID = 5368565382734058399L;

    private String logLineId;
    private String subscriberId;
    private String webhookId;
    private String requestId;
    private String logType;
    private String information;
    private Date logTime;
    private int tryTime;

    public InMemoryWebhookLogLine() {
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
