package com.plantssoil.webhook.resteasy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.common.persistence.converter.MapStringConverter;
import com.plantssoil.webhook.api.NotFoundException;
import com.plantssoil.webhook.api.WebhookApiService;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.IWebhook.SecurityStrategy;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;
import com.plantssoil.webhook.core.registry.InMemoryWebhook;

@ApplicationScoped
@Default
public class WebhookApiServiceImpl implements WebhookApiService {
    public WebhookApiServiceImpl() {
    }

    @Override
    public Response activateWebhook(String webhookId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            r.activateWebhook(webhook);
            return ResponseBuilder.ok().data(webhook).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response addWebhook(InMemoryWebhook body, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            if (body.getWebhookId() == null) {
                body.setWebhookId(EntityUtils.getInstance().createUniqueObjectId());
            }
            if (body.getAccessToken() != null) {
                body.setAccessToken(null);
            }
            if (body.getRefreshToken() != null) {
                body.setRefreshToken(null);
            }
            if (body.getWebhookSecret() != null) {
                body.setWebhookSecret(null);
            }
            body.setWebhookStatus(WebhookStatus.TEST);
            IWebhook webhook = (IWebhook) BeanBridge.getInstance().bridge(body);
            r.addWebhook(webhook);
            return ResponseBuilder.ok().data(body).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response addWebhook(String webhookId, String subscriberId, String appName, String appTag, String description, String webhookSecret,
            String publisherId, String publisherVersion, String securityStrategy, String webhookUrl, String customizedHeaders, List<String> trustedIps,
            String webhookStatus, String createdBy, Date creationTime, SecurityContext securityContext) throws NotFoundException {
        InMemoryWebhook webhook = new InMemoryWebhook();
        webhook.setWebhookId(webhookId);
        webhook.setSubscriberId(subscriberId);
        webhook.setAppName(appName);
        webhook.setAppTag(appTag);
        webhook.setDescription(description);
        webhook.setPublisherId(publisherId);
        webhook.setPublisherVersion(publisherVersion);
        if (securityStrategy != null) {
            webhook.setSecurityStrategy(SecurityStrategy.valueOf(securityStrategy));
        }
        webhook.setWebhookUrl(webhookUrl);
        if (customizedHeaders != null) {
            webhook.setCustomizedHeaders(new MapStringConverter().convertToEntityAttribute(customizedHeaders));
        }
        if (trustedIps != null) {
            webhook.setTrustedIps(trustedIps.toArray(new String[] {}));
        }
        webhook.setCreatedBy(createdBy);
        webhook.setCreationTime(creationTime);
        return addWebhook(webhook, securityContext);
    }

    @Override
    public Response deactivateWebhook(String webhookId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            r.deactivateWebhook(webhook);
            return ResponseBuilder.ok().data(webhook).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findAllWebhooks(String subscriberId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            List<IWebhook> webhooks = r.findWebhooks(subscriberId, page, pageSize);
            return ResponseBuilder.ok().data(webhooks).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findSubscribedDataGroup(String webhookId, String dataGroup, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IDataGroup dg = r.findSubscribedDataGroup(webhookId, dataGroup);
            return ResponseBuilder.ok().data(dg).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findSubscribedDataGroups(String webhookId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            List<IDataGroup> dgs = r.findSubscribedDataGroups(webhookId, page, pageSize);
            return ResponseBuilder.ok().data(dgs).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findSubscribedEvents(String webhookId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            List<IEvent> events = r.findSubscribedEvents(webhookId, page, pageSize);
            return ResponseBuilder.ok().data(events).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findWebhookById(String webhookId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            return ResponseBuilder.ok().data(webhook).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response subscribeDataGroup(String webhookId, String dataGroupId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            IDataGroup dataGroup = r.findDataGroup(dataGroupId);
            r.subscribeDataGroup(webhook, dataGroup);
            return ResponseBuilder.ok().build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response subscribeDataGroups(String webhookId, List<String> dataGroupIds, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            List<IDataGroup> dataGroups = new ArrayList<>();
            for (String id : dataGroupIds) {
                IDataGroup dataGroup = r.findDataGroup(id);
                dataGroups.add(dataGroup);
            }
            r.subscribeDataGroup(webhook, dataGroups);
            return ResponseBuilder.ok().build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response subscribeEvent(String webhookId, String eventId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            IEvent event = r.findEvent(eventId);
            r.subscribeEvent(webhook, event);
            return ResponseBuilder.ok().build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response subscribeEvents(String webhookId, List<String> eventIds, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            List<IEvent> events = new ArrayList<>();
            for (String id : eventIds) {
                IEvent event = r.findEvent(id);
                events.add(event);
            }
            r.subscribeEvent(webhook, events);
            return ResponseBuilder.ok().build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response unsubscribeDataGroup(String webhookId, String dataGroupId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            IDataGroup dataGroup = r.findDataGroup(dataGroupId);
            r.unsubscribeDataGroup(webhook, dataGroup);
            return ResponseBuilder.ok().build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response unsubscribeDataGroups(String webhookId, List<String> dataGroupIds, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            List<IDataGroup> dataGroups = new ArrayList<>();
            for (String id : dataGroupIds) {
                IDataGroup dataGroup = r.findDataGroup(id);
                dataGroups.add(dataGroup);
            }
            r.unsubscribeDataGroup(webhook, dataGroups);
            return ResponseBuilder.ok().build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response unsubscribeEvent(String webhookId, String eventId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            IEvent event = r.findEvent(eventId);
            r.unsubscribeEvent(webhook, event);
            return ResponseBuilder.ok().build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response unsubscribeEvents(String webhookId, List<String> eventIds, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            List<IEvent> events = new ArrayList<>();
            for (String id : eventIds) {
                IEvent event = r.findEvent(id);
                events.add(event);
            }
            r.unsubscribeEvent(webhook, events);
            return ResponseBuilder.ok().build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response updateWebhookById(InMemoryWebhook body, String webhookId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IWebhook webhook = r.findWebhook(webhookId);
            InMemoryWebhook old = BeanBridge.getInstance().bridge(webhook, InMemoryWebhook.class);
            updateWebhookValue(old, body);
            webhook = (IWebhook) BeanBridge.getInstance().bridge(old);
            r.updateWebhook(webhook);
            return ResponseBuilder.ok().data(old).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    private void updateWebhookValue(InMemoryWebhook old, InMemoryWebhook updated) {
        if (updated.getSubscriberId() != null && !Objects.equals(updated.getSubscriberId(), old.getSubscriberId())) {
            old.setSubscriberId(updated.getSubscriberId());
        }
        if (updated.getAppName() != null && !Objects.equals(updated.getAppName(), old.getAppName())) {
            old.setAppName(updated.getAppName());
        }
        if (updated.getAppTag() != null && !Objects.equals(updated.getAppTag(), old.getAppTag())) {
            old.setAppTag(updated.getAppTag());
        }
        if (updated.getDescription() != null && !Objects.equals(updated.getDescription(), old.getDescription())) {
            old.setDescription(updated.getDescription());
        }
        if (updated.getPublisherId() != null && !Objects.equals(updated.getPublisherId(), old.getPublisherId())) {
            old.setPublisherId(updated.getPublisherId());
        }
        if (updated.getPublisherVersion() != null && !Objects.equals(updated.getPublisherVersion(), old.getPublisherVersion())) {
            old.setPublisherVersion(updated.getPublisherVersion());
        }
        if (updated.getSecurityStrategy() != null && !Objects.equals(updated.getSecurityStrategy(), old.getSecurityStrategy())) {
            old.setSecurityStrategy(updated.getSecurityStrategy());
        }
        if (updated.getWebhookUrl() != null && !Objects.equals(updated.getWebhookUrl(), old.getWebhookUrl())) {
            old.setWebhookUrl(updated.getWebhookUrl());
        }
        if (updated.getCustomizedHeaders() != null && !Objects.equals(updated.getCustomizedHeaders(), old.getCustomizedHeaders())) {
            old.setCustomizedHeaders(updated.getCustomizedHeaders());
        }
        if (updated.getTrustedIps() != null && !Objects.equals(updated.getTrustedIps(), old.getTrustedIps())) {
            old.setTrustedIps(updated.getTrustedIps());
        }
        if (updated.getCreatedBy() != null && !Objects.equals(updated.getCreatedBy(), old.getCreatedBy())) {
            old.setCreatedBy(updated.getCreatedBy());
        }
        if (updated.getCreationTime() != null && !Objects.equals(updated.getCreationTime(), old.getCreationTime())) {
            old.setCreationTime(updated.getCreationTime());
        }
    }

    @Override
    public Response updateWebhookById(String webhookId2, String subscriberId, String appName, String appTag, String description, String webhookSecret,
            String publisherId, String publisherVersion, String securityStrategy, String webhookUrl, String customizedHeaders, List<String> trustedIps,
            String webhookStatus, String createdBy, Date creationTime, String webhookId, SecurityContext securityContext) throws NotFoundException {
        InMemoryWebhook webhook = new InMemoryWebhook();
        webhook.setWebhookId(webhookId);
        webhook.setSubscriberId(subscriberId);
        webhook.setAppName(appName);
        webhook.setAppTag(appTag);
        webhook.setDescription(description);
        webhook.setPublisherId(publisherId);
        webhook.setPublisherVersion(publisherVersion);
        if (securityStrategy != null) {
            webhook.setSecurityStrategy(SecurityStrategy.valueOf(securityStrategy));
        }
        webhook.setWebhookUrl(webhookUrl);
        if (customizedHeaders != null) {
            webhook.setCustomizedHeaders(new MapStringConverter().convertToEntityAttribute(customizedHeaders));
        }
        if (trustedIps != null) {
            webhook.setTrustedIps(trustedIps.toArray(new String[] {}));
        }
        webhook.setCreatedBy(createdBy);
        webhook.setCreationTime(creationTime);
        return updateWebhookById(webhook, webhookId, securityContext);
    }
}
