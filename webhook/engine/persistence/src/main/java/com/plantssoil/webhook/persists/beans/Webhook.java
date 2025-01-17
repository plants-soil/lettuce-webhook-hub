package com.plantssoil.webhook.persists.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.plantssoil.common.persistence.converter.ArrayStringConverter;
import com.plantssoil.common.persistence.converter.MapStringConverter;
import com.plantssoil.webhook.core.IWebhook;

/**
 * Webhook, also as the client app, which has webhook URL to receive events &
 * messages from publisher
 * 
 * @author danialdy
 * @Date 28 Dec 2024 12:27:00 pm
 */
@Entity
@Table(name = "LETTUCE_WEBHOOK", indexes = { @Index(name = "idx_webhook_subid", columnList = "subscriberId"),
        @Index(name = "idx_webhook_pubid", columnList = "publisherId") })
public class Webhook implements IWebhook, Serializable {
    private static final long serialVersionUID = -9107067549692351256L;

    @Id
    private String webhookId;
    private String subscriberId;
    @Column(unique = true)
    private String appName;
    private String appTag;
    private String description;
    private String webhookSecret;
    private String publisherId;
    private String publisherVersion;
    @Enumerated(EnumType.STRING)
    private SecurityStrategy securityStrategy;
    private String webhookUrl;
    @Convert(converter = MapStringConverter.class)
    private Map<String, String> customizedHeaders;
    @Convert(converter = ArrayStringConverter.class)
    private String[] trustedIps;
    @Enumerated(EnumType.STRING)
    private WebhookStatus webhookStatus;
    private String accessToken;
    private String refreshToken;
    private String createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    public Webhook() {
    }

    @Override
    public String getWebhookId() {
        return webhookId;
    }

    @Override
    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    @Override
    public String getSubscriberId() {
        return subscriberId;
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
    public String getWebhookSecret() {
        return webhookSecret;
    }

    @Override
    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    @Override
    public String getPublisherId() {
        return publisherId;
    }

    @Override
    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    @Override
    public String getPublisherVersion() {
        return publisherVersion;
    }

    @Override
    public void setPublisherVersion(String publisherVersion) {
        this.publisherVersion = publisherVersion;
    }

    @Override
    public SecurityStrategy getSecurityStrategy() {
        return securityStrategy;
    }

    @Override
    public void setSecurityStrategy(SecurityStrategy securityStrategy) {
        this.securityStrategy = securityStrategy;
    }

    @Override
    public String getWebhookUrl() {
        return webhookUrl;
    }

    @Override
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    @Override
    public Map<String, String> getCustomizedHeaders() {
        return customizedHeaders;
    }

    @Override
    public void setCustomizedHeaders(Map<String, String> customizedHeaders) {
        this.customizedHeaders = customizedHeaders;
    }

    @Override
    public String[] getTrustedIps() {
        return trustedIps;
    }

    @Override
    public void setTrustedIps(String[] trustedIps) {
        this.trustedIps = trustedIps;
    }

    @Override
    public WebhookStatus getWebhookStatus() {
        return webhookStatus;
    }

    @Override
    public void setWebhookStatus(WebhookStatus webhookStatus) {
        this.webhookStatus = webhookStatus;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
