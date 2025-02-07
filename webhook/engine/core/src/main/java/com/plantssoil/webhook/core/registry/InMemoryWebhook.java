package com.plantssoil.webhook.core.registry;

import java.util.Date;
import java.util.Map;

import com.plantssoil.webhook.core.ClonableBean;
import com.plantssoil.webhook.core.IWebhook;

/**
 * The in-memory implementation of IWebhook<br/>
 * All data will be lost when JVM shutdown<br/>
 * It's only for demonstration purpose, SHOULD AVOID be used in production
 * environment<br/>
 * 
 * @author danialdy
 * @Date 2 Jan 2025 5:10:09 pm
 */
public class InMemoryWebhook extends ClonableBean implements IWebhook {
    private static final long serialVersionUID = 4304298570867180416L;

    private String webhookId;
    private String subscriberId;
    private String appName;
    private String appTag;
    private String description;
    private String webhookSecret;
    private SecurityStrategy securityStrategy;
    private String webhookUrl;
    private Map<String, String> customizedHeaders;
    private String[] trustedIps;
    private WebhookStatus webhookStatus = WebhookStatus.TEST;
    private String publisherId;
    private String publisherVersion;
    private String accessToken;
    private String refreshToken;
    private String createdBy;
    private Date creationTime;

    @Override
    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    @Override
    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppTag() {
        return appTag;
    }

    public void setAppTag(String appTag) {
        this.appTag = appTag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    @Override
    public void setSecurityStrategy(SecurityStrategy securityStrategy) {
        this.securityStrategy = securityStrategy;
    }

    @Override
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void setCustomizedHeaders(Map<String, String> customizedHeaders) {
        this.customizedHeaders = customizedHeaders;
    }

    @Override
    public void setTrustedIps(String[] trustedIps) {
        this.trustedIps = trustedIps;
    }

    @Override
    public void setWebhookStatus(WebhookStatus webhookStatus) {
        this.webhookStatus = webhookStatus;
    }

    @Override
    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    @Override
    public void setPublisherVersion(String publisherVersion) {
        this.publisherVersion = publisherVersion;
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String getWebhookId() {
        return this.webhookId;
    }

    @Override
    public String getSubscriberId() {
        return this.subscriberId;
    }

    @Override
    public String getWebhookSecret() {
        return this.webhookSecret;
    }

    @Override
    public SecurityStrategy getSecurityStrategy() {
        return this.securityStrategy;
    }

    @Override
    public String getWebhookUrl() {
        return this.webhookUrl;
    }

    @Override
    public Map<String, String> getCustomizedHeaders() {
        return this.customizedHeaders;
    }

    @Override
    public String[] getTrustedIps() {
        return this.trustedIps;
    }

    @Override
    public WebhookStatus getWebhookStatus() {
        return this.webhookStatus;
    }

    @Override
    public String getPublisherId() {
        return this.publisherId;
    }

    @Override
    public String getPublisherVersion() {
        return this.publisherVersion;
    }

    @Override
    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public String getRefreshToken() {
        return this.refreshToken;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

}
