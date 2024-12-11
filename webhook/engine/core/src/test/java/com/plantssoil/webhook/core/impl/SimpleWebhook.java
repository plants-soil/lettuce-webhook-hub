package com.plantssoil.webhook.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IWebhook;

public class SimpleWebhook implements IWebhook {
    private String webhookId;
    private String webhookSecret;
    private SecurityStrategy securityStrategy;
    private String webhookUrl;
    private Map<String, String> customizedHeaders;
    private String[] trustedIps;
    private WebhookStatus webhookStatus;
    private String publisherId;
    private String publisherVersion;
    private String accessToken;
    private String refreshToken;
    private List<IEvent> eventsSubscribed = new ArrayList<>();
    private List<IDataGroup> dataGroupsSubscribed = new ArrayList<>();

    @Override
    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
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
    public void setPubliserhVersion(String publisherVersion) {
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
    public void subscribeEvent(IEvent event) {
        this.eventsSubscribed.add(event);
    }

    @Override
    public void subscribeEvent(List<IEvent> events) {
        this.eventsSubscribed.addAll(events);
    }

    @Override
    public void subscribeDataGroup(IDataGroup dataGroup) {
        this.dataGroupsSubscribed.add(dataGroup);
    }

    @Override
    public void subscribeDataGroup(List<IDataGroup> dataGroups) {
        this.dataGroupsSubscribed.addAll(dataGroups);
    }

    @Override
    public String getWebhookId() {
        return this.webhookId;
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

    @Override
    public List<IEvent> findSubscribedEvents(int page, int pageSize) {
        List<IEvent> list = new ArrayList<>();
        int beginIndex = page * pageSize;
        int endIndex = beginIndex + pageSize;

        for (int i = beginIndex; i < this.eventsSubscribed.size() && i < endIndex; i++) {
            list.add(this.eventsSubscribed.get(i));
        }
        return list;
    }

    @Override
    public List<IDataGroup> findSubscribedDataGroups(int page, int pageSize) {
        List<IDataGroup> list = new ArrayList<>();
        int beginIndex = page * pageSize;
        int endIndex = beginIndex + pageSize;

        for (int i = beginIndex; i < this.dataGroupsSubscribed.size() && i < endIndex; i++) {
            list.add(this.dataGroupsSubscribed.get(i));
        }
        return list;
    }

    @Override
    public IDataGroup findSubscribedDataGroup(String dataGroup) {
        IDataGroup dg = null;
        for (IDataGroup d : this.dataGroupsSubscribed) {
            if (d.getDataGroup().equals(dataGroup)) {
                dg = d;
                break;
            }
        }
        return dg;
    }

}
