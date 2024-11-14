package com.plantssoil.webhook.core;

import java.util.Map;

/**
 * The subscriber of webhook
 * 
 * @author danialdy
 * @Date 13 Nov 2024 1:43:39 pm
 */
public interface IWebhookSubscriber {
    /**
     * The Security Strategy<br/>
     * <ul>
     * <li>{@link SecurityStrategy#SIGNATURE} - Will generate signature in header
     * when callback subscriber ({@link IWebhookSubscriber#getWebhookUrl()})</li>
     * <li>{@link SecurityStrategy#TOKEN} - Will add the secret key as signature in
     * header when callback subscriber
     * ({@link IWebhookSubscriber#getWebhookUrl()})</li>
     * <li>{@link SecurityStrategy#NONE} - Won't add signature in header when
     * callback subscriber ({@link IWebhookSubscriber#getWebhookUrl()})</li>
     * </ul>
     * 
     * @author danialdy
     * @Date 13 Nov 2024 2:13:00 pm
     */
    public enum SecurityStrategy {
        SIGNATURE, TOKEN, NONE
    }

    /**
     * The subscriber identity
     * 
     * @return subscriber id
     */
    public String getSubscriberId();

    /**
     * The subscriber app identity, may subscriber has multiple Applications
     * 
     * @return subscriber app id
     */
    public String getSubscriberAppId();

    /**
     * The secret key which is used to be Token or generate signature<br/>
     * <p>
     * This key will be as the token which is added in callback request
     * ({@link IWebhookSubscriber#getWebhookUrl()}) http header, if subscriber
     * choose {@link SecurityStrategy#TOKEN} as the security strategy
     * ({@link IWebhookSubscriber#getSecurityStrategy()})
     * </p>
     * <p>
     * The key is used to generate signature, the signature will be added in
     * callback request ({@link IWebhookSubscriber#getWebhookUrl()}) http header, if
     * subscriber choose {@link SecurityStrategy#SIGNATURE} as the security strategy
     * ({@link IWebhookSubscriber#getSecurityStrategy()})
     * </p>
     * 
     * @return secret key
     */
    public String getSecretKey();

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

    /**
     * The trusted IP list, if the callback URL domain is not one of the trusted,
     * will fail to callback
     * 
     * @return trusted IP list
     */
    public String[] getTrustedIps();
}
