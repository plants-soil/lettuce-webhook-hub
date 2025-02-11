package com.plantssoil.webhook.api;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-09T17:41:01.999402200+08:00[Asia/Shanghai]")
public interface PublisherApiService {
    Response addDataGroup(com.plantssoil.webhook.core.registry.InMemoryDataGroup body, String publisherId, SecurityContext securityContext)
            throws NotFoundException;

    Response addDataGroup(String dataGroupId, String publisherId2, String dataGroup, String publisherId, SecurityContext securityContext)
            throws NotFoundException;

    Response addDataGroups(List<com.plantssoil.webhook.core.registry.InMemoryDataGroup> body, String publisherId, SecurityContext securityContext)
            throws NotFoundException;

    Response addEvent(com.plantssoil.webhook.core.registry.InMemoryEvent body, String publisherId, SecurityContext securityContext) throws NotFoundException;

    Response addEvent(String eventId, String publisherId2, String eventType, String eventTag, String contentType, String charset, String eventStatus,
            String publisherId, SecurityContext securityContext) throws NotFoundException;

    Response addEvents(List<com.plantssoil.webhook.core.registry.InMemoryEvent> body, String publisherId, SecurityContext securityContext)
            throws NotFoundException;

    Response addPublisher(com.plantssoil.webhook.core.registry.InMemoryPublisher body, SecurityContext securityContext) throws NotFoundException;

    Response addPublisher(String publisherId, String organizationId, Boolean supportDataGroup, String version, String createdBy, Date creationTime,
            SecurityContext securityContext) throws NotFoundException;

    Response findAllPublishers(Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException;

    Response findDataGroupById(String dataGroupId, SecurityContext securityContext) throws NotFoundException;

    Response findDataGroupByName(String publisherId, String dataGroup, SecurityContext securityContext) throws NotFoundException;

    Response findDataGroups(String publisherId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException;

    Response findEventById(String eventId, SecurityContext securityContext) throws NotFoundException;

    Response findEvents(String publisherId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException;

    Response findPublisherById(String publisherId, SecurityContext securityContext) throws NotFoundException;

    Response updatePublisherById(com.plantssoil.webhook.core.registry.InMemoryPublisher body, String publisherId, SecurityContext securityContext)
            throws NotFoundException;

    Response updatePublisherById(String publisherId2, String organizationId, Boolean supportDataGroup, String version, String createdBy, Date creationTime,
            String publisherId, SecurityContext securityContext) throws NotFoundException;
}
