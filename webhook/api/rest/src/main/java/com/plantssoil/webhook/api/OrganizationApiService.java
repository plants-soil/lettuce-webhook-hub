package com.plantssoil.webhook.api;

import com.plantssoil.webhook.api.*;
import com.plantssoil.webhook.beans.*;

import java.util.Date;
import com.plantssoil.webhook.beans.InlineResponse200;
import com.plantssoil.webhook.beans.InlineResponse2001;

import java.util.List;
import java.util.Map;
import com.plantssoil.webhook.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-09T17:41:01.999402200+08:00[Asia/Shanghai]")
public interface OrganizationApiService {
      Response addOrganization(com.plantssoil.webhook.core.registry.InMemoryOrganization body,SecurityContext securityContext) throws NotFoundException;
      Response addOrganization(String organizationId,String organizationName,String email,String website,String logoLink,String organizationStatus,String createdBy,Date creationTime,SecurityContext securityContext) throws NotFoundException;
      Response findAllOrganizations(Integer page,Integer pageSize,SecurityContext securityContext) throws NotFoundException;
      Response findOrganizationById(String organizationId,SecurityContext securityContext) throws NotFoundException;
      Response updateOrganizationById(com.plantssoil.webhook.core.registry.InMemoryOrganization body,String organizationId,SecurityContext securityContext) throws NotFoundException;
      Response updateOrganizationById(String organizationId2,String organizationName,String email,String website,String logoLink,String organizationStatus,String createdBy,Date creationTime,String organizationId,SecurityContext securityContext) throws NotFoundException;
}
