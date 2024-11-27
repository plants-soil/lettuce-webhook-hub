package com.plantssoil.webhook.core.impl;

import java.util.Map;

import com.plantssoil.common.httpclient.IHttpPoster;
import com.plantssoil.webhook.beans.Organization;
import com.plantssoil.webhook.beans.WebhookClientApp;
import com.plantssoil.webhook.core.IWebhookSubscriber;

/**
 * The default implementation of webhook subscriber (with persistence)
 * 
 * @author danialdy
 * @Date 21 Nov 2024 3:19:30 pm
 */
public class DefaultWebhookSubscriber implements IWebhookSubscriber {
    private static final long serialVersionUID = 3029888345870011663L;
    private Organization organization;
    private WebhookClientApp webhookClientApp;

    /**
     * The default constructor from persisted organization & webhookClientApp
     * 
     * @param organization     organization instance
     * @param webhookClientApp webhook client app instance
     */
    protected DefaultWebhookSubscriber(Organization organization, WebhookClientApp webhookClientApp) {
        this.organization = organization;
        this.webhookClientApp = webhookClientApp;
    }

    @Override
    public String getOrganizationId() {
        return this.organization.getOrganizationId();
    }

    @Override
    public String getOrganizationName() {
        return this.organization.getOrganizationId();
    }

    @Override
    public String getWebsite() {
        return this.organization.getWebsite();
    }

    @Override
    public String getLogoLink() {
        return this.organization.getLogoLink();
    }

    @Override
    public String getSecretKey() {
        return this.organization.getSecretKey();
    }

    @Override
    public String getSubscriberAppId() {
        return this.webhookClientApp.getClientAppId();
    }

    @Override
    public IHttpPoster.SecurityStrategy getSecurityStrategy() {
        return IHttpPoster.SecurityStrategy.valueOf(this.webhookClientApp.getSecurityStrategy().name());
    }

    @Override
    public String getWebhookUrl() {
        return this.webhookClientApp.getWebhookUrl();
    }

    @Override
    public Map<String, String> getCustomizedHeaders() {
        return this.webhookClientApp.getCustomizedHeaders();
    }

    @Override
    public String[] getTrustedIps() {
        return this.webhookClientApp.getTrustedIps();
    }

}
