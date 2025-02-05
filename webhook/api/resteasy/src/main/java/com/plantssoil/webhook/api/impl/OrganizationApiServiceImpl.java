package com.plantssoil.webhook.api.impl;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.plantssoil.webhook.api.ApiResponseMessage;
import com.plantssoil.webhook.api.NotFoundException;
import com.plantssoil.webhook.api.OrganizationApiService;

@RequestScoped
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-05T11:58:30.837020300+08:00[Asia/Shanghai]")
public class OrganizationApiServiceImpl implements OrganizationApiService {
    @Override
    public Response addOrganization(com.plantssoil.webhook.core.registry.InMemoryOrganization body, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response addOrganization(String organizationId, String organizationName, String email, String website, String logoLink, String organizationStatus,
            String createdBy, Date creationTime, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response findAllOrganizations(Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response findOrganizationById(String organizationId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response updateOrganization(com.plantssoil.webhook.core.registry.InMemoryOrganization body, SecurityContext securityContext)
            throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response updateOrganization(String organizationId, String organizationName, String email, String website, String logoLink, String organizationStatus,
            String createdBy, Date creationTime, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response updateOrganizationById(com.plantssoil.webhook.core.registry.InMemoryOrganization body, String organizationId,
            SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response updateOrganizationById(String organizationId2, String organizationName, String email, String website, String logoLink,
            String organizationStatus, String createdBy, Date creationTime, String organizationId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
