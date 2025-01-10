package com.plantssoil.webhook.persists.registry;

import java.util.Date;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.webhook.beans.Event;
import com.plantssoil.webhook.beans.Event.EventStatus;
import com.plantssoil.webhook.core.IEvent;

/**
 * The event implementation with persistence<br/>
 * Used by {@link PersistedRegistry}<br/>
 * 
 * @author danialdy
 *
 */
public class PersistedEvent implements IEvent {
	private Event eventBean;

	/**
	 * Default Constructor
	 */
	public PersistedEvent() {
	}

	/**
	 * Constructor base on event bean, used for query result
	 * 
	 * @param eventBean the event bean
	 */
	public PersistedEvent(Event eventBean) {
		this.eventBean = eventBean;
	}

	/**
	 * Get the event bean
	 * 
	 * @return the event bean
	 */
	Event getEventBean() {
		if (this.eventBean == null) {
			this.eventBean = new Event();
			this.eventBean.setEventId(EntityUtils.getInstance().createUniqueObjectId());
		}
		return this.eventBean;
	}

	/**
	 * Set publisher id which event belongs to
	 * 
	 * @param publisherId publisher id
	 */
	public void setPublisherId(String publisherId) {
		getEventBean().setPublisherId(publisherId);
	}

	/**
	 * Get publisher id which event belongs to
	 * 
	 * @return publisher id
	 */
	public String getPublisherId() {
		return getEventBean().getPublisherId();
	}

	/**
	 * Set event status
	 * 
	 * @param eventStatus event status
	 */
	public void setEventStatus(EventStatus eventStatus) {
		getEventBean().setEventStatus(eventStatus);
	}

	/**
	 * Get event status
	 * 
	 * @return event status
	 */
	public EventStatus getEventStatus() {
		return getEventBean().getEventStatus();
	}

	/**
	 * Set created by
	 * 
	 * @param createdBy created by
	 */
	public void setCreatedBy(String createdBy) {
		getEventBean().setCreatedBy(createdBy);
	}

	/**
	 * Get created by
	 * 
	 * @return created by
	 */
	public String getCreatedBy() {
		return getEventBean().getCreatedBy();
	}

	/**
	 * Get creation time
	 * 
	 * @param creationTime creation time
	 */
	public void setCreationTime(Date creationTime) {
		getEventBean().setCreationTime(creationTime);
	}

	/**
	 * Set creation time
	 * 
	 * @return creation time
	 */
	public Date getCreationTime() {
		return getEventBean().getCreationTime();
	}

	@Override
	public void setEventId(String eventId) {
		getEventBean().setEventId(eventId);
	}

	@Override
	public void setEventType(String eventType) {
		getEventBean().setEventType(eventType);
	}

	@Override
	public void setEventTag(String eventTag) {
		getEventBean().setEventTag(eventTag);
	}

	@Override
	public void setContentType(String contentType) {
		getEventBean().setContentType(contentType);
	}

	@Override
	public void setCharset(String charset) {
		getEventBean().setCharset(charset);
	}

	@Override
	public String getEventId() {
		return getEventBean().getEventId();
	}

	@Override
	public String getEventType() {
		return getEventBean().getEventType();
	}

	@Override
	public String getEventTag() {
		return getEventBean().getEventTag();
	}

	@Override
	public String getContentType() {
		return getEventBean().getContentType();
	}

	@Override
	public String getCharset() {
		return getEventBean().getCharset();
	}

}
