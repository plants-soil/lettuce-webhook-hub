package com.plantssoil.webhook.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The events subscribed by webhook, a webhook could subscribed multiple events
 * and listen message from these events
 * 
 * @author danialdy
 * @Date 28 Dec 2024 12:24:37 pm
 */
@Entity
@Table(name = "LETTUCE_EVENTSUB", uniqueConstraints = @UniqueConstraint(columnNames = { "webhookId", "eventId" }), indexes = {
		@Index(name = "idx_eventsub_webhookid", columnList = "webhookId"), @Index(name = "idx_eventsub_eventid", columnList = "eventId") })
public class EventSubscribed implements Serializable {
	private static final long serialVersionUID = 5996607146075281389L;

	@Id
	private String eventSubedId;
	private String webhookId;
	private String eventId;

	public String getEventSubedId() {
		return eventSubedId;
	}

	public void setEventSubedId(String eventSubedId) {
		this.eventSubedId = eventSubedId;
	}

	public String getWebhookId() {
		return webhookId;
	}

	public void setWebhookId(String webhookId) {
		this.webhookId = webhookId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

}
