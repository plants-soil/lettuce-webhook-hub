package com.plantssoil.webhook.api;


import com.plantssoil.webhook.api.OrganizationApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.Date;

import java.util.Map;
import java.util.List;
import com.plantssoil.webhook.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.inject.Inject;

import javax.validation.constraints.*;
@Path("/organization")


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-08T22:55:59.560416600+08:00[Asia/Shanghai]")
public class OrganizationApi  {

    @Inject OrganizationApiService service;

    @POST
    
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add a new organization", description = "Add a new organization", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "organization_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "organization" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryOrganization.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addOrganization(
@Parameter(description = "Create a new organization" ,required=true) com.plantssoil.webhook.core.registry.InMemoryOrganization body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addOrganization(body,securityContext);
    }
    @POST
    
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add a new organization", description = "Add a new organization", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "organization_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "organization" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryOrganization.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addOrganization(@Parameter(description = "", required=true)@FormParam("organizationId")  String organizationId,@Parameter(description = "", required=true)@FormParam("organizationName")  String organizationName,@Parameter(description = "", required=true)@FormParam("email")  String email,@Parameter(description = "", required=true)@FormParam("website")  String website,@Parameter(description = "", required=true)@FormParam("logoLink")  String logoLink,@Parameter(description = "", required=true, schema=@Schema(allowableValues={ "ACTIVE", "INACTIVE", "LOCKED" })
)@FormParam("organizationStatus")  String organizationStatus,@Parameter(description = "", required=true)@FormParam("createdBy")  String createdBy,@Parameter(description = "", required=true)@FormParam("creationTime")  Date creationTime,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addOrganization(organizationId,organizationName,email,website,logoLink,organizationStatus,createdBy,creationTime,securityContext);
    }
    @GET
    @Path("/all")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find all organizations with pagination", description = "Will find the organizations on the page specified (page, pageSize)", security = {
        @SecurityRequirement(name = "organization_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "organization" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryOrganization.class)))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findAllOrganizations( @NotNull  @QueryParam("page") Integer page, @NotNull  @QueryParam("pageSize") Integer pageSize,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findAllOrganizations(page,pageSize,securityContext);
    }
    @GET
    @Path("/{organizationId}")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find organization by ID", description = "Returns a single organization", security = {
        @SecurityRequirement(name = "organization_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "organization" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryOrganization.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Organization not found") })
    public Response findOrganizationById( @PathParam("organizationId") String organizationId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findOrganizationById(organizationId,securityContext);
    }
    @PUT
    
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Update an existing organization", description = "Update an existing organization by organizationId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "organization_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "organization" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryOrganization.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response updateOrganization(
@Parameter(description = "Update an existent organization" ,required=true) com.plantssoil.webhook.core.registry.InMemoryOrganization body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.updateOrganization(body,securityContext);
    }
    @PUT
    
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Update an existing organization", description = "Update an existing organization by organizationId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "organization_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "organization" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryOrganization.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response updateOrganization(@Parameter(description = "", required=true)@FormParam("organizationId")  String organizationId,@Parameter(description = "", required=true)@FormParam("organizationName")  String organizationName,@Parameter(description = "", required=true)@FormParam("email")  String email,@Parameter(description = "", required=true)@FormParam("website")  String website,@Parameter(description = "", required=true)@FormParam("logoLink")  String logoLink,@Parameter(description = "", required=true, schema=@Schema(allowableValues={ "ACTIVE", "INACTIVE", "LOCKED" })
)@FormParam("organizationStatus")  String organizationStatus,@Parameter(description = "", required=true)@FormParam("createdBy")  String createdBy,@Parameter(description = "", required=true)@FormParam("creationTime")  Date creationTime,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.updateOrganization(organizationId,organizationName,email,website,logoLink,organizationStatus,createdBy,creationTime,securityContext);
    }
    @PUT
    @Path("/{organizationId}")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Update an existing organization", description = "Update an existing organization by organizationId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "organization_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "organization" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryOrganization.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response updateOrganizationById(
@Parameter(description = "Update an existent organization" ,required=true) com.plantssoil.webhook.core.registry.InMemoryOrganization body
, @PathParam("organizationId") String organizationId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.updateOrganizationById(body,organizationId,securityContext);
    }
    @PUT
    @Path("/{organizationId}")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Update an existing organization", description = "Update an existing organization by organizationId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "organization_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "organization" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryOrganization.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response updateOrganizationById(@Parameter(description = "", required=true)@FormParam("organizationId")  String organizationId2,@Parameter(description = "", required=true)@FormParam("organizationName")  String organizationName,@Parameter(description = "", required=true)@FormParam("email")  String email,@Parameter(description = "", required=true)@FormParam("website")  String website,@Parameter(description = "", required=true)@FormParam("logoLink")  String logoLink,@Parameter(description = "", required=true, schema=@Schema(allowableValues={ "ACTIVE", "INACTIVE", "LOCKED" })
)@FormParam("organizationStatus")  String organizationStatus,@Parameter(description = "", required=true)@FormParam("createdBy")  String createdBy,@Parameter(description = "", required=true)@FormParam("creationTime")  Date creationTime, @PathParam("organizationId") String organizationId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.updateOrganizationById(organizationId2,organizationName,email,website,logoLink,organizationStatus,createdBy,creationTime,organizationId,securityContext);
    }
}
