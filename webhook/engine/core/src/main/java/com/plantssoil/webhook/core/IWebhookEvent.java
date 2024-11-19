package com.plantssoil.webhook.core;

/**
 * The webhook event
 * 
 * @author danialdy
 * @Date 12 Nov 2024 3:12:39 pm
 */
public interface IWebhookEvent extends java.io.Serializable {
    /**
     * The publisher id (mostly it's the organization id which produces Webhook
     * events)
     * 
     * @return the publisher id
     */
    public String getPublisherId();

    /**
     * The webhook version, webhook can support different version
     * 
     * @return webhook version
     */
    public String getVersion();

    /**
     * Webhook data group (could be null if not multi-datagroup managed). Some
     * organization need separate data between different business units or
     * merchants, could use data group to identify
     * 
     * @return data group
     */
    public String getDataGroup();

    /**
     * The event type, may include business domain and action (or status) name<br/>
     * From some best practices, could named event type as:
     * 
     * <pre>
     * <code>
     * product.created, product.updated, product.disabled
     * order.created, order.confirmed, order.paid, order.fulfilling, order.delivered
     * </code>
     * </pre>
     * 
     * @return event type name
     */
    public String getEventType();

    /**
     * The event tag, if need authorize subscriber to some business scope, could use
     * event tag to group the event type.<br/>
     * e.g:
     * <ul>
     * <li>Event type "product.created", "product.updated", "product.disabled" could
     * belong to the same event tag "product"</li>
     * <li>Event type "order.created", "order.confirmed", "order.paid",
     * "order.fulfilling", "order.delivered" could belong to the same event tag
     * "order"</li>
     * </ul>
     * Event tag is not mandatory, could get the event tag here if publisher defined
     * webhook event with the event tag
     * 
     * @return event tag
     */
    public String getEventTag();

    /**
     * Get the content type, such as "application/json", "application/xml", etc.
     * 
     * @return content type, defaults to "application/json"
     */
    public String getContentType();

    /**
     * Get the Charset of content encoding, such as "UTF-8"
     * 
     * @return Encoding Charset, defaults to "UTF-8"
     */
    public String getCharset();
}
