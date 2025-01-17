package com.plantssoil.webhook.core;

import java.util.Map;

/**
 * The webhook, webhook could be considered as client application, one
 * subscriber could have multiple webhooks if have multiple client application.
 * <p>
 * If the webhook property changed, such as security strategy / request headers
 * / webhook id / webhook secret / etc., need call
 * {@link IRegistry#updateWebhook} to ensure consumer to reloaded & re-subscribe
 * </p>
 * 
 * @author danialdy
 * @Date 29 Nov 2024 2:52:09 pm
 * @see ISubscriber
 */
public interface IWebhook {
    /**
     * The security strategy<br/>
     * This strategy determines how to signature the webhook request<br/>
     * <ul>
     * <li>SIGNATURE - Signature the webhook request with access token + timestamp +
     * request id + payload</li>
     * <li>TOKEN - Signature the webhook request with static access token</li>
     * <li>NONE - Not signature the webhook request</li>
     * </ul>
     * 
     * @author danialdy
     * @Date 29 Nov 2024 2:53:02 pm
     */
    public enum SecurityStrategy {
        /**
         * Signature the webhook request with access token + timestamp + request id +
         * payload
         */
        SIGNATURE,
        /**
         * Signature the webhook request with static access token
         */
        TOKEN,
        /**
         * Not signature the webhook request
         */
        NONE
    }

    /**
     * The webhook status
     * <ul>
     * <li>TEST - The webhook still under development or testing</li>
     * <li>AWAITING_FOR_APPROVEL - submit to publisher and waiting for the
     * approval</li>
     * <li>PRODUCTION - Got approved and running in production</li>
     * <li>INACTIVE - Got approved but deactivated</li>
     * </ul>
     * 
     * @author danialdy
     * @Date 29 Nov 2024 2:56:48 pm
     */
    public enum WebhookStatus {
        /**
         * The webhook still under development or testing
         */
        TEST,
        /**
         * submit to publisher and waiting for the approval
         */
        AWAITING_FOR_APPROVEL,
        /**
         * Got approved and running in production
         */
        PRODUCTION,
        /**
         * Got approved but deactivated
         */
        INACTIVE
    }

    /**
     * Set webhook id
     * 
     * @param webhookId webhook id
     */
    public void setWebhookId(String webhookId);

    /**
     * set subscriber id
     * 
     * @param subscriberId subscriber id
     */
    public void setSubscriberId(String subscriberId);

    /**
     * Set webhook secret
     * 
     * @param webhookSecret webhook secret
     */
    public void setWebhookSecret(String webhookSecret);

    /**
     * Set security strategy
     * 
     * @param securityStrategy security strategy
     * @see SecurityStrategy
     */
    public void setSecurityStrategy(SecurityStrategy securityStrategy);

    /**
     * Set webhook url
     * 
     * @param webhookUrl webhook url
     */
    public void setWebhookUrl(String webhookUrl);

    /**
     * Set customized headers (these headers will be added in to webhook request
     * headers when call webhook url)
     * 
     * @param customizedHeaders customized headers
     */
    public void setCustomizedHeaders(Map<String, String> customizedHeaders);

    /**
     * Set trusted IPs
     * 
     * @param trustedIps trusted IPs
     */
    public void setTrustedIps(String[] trustedIps);

    /**
     * Set webhook status
     * 
     * @param webhookStatus webhook status
     * @see WebhookStatus
     */
    public void setWebhookStatus(WebhookStatus webhookStatus);

    /**
     * Set the publisher id subscribed
     * 
     * @param publisherId publisher id subscribed
     */
    public void setPublisherId(String publisherId);

    /**
     * Set the publisher version subscribed
     * 
     * @param publisherVersion publisher version subscribed
     */
    public void setPublisherVersion(String publisherVersion);

    /**
     * Set access token
     * 
     * @param accessToken access token
     */
    public void setAccessToken(String accessToken);

    /**
     * Set refresh token
     * 
     * @param refreshToken refresh token
     */
    public void setRefreshToken(String refreshToken);

    /**
     * Get webhook id
     * 
     * @return webhook id
     */
    public String getWebhookId();

    /**
     * Get subscriber id
     * 
     * @return subscriber id
     */
    public String getSubscriberId();

    /**
     * Get webhook secret
     * 
     * @return webhook secret
     */
    public String getWebhookSecret();

    /**
     * Get security strategy
     * 
     * @return security strategy
     * @see SecurityStrategy
     */
    public SecurityStrategy getSecurityStrategy();

    /**
     * Get webhook URL
     * 
     * @return webhook URL
     */
    public String getWebhookUrl();

    /**
     * Get customized headers (these headers will be added in to webhook request
     * headers when call webhook url)
     * 
     * @return customized headers
     */
    public Map<String, String> getCustomizedHeaders();

    /**
     * Get trusted IPs
     * 
     * @return trusted IPs
     */
    public String[] getTrustedIps();

    /**
     * Get webhook status
     * 
     * @return webhook status
     * @see WebhookStatus
     */
    public WebhookStatus getWebhookStatus();

    /**
     * Get publisher id subscribed
     * 
     * @return publisher id
     */
    public String getPublisherId();

    /**
     * Get publisher version subscribed
     * 
     * @return publisher version
     */
    public String getPublisherVersion();

    /**
     * Get access token on subscribed publisher
     * 
     * @return access token
     */
    public String getAccessToken();

    /**
     * Get refresh token to update access token
     * 
     * @return refresh token
     */
    public String getRefreshToken();
}
