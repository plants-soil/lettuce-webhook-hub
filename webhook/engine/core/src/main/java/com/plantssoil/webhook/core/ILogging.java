package com.plantssoil.webhook.core;

import java.util.List;

/**
 * The logger for webhook trigger, and consumer dispatcher
 * 
 * @author danialdy
 * @Date 22 Jan 2025 3:31:17 pm
 */
public interface ILogging {
    /**
     * Do logging when publisher trigger message
     * 
     * @param message The message triggered
     */
    public void triggerMessage(Message message);

    /**
     * Do logging when consumer dispatch message
     * 
     * @param message The message is dispatched
     * @param webhook The webhook which message is dispatched to
     * @param tryTime How many time try to dispatch the message
     */
    public void dispatchMessage(Message message, IWebhook webhook, int tryTime);

    /**
     * Do logging when webhook response message to consumer
     * 
     * @param message      The message is dispatched
     * @param webhook      The webhook which responses message
     * @param responseType The response type, could be success, fail, exception, etc
     * @param information  The response information or exception message
     */
    public void responseMessage(Message message, IWebhook webhook, String responseType, String information);

    /**
     * Find all webhook logs which belong to the specific publisher & dataGroup,
     * support pagination
     * 
     * @param publisherId ID of publisher which triggered events
     * @param dataGroup   Data group of publisher which triggered events (OPTIONAL)
     * @param page        The page index
     * @param pageSize    Maximum webhook logs on current page
     * @return Webhook Logs on current page
     * @see IWebhookLog
     */
    public List<IWebhookLog> findAllWebhookLogs(String publisherId, String dataGroup, int page, int pageSize);

    /**
     * Find all webhook logs which belong to the specific webhook, support
     * pagination
     * 
     * @param webhookId ID of webhook which received events
     * @param page      The page index
     * @param pageSize  Maximum webhook logs on current page
     * @return Webhook Logs on current page
     * @see IWebhookLog
     */
    public List<IWebhookLog> findAllWebhookLogs(String webhookId, int page, int pageSize);

    /**
     * Find all webhook log lines which belong to the specific request
     * 
     * @param requestId ID of request
     * @param webhookId ID of webhook
     * @return Webhook Log Lines
     * @see IWebhookLogLine
     */
    public List<IWebhookLogLine> findWebhookLogLines(String requestId, String webhookId);
}
