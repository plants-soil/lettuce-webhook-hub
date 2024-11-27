package com.plantssoil.webhook.core;

import java.util.Map;

import com.plantssoil.common.httpclient.IHttpPoster.SecurityStrategy;

/**
 * The subscriber of webhook
 * 
 * @author danialdy
 * @Date 13 Nov 2024 1:43:39 pm
 */
public interface IWebhookSubscriber extends IOrganization {

    /**
     * The subscriber app identity, may subscriber has multiple Applications
     * 
     * @return subscriber app id
     */
    public String getSubscriberAppId();

    /**
     * The security strategy subscriber chosen<br/>
     * <p>
     * Different header information will be added as the authentication base on this
     * security strategy
     * </p>
     * 
     * @return security strategy
     */
    public SecurityStrategy getSecurityStrategy();

    /**
     * The webhook URL, webhook engine will call this URL if subscribed webhook
     * event triggered
     * 
     * @return webhook URL
     */
    public String getWebhookUrl();

    /**
     * The customized headers defined by subscriber if needed<br/>
     * Customized Headers will be added into webhook callback request
     * ({@link IWebhookSubscriber#getWebhookUrl()}) header<br/>
     * 
     * 
     * @return customized headers, key - header name, value - correspond header
     *         value
     */
    public Map<String, String> getCustomizedHeaders();
}
