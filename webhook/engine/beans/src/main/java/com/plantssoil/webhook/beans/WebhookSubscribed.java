package com.plantssoil.webhook.beans;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "LETTUCE_WEBHOOKSUBSCRIBED", uniqueConstraints = @UniqueConstraint(columnNames = "clientAppId,eventOrganizationId,eventDataGroup,eventId"), indexes = {
        @Index(name = "idx_whsubscribed_clientorgid", columnList = "clientOrganizationId"),
        @Index(name = "idx_whsubscribed_clientappid", columnList = "clientAppId"),
        @Index(name = "idx_whsubscribed_eventorgid", columnList = "eventOrganizationId"),
        @Index(name = "idx_whsubscribed_datagroup", columnList = "eventDataGroup"), @Index(name = "idx_whsubscribed_eventid", columnList = "eventId") })
public class WebhookSubscribed implements java.io.Serializable {
    private static final long serialVersionUID = -3373068800005798198L;
    @Id
    private String subscribedId;
    private String clientOrganizationId;
    private String clientAppId;
    private String eventOrganizationId;
    private String eventDataGroup;
    private String eventId;

    public String getSubscribedId() {
        return subscribedId;
    }

    public void setSubscribedId(String subscribedId) {
        this.subscribedId = subscribedId;
    }

    public String getClientOrganizationId() {
        return clientOrganizationId;
    }

    public void setClientOrganizationId(String clientOrganizationId) {
        this.clientOrganizationId = clientOrganizationId;
    }

    public String getClientAppId() {
        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }

    public String getEventOrganizationId() {
        return eventOrganizationId;
    }

    public void setEventOrganizationId(String eventOrganizationId) {
        this.eventOrganizationId = eventOrganizationId;
    }

    public String getEventDataGroup() {
        return eventDataGroup;
    }

    public void setEventDataGroup(String eventDataGroup) {
        this.eventDataGroup = eventDataGroup;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

}
