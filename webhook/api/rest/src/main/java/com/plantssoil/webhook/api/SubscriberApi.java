package com.plantssoil.webhook.api;

import com.plantssoil.webhook.beans.*;
import com.plantssoil.webhook.api.SubscriberApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


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
@Path("/subscriber")


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-05T11:58:30.837020300+08:00[Asia/Shanghai]")
public class SubscriberApi  {

    @Inject SubscriberApiService service;

    @POST
    
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add a new subscriber", description = "Add a new subscriber", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "subscriber" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemorySubscriber.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addSubscriber(
@Parameter(description = "Create a new subscriber" ,required=true) com.plantssoil.webhook.core.registry.InMemorySubscriber body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addSubscriber(body,securityContext);
    }
    @POST
    
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add a new subscriber", description = "Add a new subscriber", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "subscriber" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemorySubscriber.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addSubscriber(@Parameter(description = "", required=true)@FormParam("subscriberId")  String subscriberId,@Parameter(description = "", required=true)@FormParam("organizationId")  String organizationId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addSubscriber(subscriberId,organizationId,securityContext);
    }
    @DELETE
    @Path("/{subscriberId}")
    
    
    @Operation(summary = "Deletes a subscriber", description = "delete a subscriber", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "subscriber" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "400", description = "Invalid subscriber value") })
    public Response deleteSubscriber( @PathParam("subscriberId") String subscriberId,@Parameter(description = "" )@HeaderParam("api_key") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.deleteSubscriber(subscriberId,apiKey,securityContext);
    }
    @GET
    @Path("/all")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find all subscribers with pagination", description = "Will find the subscribers on the page specified (page, pageSize)", security = {
        @SecurityRequirement(name = "webhook_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "subscriber" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemorySubscriber.class)))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findAllSubscribers( @NotNull  @QueryParam("page") Integer page, @NotNull  @QueryParam("pageSize") Integer pageSize,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findAllSubscribers(page,pageSize,securityContext);
    }
    @GET
    @Path("/{subscriberId}")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find subscriber by ID", description = "Returns a single subscriber", security = {
        @SecurityRequirement(name = "webhook_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "subscriber" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemorySubscriber.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Subscriber not found") })
    public Response findSubscriberById( @PathParam("subscriberId") String subscriberId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findSubscriberById(subscriberId,securityContext);
    }
    @PUT
    @Path("/{subscriberId}")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Update an existing subscriber", description = "Update an existing subscriber by subscriberId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "subscriber" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemorySubscriber.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Subscriber not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response updateSubscriberById(
@Parameter(description = "Update an existent subscriber" ,required=true) com.plantssoil.webhook.core.registry.InMemorySubscriber body
, @PathParam("subscriberId") String subscriberId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.updateSubscriberById(body,subscriberId,securityContext);
    }
    @PUT
    @Path("/{subscriberId}")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Update an existing subscriber", description = "Update an existing subscriber by subscriberId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "subscriber" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemorySubscriber.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Subscriber not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response updateSubscriberById(@Parameter(description = "", required=true)@FormParam("subscriberId")  String subscriberId2,@Parameter(description = "", required=true)@FormParam("organizationId")  String organizationId, @PathParam("subscriberId") String subscriberId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.updateSubscriberById(subscriberId2,organizationId,subscriberId,securityContext);
    }
}
