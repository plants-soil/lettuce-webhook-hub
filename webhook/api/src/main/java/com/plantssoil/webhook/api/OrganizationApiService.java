package com.plantssoil.webhook.api;

import java.util.Date;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public interface OrganizationApiService {
    Response addOrganization(com.plantssoil.webhook.core.registry.InMemoryOrganization body, SecurityContext securityContext) throws NotFoundException;

    Response addOrganization(String organizationId, String organizationName, String email, String website, String logoLink, String organizationStatus,
            String createdBy, Date creationTime, SecurityContext securityContext) throws NotFoundException;

    Response findAllOrganizations(Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException;

    Response findOrganizationById(String organizationId, SecurityContext securityContext) throws NotFoundException;

    Response updateOrganizationById(com.plantssoil.webhook.core.registry.InMemoryOrganization body, String organizationId, SecurityContext securityContext)
            throws NotFoundException;

    Response updateOrganizationById(String organizationId2, String organizationName, String email, String website, String logoLink, String organizationStatus,
            String createdBy, Date creationTime, String organizationId, SecurityContext securityContext) throws NotFoundException;
}
