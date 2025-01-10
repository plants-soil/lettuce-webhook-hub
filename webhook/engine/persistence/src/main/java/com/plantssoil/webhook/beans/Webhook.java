package com.plantssoil.webhook.beans;

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
public class Webhook implements Serializable {
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

	/**
	 * The webhook status
	 * <ul>
	 * <li>TEST - The webhook still under development or testing</li>
	 * <li>AWAITING_FOR_APPROVEL - submit to publisher and waiting for the
	 * approval</li>
	 * <li>PRODUCTION - Got approved and running in production</li>
	 * </ul>
	 * 
	 * @author danialdy
	 * @Date 29 Nov 2024 2:56:48 pm
	 */
	public enum WebhookStatus {
		TEST, AWAITING_FOR_APPROVEL, PRODUCTION
	}

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

	public String getWebhookId() {
		return webhookId;
	}

	public void setWebhookId(String webhookId) {
		this.webhookId = webhookId;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

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

	public String getWebhookSecret() {
		return webhookSecret;
	}

	public void setWebhookSecret(String webhookSecret) {
		this.webhookSecret = webhookSecret;
	}

	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	public String getPublisherVersion() {
		return publisherVersion;
	}

	public void setPublisherVersion(String publisherVersion) {
		this.publisherVersion = publisherVersion;
	}

	public SecurityStrategy getSecurityStrategy() {
		return securityStrategy;
	}

	public void setSecurityStrategy(SecurityStrategy securityStrategy) {
		this.securityStrategy = securityStrategy;
	}

	public String getWebhookUrl() {
		return webhookUrl;
	}

	public void setWebhookUrl(String webhookUrl) {
		this.webhookUrl = webhookUrl;
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

	public WebhookStatus getWebhookStatus() {
		return webhookStatus;
	}

	public void setWebhookStatus(WebhookStatus webhookStatus) {
		this.webhookStatus = webhookStatus;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

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
