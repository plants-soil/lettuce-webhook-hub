package com.plantssoil.webhook.persists.beans;

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

import com.plantssoil.webhook.core.ClonableBean;
import com.plantssoil.webhook.core.IEvent;

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
@Table(name = "LETTUCE_EVENT", uniqueConstraints = @UniqueConstraint(columnNames = { "publisherId", "eventType" }), indexes = {
        @Index(name = "idx_event_pubid", columnList = "publisherId") })
public class Event extends ClonableBean implements IEvent {
    private static final long serialVersionUID = 5996607146075281389L;
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

    public Event() {
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String getEventTag() {
        return eventTag;
    }

    @Override
    public void setEventTag(String eventTag) {
        this.eventTag = eventTag;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    @Override
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
