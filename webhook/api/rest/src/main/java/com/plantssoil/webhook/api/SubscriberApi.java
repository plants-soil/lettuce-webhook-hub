package com.plantssoil.webhook.api;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.plantssoil.webhook.beans.InlineResponse2008;
import com.plantssoil.webhook.beans.InlineResponse2009;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Path("/subscriber")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-09T17:41:01.999402200+08:00[Asia/Shanghai]")
public class SubscriberApi {

    @Inject
    SubscriberApiService service;

    @POST
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Add a new subscriber", description = "Add a new subscriber", security = { @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "subscriber_auth", scopes = { "write:subscribers", "read:subscribers" }) }, tags = { "subscriber" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the posted subscriber object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2008.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response addSubscriber(
            @Parameter(description = "Create a new subscriber", required = true) com.plantssoil.webhook.core.registry.InMemorySubscriber body,
            @Context SecurityContext securityContext) throws NotFoundException {
        return service.addSubscriber(body, securityContext);
    }

    @DELETE
    @Path("/{subscriberId}")
    @Produces({ "application/json" })
    @Operation(summary = "Deletes a subscriber", description = "delete a subscriber", security = { @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "subscriber_auth", scopes = { "write:subscribers", "read:subscribers" }) }, tags = { "subscriber" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the posted subscriber object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response deleteSubscriber(@PathParam("subscriberId") String subscriberId, @Parameter(description = "") @HeaderParam("api_key") String apiKey,
            @Context SecurityContext securityContext) throws NotFoundException {
        return service.deleteSubscriber(subscriberId, apiKey, securityContext);
    }

    @GET
    @Path("/all")
    @Produces({ "application/json" })
    @Operation(summary = "Find all subscribers with pagination", description = "Will find the subscribers on the page specified (page, pageSize)", security = {
            @SecurityRequirement(name = "subscriber_auth", scopes = { "write:subscribers", "read:subscribers" }) }, tags = { "subscriber" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contain subscriber array in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2009.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findAllSubscribers(@NotNull @QueryParam("page") Integer page, @NotNull @QueryParam("pageSize") Integer pageSize,
            @Context SecurityContext securityContext) throws NotFoundException {
        return service.findAllSubscribers(page, pageSize, securityContext);
    }

    @GET
    @Path("/{subscriberId}")
    @Produces({ "application/json" })
    @Operation(summary = "Find subscriber by ID", description = "Returns a single subscriber", security = {
            @SecurityRequirement(name = "subscriber_auth", scopes = { "write:subscribers", "read:subscribers" }) }, tags = { "subscriber" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the subscriber object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2008.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findSubscriberById(@PathParam("subscriberId") String subscriberId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.findSubscriberById(subscriberId, securityContext);
    }

    @PUT
    @Path("/{subscriberId}")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Update an existing subscriber", description = "Update an existing subscriber by subscriberId", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "subscriber_auth", scopes = { "write:subscribers", "read:subscribers" }) }, tags = { "subscriber" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the updated subscriber object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2008.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response updateSubscriberById(
            @Parameter(description = "Update an existent subscriber", required = true) com.plantssoil.webhook.core.registry.InMemorySubscriber body,
            @PathParam("subscriberId") String subscriberId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.updateSubscriberById(body, subscriberId, securityContext);
    }
}
