package com.plantssoil.webhook.beans;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "LETTUCE_WEBHOOKEVENTLOGLINE", indexes = { @Index(name = "idx_wheventlogline_requestid", columnList = "requestId"),
        @Index(name = "idx_wheventlogline_subscriberId", columnList = "subscriberId") })
public class WebhookEventLogLine implements java.io.Serializable {
    private static final long serialVersionUID = 5368565382734058399L;
    @Id
    private String logLineId;
    private String subscriberId;
    private String requestId;
    private long executeMillseconds;
    private int responseCode;
    private String responseMessage;
    @Temporal(TemporalType.TIMESTAMP)
    private Date postTime;

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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getExecuteMillseconds() {
        return executeMillseconds;
    }

    public void setExecuteMillseconds(long executeMillseconds) {
        this.executeMillseconds = executeMillseconds;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Date getPostTime() {
        return postTime;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

}
