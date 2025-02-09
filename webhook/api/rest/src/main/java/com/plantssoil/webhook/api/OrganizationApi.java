package com.plantssoil.webhook.api;

import java.util.Date;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.plantssoil.webhook.beans.InlineResponse200;
import com.plantssoil.webhook.beans.InlineResponse2001;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Path("/api/v1/organization")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-09T17:41:01.999402200+08:00[Asia/Shanghai]")
public class OrganizationApi {

    @Inject
    OrganizationApiService service;

    @POST

    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Add a new organization", description = "Add a new organization", security = { @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "organization_auth", scopes = { "write:organizations", "read:organizations" }) }, tags = { "organization" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the posted organization object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse200.class))),

            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),

            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),

            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response addOrganization(
            @Parameter(description = "Create a new organization", required = true) com.plantssoil.webhook.core.registry.InMemoryOrganization body,
            @Context SecurityContext securityContext) throws NotFoundException {
        return service.addOrganization(body, securityContext);
    }

    @POST

    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Add a new organization", description = "Add a new organization", security = { @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "organization_auth", scopes = { "write:organizations", "read:organizations" }) }, tags = { "organization" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the posted organization object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse200.class))),

            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),

            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),

            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response addOrganization(@Parameter(description = "", required = true) @FormParam("organizationId") String organizationId,
            @Parameter(description = "", required = true) @FormParam("organizationName") String organizationName,
            @Parameter(description = "", required = true) @FormParam("email") String email,
            @Parameter(description = "", required = true) @FormParam("website") String website,
            @Parameter(description = "", required = true) @FormParam("logoLink") String logoLink,
            @Parameter(description = "", required = true, schema = @Schema(allowableValues = { "ACTIVE", "INACTIVE",
                    "LOCKED" })) @FormParam("organizationStatus") String organizationStatus,
            @Parameter(description = "", required = true) @FormParam("createdBy") String createdBy,
            @Parameter(description = "", required = true) @FormParam("creationTime") Date creationTime, @Context SecurityContext securityContext)
            throws NotFoundException {
        return service.addOrganization(organizationId, organizationName, email, website, logoLink, organizationStatus, createdBy, creationTime,
                securityContext);
    }

    @GET
    @Path("/all")

    @Produces({ "application/json" })
    @Operation(summary = "Find all organizations with pagination", description = "Will find the organizations on the page specified (page, pageSize)", security = {
            @SecurityRequirement(name = "organization_auth", scopes = { "write:organizations", "read:organizations" }) }, tags = { "organization" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contain organization object array in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2001.class))),

            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),

            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),

            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findAllOrganizations(@NotNull @QueryParam("page") Integer page, @NotNull @QueryParam("pageSize") Integer pageSize,
            @Context SecurityContext securityContext) throws NotFoundException {
        return service.findAllOrganizations(page, pageSize, securityContext);
    }

    @GET
    @Path("/{organizationId}")

    @Produces({ "application/json" })
    @Operation(summary = "Find organization by ID", description = "Returns a single organization", security = {
            @SecurityRequirement(name = "organization_auth", scopes = { "write:organizations", "read:organizations" }) }, tags = { "organization" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the organization object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse200.class))),

            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),

            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),

            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findOrganizationById(@PathParam("organizationId") String organizationId, @Context SecurityContext securityContext)
            throws NotFoundException {
        return service.findOrganizationById(organizationId, securityContext);
    }

    @PUT
    @Path("/{organizationId}")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Update an existing organization", description = "Update an existing organization by organizationId", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "organization_auth", scopes = { "write:organizations", "read:organizations" }) }, tags = { "organization" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the updated organization object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse200.class))),

            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),

            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),

            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response updateOrganizationById(
            @Parameter(description = "Update an existent organization", required = true) com.plantssoil.webhook.core.registry.InMemoryOrganization body,
            @PathParam("organizationId") String organizationId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.updateOrganizationById(body, organizationId, securityContext);
    }

    @PUT
    @Path("/{organizationId}")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Update an existing organization", description = "Update an existing organization by organizationId", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "organization_auth", scopes = { "write:organizations", "read:organizations" }) }, tags = { "organization" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the updated organization object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse200.class))),

            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),

            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),

            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response updateOrganizationById(@Parameter(description = "", required = true) @FormParam("organizationId") String organizationId2,
            @Parameter(description = "", required = true) @FormParam("organizationName") String organizationName,
            @Parameter(description = "", required = true) @FormParam("email") String email,
            @Parameter(description = "", required = true) @FormParam("website") String website,
            @Parameter(description = "", required = true) @FormParam("logoLink") String logoLink,
            @Parameter(description = "", required = true, schema = @Schema(allowableValues = { "ACTIVE", "INACTIVE",
                    "LOCKED" })) @FormParam("organizationStatus") String organizationStatus,
            @Parameter(description = "", required = true) @FormParam("createdBy") String createdBy,
            @Parameter(description = "", required = true) @FormParam("creationTime") Date creationTime, @PathParam("organizationId") String organizationId,
            @Context SecurityContext securityContext) throws NotFoundException {
        return service.updateOrganizationById(organizationId2, organizationName, email, website, logoLink, organizationStatus, createdBy, creationTime,
                organizationId, securityContext);
    }
}
