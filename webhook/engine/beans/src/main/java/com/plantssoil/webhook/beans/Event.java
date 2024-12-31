package com.plantssoil.webhook.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * The events registered by publisher, pubisher may have multiple events.
 * e.g.:<br/>
 * product.created, product.approved, product.actived, order.created,
 * order.paid, order.delivered
 * 
 * @author danialdy
 * @Date 28 Dec 2024 12:22:28 pm
 */
@Entity
@Table(name = "LETTUCE_EVENT", uniqueConstraints = @UniqueConstraint(columnNames = "publisherId,eventType"), indexes = {
        @Index(name = "idx_event_pubid", columnList = "publisherId") })
public class Event implements Serializable {
    private static final long serialVersionUID = 5996607146075281389L;

    /**
     * The status of webhook event
     * 
     * @author danialdy
     * @Date 21 Nov 2024 2:57:36 pm
     */
    public enum EventStatus {
        SUBMITTED, PUBLISHED, RETIRED
    }

    @Id
    private String eventId;
    private String publisherId;
    private String eventType;
    private String eventTag;
    private String contentType;
    private String charset;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;
    private String createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
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

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

}
