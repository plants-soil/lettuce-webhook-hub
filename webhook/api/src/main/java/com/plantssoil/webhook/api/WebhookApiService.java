package com.plantssoil.webhook.api;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public interface WebhookApiService {
    Response activateWebhook(String webhookId, SecurityContext securityContext) throws NotFoundException;

    Response addWebhook(com.plantssoil.webhook.core.registry.InMemoryWebhook body, SecurityContext securityContext) throws NotFoundException;

    Response addWebhook(String webhookId, String subscriberId, String appName, String appTag, String description, String webhookSecret, String publisherId,
            String publisherVersion, String securityStrategy, String webhookUrl, String customizedHeaders, List<String> trustedIps, String webhookStatus,
            String createdBy, Date creationTime, SecurityContext securityContext) throws NotFoundException;

    Response deactivateWebhook(String webhookId, SecurityContext securityContext) throws NotFoundException;

    Response findAllWebhooks(String subscriberId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException;

    Response findSubscribedDataGroup(String webhookId, String dataGroup, SecurityContext securityContext) throws NotFoundException;

    Response findSubscribedDataGroups(String webhookId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException;

    Response findSubscribedEvents(String webhookId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException;

    Response findWebhookById(String webhookId, SecurityContext securityContext) throws NotFoundException;

    Response subscribeDataGroup(String webhookId, String dataGroupId, SecurityContext securityContext) throws NotFoundException;

    Response subscribeDataGroups(String webhookId, List<String> dataGroupIds, SecurityContext securityContext) throws NotFoundException;

    Response subscribeEvent(String webhookId, String eventId, SecurityContext securityContext) throws NotFoundException;

    Response subscribeEvents(String webhookId, List<String> eventIds, SecurityContext securityContext) throws NotFoundException;

    Response unsubscribeDataGroup(String webhookId, String dataGroupId, SecurityContext securityContext) throws NotFoundException;

    Response unsubscribeDataGroups(String webhookId, List<String> dataGroupIds, SecurityContext securityContext) throws NotFoundException;

    Response unsubscribeEvent(String webhookId, String eventId, SecurityContext securityContext) throws NotFoundException;

    Response unsubscribeEvents(String webhookId, List<String> eventIds, SecurityContext securityContext) throws NotFoundException;

    Response updateWebhookById(com.plantssoil.webhook.core.registry.InMemoryWebhook body, String webhookId, SecurityContext securityContext)
            throws NotFoundException;

    Response updateWebhookById(String webhookId2, String subscriberId, String appName, String appTag, String description, String webhookSecret,
            String publisherId, String publisherVersion, String securityStrategy, String webhookUrl, String customizedHeaders, List<String> trustedIps,
            String webhookStatus, String createdBy, Date creationTime, String webhookId, SecurityContext securityContext) throws NotFoundException;
}
