package com.plantssoil.webhook.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public interface SubscriberApiService {
    Response addSubscriber(com.plantssoil.webhook.core.registry.InMemorySubscriber body, SecurityContext securityContext) throws NotFoundException;

    Response addSubscriber(String subscriberId, String organizationId, SecurityContext securityContext) throws NotFoundException;

    Response deleteSubscriber(String subscriberId, String apiKey, SecurityContext securityContext) throws NotFoundException;

    Response findAllSubscribers(Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException;

    Response findSubscriberById(String subscriberId, SecurityContext securityContext) throws NotFoundException;

    Response updateSubscriberById(com.plantssoil.webhook.core.registry.InMemorySubscriber body, String subscriberId, SecurityContext securityContext)
            throws NotFoundException;

    Response updateSubscriberById(String subscriberId2, String organizationId, String subscriberId, SecurityContext securityContext) throws NotFoundException;
}
