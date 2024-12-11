package com.plantssoil.webhook.core;

/**
 * The message used to publish & consume by message service
 * 
 * @author danialdy
 * @Date 25 Nov 2024 12:04:11 pm
 */
public class Message {
    private String publisherId;
    private String version;
    private String eventType;
    private String eventTag;
    private String contentType;
    private String charset;
    private String dataGroup;
    private String requestId;
    private String payload;

    public Message() {
    }

    /**
     * The constructor of message
     * 
     * @param publisherId the publisher id, required
     * @param version     the publisher version, required
     * @param eventType   the event type, required
     * @param eventTag    the event tag, optional (could be null)
     * @param contentType the content type, http body media type (application/json,
     *                    application/xml), optional (could be null)
     * @param charset     the charset of http body encoding/decoding (UTF-8, etc.),
     *                    optional (could be null)
     * @param dataGroup   the data group if have multi-datagroups, optional (could
     *                    be null)
     * @param requestId   the request id, which used to avoid duplication for
     *                    webhook, required
     * @param payload     the request body, mostly in JSON format and represents
     *                    business content, required
     */
    public Message(String publisherId, String version, String eventType, String eventTag, String contentType, String charset, String dataGroup,
            String requestId, String payload) {
        super();
        this.publisherId = publisherId;
        this.version = version;
        this.eventType = eventType;
        this.eventTag = eventTag;
        this.contentType = contentType;
        this.charset = charset;
        this.dataGroup = dataGroup;
        this.requestId = requestId;
        this.payload = payload;
    }

    /**
     * set the publisher id, required
     * 
     * @param publisherId publisher id
     * @return this message instance
     */
    public Message publisherId(String publisherId) {
        this.publisherId = publisherId;
        return this;
    }

    /**
     * set the publisher version, required
     * 
     * @param version publisher version
     * @return this message instance
     */
    public Message version(String version) {
        this.version = version;
        return this;
    }

    /**
     * set the event type, required
     * 
     * @param eventType event type, usually defined in format with business meaning,
     *                  like "order.created" / "order.paid" / "order.delivering" /
     *                  "order.delivered"
     * @return this message instance
     */
    public Message eventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    /**
     * set the event tag, optional (could be null)
     * 
     * @param eventTag event tag
     * @return this message instance
     */
    public Message eventTag(String eventTag) {
        this.eventTag = eventTag;
        return this;
    }

    /**
     * set the content type, http body media type (application/json,
     * application/xml), optional (could be null)
     * 
     * @param contentType content type, http body media type (application/json,
     *                    application/xml)
     * @return this message instance
     */
    public Message contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * set the charset of http body encoding/decoding (UTF-8, etc.), optional (could
     * be null)
     * 
     * @param charset charset of http body encoding/decoding (UTF-8, etc.)
     * @return this message instance
     */
    public Message charset(String charset) {
        this.charset = charset;
        return this;
    }

    /**
     * set the data group if have multi-datagroups, optional (could be null)
     * 
     * @param dataGroup data group if have multi-datagroups
     * @return this message instance
     */
    public Message dataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
        return this;
    }

    /**
     * set the request id, which used to avoid duplication for webhook, required
     * 
     * @param requestId request id
     * @return this message instance
     */
    public Message requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * set the request body, mostly in JSON format and represents business content,
     * required
     * 
     * @param payload request body, mostly in JSON format and represents business
     *                content
     * @return this message instance
     */
    public Message payload(String payload) {
        this.payload = payload;
        return this;
    }

    /**
     * get the publisher id
     * 
     * @return the publisher id
     */
    public String getPublisherId() {
        return publisherId;
    }

    /**
     * get the publisher version
     * 
     * @return the publisher version
     */
    public String getVersion() {
        return version;
    }

    /**
     * get the event type, usually defined in format with business meaning, like
     * "order.created" / "order.paid" / "order.delivering" / "order.delivered"
     * 
     * @return the event type
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * get the event tag
     * 
     * @return the event tag
     */
    public String getEventTag() {
        return eventTag;
    }

    /**
     * get the content type, http body media type (application/json,
     * application/xml)
     * 
     * @return the content type, http body media type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * get the charset of http body encoding/decoding (UTF-8, etc.)
     * 
     * @return the charset of http body encoding/decoding
     */
    public String getCharset() {
        return charset;
    }

    /**
     * get the data group if have multi-datagroups
     * 
     * @return the data group
     */
    public String getDataGroup() {
        return dataGroup;
    }

    /**
     * get the request id, which used to avoid duplication for webhook
     * 
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * get the request body, mostly in JSON format and represents business content
     * 
     * @return the request body
     */
    public String getPayload() {
        return payload;
    }
}
