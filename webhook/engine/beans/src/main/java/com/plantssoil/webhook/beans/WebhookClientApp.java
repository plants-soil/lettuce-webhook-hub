package com.plantssoil.webhook.beans;

import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * The webhook client app
 * 
 * @author danialdy
 * @Date 19 Nov 2024 10:52:48 am
 */
@Entity
@Table(name = "LETTUCE_CLIENTAPP", uniqueConstraints = @UniqueConstraint(columnNames = "appName"), indexes = {
        @Index(name = "idx_clientapp_orgid", columnList = "organizationId") })
public class WebhookClientApp implements java.io.Serializable {
    private static final long serialVersionUID = -361959884539379835L;

    /**
     * The Security Strategy<br/>
     * <ul>
     * <li>{@link SecurityStrategy#SIGNATURE} - Will generate signature in header
     * when callback subscriber ({@link IOrganization#getWebhookUrl()})</li>
     * <li>{@link SecurityStrategy#TOKEN} - Will add the secret key as signature in
     * header when callback subscriber ({@link IOrganization#getWebhookUrl()})</li>
     * <li>{@link SecurityStrategy#NONE} - Won't add signature in header when
     * callback subscriber ({@link IOrganization#getWebhookUrl()})</li>
     * </ul>
     * 
     * @author danialdy
     * @Date 19 Nov 2024 10:52:16 am
     */
    public enum SecurityStrategy {
        SIGNATURE, TOKEN, NONE
    }

    public enum ClientAppStatus {
        TEST, AWAITING_FOR_APPROVEL, PRODUCTION
    }

    @Id
    private String clientAppId;
    private String organizationId;
    private String appName;
    private String appTag;
    private String description;
    @Enumerated(EnumType.STRING)
    private SecurityStrategy securityStrategy;
    private String WebhookUrl;
    private Map<String, String> customizedHeaders;
    private String[] trustedIps;
    @Enumerated(EnumType.STRING)
    private ClientAppStatus clientAppStatus;
    private String createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    public String getClientAppId() {
        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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

    public SecurityStrategy getSecurityStrategy() {
        return securityStrategy;
    }

    public void setSecurityStrategy(SecurityStrategy securityStrategy) {
        this.securityStrategy = securityStrategy;
    }

    public String getWebhookUrl() {
        return WebhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        WebhookUrl = webhookUrl;
    }

    public Map<String, String> getCustomizedHeaders() {
        return customizedHeaders;
    }

    public void setCustomizedHeaders(Map<String, String> customizedHeaders) {
        this.customizedHeaders = customizedHeaders;
    }

    public String[] getTrustedIps() {
        return trustedIps;
    }

    public void setTrustedIps(String[] trustedIps) {
        this.trustedIps = trustedIps;
    }

    public ClientAppStatus getClientAppStatus() {
        return clientAppStatus;
    }

    public void setClientAppStatus(ClientAppStatus clientAppStatus) {
        this.clientAppStatus = clientAppStatus;
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
