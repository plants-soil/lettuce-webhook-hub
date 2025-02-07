package com.plantssoil.webhook.core;

import java.util.Date;

/**
 * The webhook log line information, hold the dispatch / response logs
 * 
 * @author danialdy
 * @Date 6 Feb 2025 2:44:11 pm
 */
public interface IWebhookLogLine {
    /**
     * Get the log type (dispatch, success, fail)
     * 
     * @return The log type
     */
    public String getLogType();

    /**
     * Get the log information
     * 
     * @return The log information
     */
    public String getInformation();

    /**
     * Get the log time
     * 
     * @return The log time
     */
    public Date getLogTime();

    /**
     * Get the webhook dispatching tried times (Will try 3 times when failed to
     * dispatch webhook)
     * 
     * @return The webhook dispatching tried times
     */
    public int getTryTime();
}
