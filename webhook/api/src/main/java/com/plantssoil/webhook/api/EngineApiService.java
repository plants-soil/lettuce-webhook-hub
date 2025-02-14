package com.plantssoil.webhook.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public interface EngineApiService {
    Response findAllPublisherWebhookLogs(String publisherId, Integer page, Integer pageSize, String dataGroup, SecurityContext securityContext)
            throws NotFoundException;

    Response findAllSubscriberWebhookLogs(String webhookId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException;

    Response findWebhookLogLines(String requestId, String webhookId, SecurityContext securityContext) throws NotFoundException;

    Response getEngineVersion(SecurityContext securityContext) throws NotFoundException;

    Response trigger(com.plantssoil.webhook.core.Message body, SecurityContext securityContext) throws NotFoundException;

    Response trigger(String publisherId, String version, String eventType, String eventTag, String contentType, String charset, String dataGroup,
            String requestId, String payload, SecurityContext securityContext) throws NotFoundException;
}
