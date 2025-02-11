package com.plantssoil.webhook.core;

import java.io.Serializable;

/**
 * The publisher event
 * <p>
 * Publisher may have different group of events, e.g:
 * <ul>
 * <li>product related events: <code>product.created, product.updated,
 * product.removed, product.approved</code></li>
 * <li>order related events:
 * <code>order.created, order.paid, order.packing, order.delivering, order.delivered</code></li>
 * </ul>
 * </p>
 * <p>
 * the event group is considered as <b>eventTag</b>, the different event is
 * considered as <b>eventType</b>
 * </p>
 * 
 * @author danialdy
 * @Date 29 Nov 2024 2:30:50 pm
 */
public interface IEvent extends Serializable {
    /**
     * The status of webhook event
     * 
     * @author danialdy
     * @Date 21 Nov 2024 2:57:36 pm
     */
    public enum EventStatus {
        SUBMITTED, PUBLISHED, RETIRED
    }

    /**
     * set the event id
     * 
     * @param eventId event id
     */
    public void setEventId(String eventId);

    /**
     * Set the event type
     * 
     * @param eventType event type
     */
    public void setEventType(String eventType);

    /**
     * Set the event tag (group)
     * 
     * @param eventTag event tag
     */
    public void setEventTag(String eventTag);

    /**
     * Set the content type of payload encapsulated (application/json,
     * application/xml)
     * 
     * @param contentType content type
     */
    public void setContentType(String contentType);

    /**
     * Set the content charset of payload encapsulated (UTF-8)
     * 
     * @param charset the content charset
     */
    public void setCharset(String charset);

    /**
     * Get the event id
     * 
     * @return event id
     */
    public String getEventId();

    /**
     * Get the event type
     * 
     * @return event type
     */
    public String getEventType();

    /**
     * Get the event tag (group)
     * 
     * @return event tag
     */
    public String getEventTag();

    /**
     * Get the content type
     * 
     * @return content type
     */
    public String getContentType();

    /**
     * Get the content charset
     * 
     * @return charset
     */
    public String getCharset();

}
