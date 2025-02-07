package com.plantssoil.webhook.core;

import java.util.Date;

/**
 * The webhook log information, generated when webhook event & message is
 * triggered
 * 
 * @author danialdy
 * @Date 6 Feb 2025 2:38:23 pm
 */
public interface IWebhookLog {
    /**
     * Get the event type be triggered
     * 
     * @return The event type
     */
    public String getEventType();

    /**
     * Get the event tag
     * 
     * @return The event tag
     */
    public String getEventTag();

    /**
     * Get the content type (application/json, application/xml)
     * 
     * @return The content type
     */
    public String getContentType();

    /**
     * Get the charset of the payload (UTF-8)
     * 
     * @return The charset of the payload
     */
    public String getCharset();

    /**
     * Get the request id of the webhook log
     * 
     * @return The request id of the webhook log
     */
    public String getRequestId();

    /**
     * Get the payload (json, xml)
     * 
     * @return The payload (json, xml)
     */
    public String getPayload();

    /**
     * Get the webhook status
     * 
     * @return The webhook status
     */
    public String getWebhookStatus();

    /**
     * Get the trigger time
     * 
     * @return The trigger time
     */
    public Date getTriggerTime();

}
