package com.plantssoil.webhook.api;


import com.plantssoil.webhook.api.WebhookApiService;

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
@Path("/webhook")


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-08T22:55:59.560416600+08:00[Asia/Shanghai]")
public class WebhookApi  {

    @Inject WebhookApiService service;

    @POST
    @Path("/activate")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Activate webhook", description = "Activate webhook and change webhook status to production", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryWebhook.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Webhook not found") })
    public Response activateWebhook( @NotNull  @QueryParam("webhookId") String webhookId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.activateWebhook(webhookId,securityContext);
    }
    @POST
    
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add a new webhook", description = "Add a new webhook", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryWebhook.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addWebhook(
@Parameter(description = "Create a new webhook" ,required=true) com.plantssoil.webhook.core.registry.InMemoryWebhook body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addWebhook(body,securityContext);
    }
    @POST
    
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add a new webhook", description = "Add a new webhook", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryWebhook.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addWebhook(@Parameter(description = "", required=true)@FormParam("webhookId")  String webhookId,@Parameter(description = "", required=true)@FormParam("subscriberId")  String subscriberId,@Parameter(description = "", required=true)@FormParam("appName")  String appName,@Parameter(description = "", required=true)@FormParam("appTag")  String appTag,@Parameter(description = "", required=true)@FormParam("description")  String description,@Parameter(description = "", required=true)@FormParam("webhookSecret")  String webhookSecret,@Parameter(description = "", required=true)@FormParam("publisherId")  String publisherId,@Parameter(description = "", required=true)@FormParam("publisherVersion")  String publisherVersion,@Parameter(description = "", required=true, schema=@Schema(allowableValues={ "SIGNATURE", "TOKEN", "NONE" })
)@FormParam("securityStrategy")  String securityStrategy,@Parameter(description = "", required=true)@FormParam("webhookUrl")  String webhookUrl,@Parameter(description = "", required=true)@FormParam("customizedHeaders")  String customizedHeaders,@Parameter(description = "", required=true)@FormParam("trustedIps")  List<String> trustedIps,@Parameter(description = "", required=true, schema=@Schema(allowableValues={ "TEST", "AWAITING_FOR_APPROVEL", "PRODUCTION", "INACTIVE" })
)@FormParam("webhookStatus")  String webhookStatus,@Parameter(description = "", required=true)@FormParam("createdBy")  String createdBy,@Parameter(description = "", required=true)@FormParam("creationTime")  Date creationTime,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addWebhook(webhookId,subscriberId,appName,appTag,description,webhookSecret,publisherId,publisherVersion,securityStrategy,webhookUrl,customizedHeaders,trustedIps,webhookStatus,createdBy,creationTime,securityContext);
    }
    @POST
    @Path("/deactivate")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Deactivate webhook", description = "Deactivate webhook and change webhook status to inactive", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryWebhook.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Webhook not found") })
    public Response deactivateWebhook( @NotNull  @QueryParam("webhookId") String webhookId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.deactivateWebhook(webhookId,securityContext);
    }
    @GET
    @Path("/all")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find all webhooks which belong to specific subscriber with pagination", description = "Will find the webhooks on the page specified (page, pageSize)", security = {
        @SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryWebhook.class)))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findAllWebhooks( @NotNull  @QueryParam("subscriberId") String subscriberId, @NotNull  @QueryParam("page") Integer page, @NotNull  @QueryParam("pageSize") Integer pageSize,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findAllWebhooks(subscriberId,page,pageSize,securityContext);
    }
    @GET
    @Path("/subscribedDataGroup")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find the subscribed data group which belong to specific webhook with data group name", description = "Will find the subscribed data group which belong to specific webhook with data group name", security = {
        @SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryDataGroup.class))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findSubscribedDataGroup( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("dataGroup") String dataGroup,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findSubscribedDataGroup(webhookId,dataGroup,securityContext);
    }
    @GET
    @Path("/allSubscribedDataGroups")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find all subscribed data groups which belong to specific webhook with pagination", description = "Will find the subscribed data groups on the page specified (page, pageSize)", security = {
        @SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryDataGroup.class)))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findSubscribedDataGroups( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("page") Integer page, @NotNull  @QueryParam("pageSize") Integer pageSize,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findSubscribedDataGroups(webhookId,page,pageSize,securityContext);
    }
    @GET
    @Path("/allSubscribedEvents")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find all subscribed events which belong to specific webhook with pagination", description = "Will find the subscribed events on the page specified (page, pageSize)", security = {
        @SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryEvent.class)))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findSubscribedEvents( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("page") Integer page, @NotNull  @QueryParam("pageSize") Integer pageSize,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findSubscribedEvents(webhookId,page,pageSize,securityContext);
    }
    @GET
    @Path("/{webhookId}")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find webhook by ID", description = "Returns a single webhook", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryWebhook.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Webhook not found") })
    public Response findWebhookById( @PathParam("webhookId") String webhookId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findWebhookById(webhookId,securityContext);
    }
    @POST
    @Path("/subscribeDataGroup")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Subscribe a publisher data group for webhook", description = "Subscribe a publisher data group for webhook", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Webhook or data group not found") })
    public Response subscribeDataGroup( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("dataGroupId") String dataGroupId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.subscribeDataGroup(webhookId,dataGroupId,securityContext);
    }
    @POST
    @Path("/subscribeDataGroups")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Subscribe publisher data groups for webhook", description = "Subscribe publisher data groups for webhook", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Webhook or data group not found") })
    public Response subscribeDataGroups( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("dataGroupIds") List<String> dataGroupIds,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.subscribeDataGroups(webhookId,dataGroupIds,securityContext);
    }
    @POST
    @Path("/subscribeEvent")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Subscribe a publisher event for webhook", description = "Subscribe a publisher event for webhook", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Webhook or event not found") })
    public Response subscribeEvent( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("eventId") String eventId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.subscribeEvent(webhookId,eventId,securityContext);
    }
    @POST
    @Path("/subscribeEvents")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Subscribe publisher events for webhook", description = "Subscribe publisher events for webhook", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Webhook or event not found") })
    public Response subscribeEvents( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("eventIds") List<String> eventIds,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.subscribeEvents(webhookId,eventIds,securityContext);
    }
    @POST
    @Path("/unsubscribeDataGroup")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Unsubscribe a publisher data group for webhook", description = "Unsubscribe a publisher data group for webhook", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Webhook or data group not found") })
    public Response unsubscribeDataGroup( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("dataGroupId") String dataGroupId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.unsubscribeDataGroup(webhookId,dataGroupId,securityContext);
    }
    @POST
    @Path("/unsubscribeDataGroups")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Unsubscribe publisher events for webhook", description = "Unsubscribe publisher events for webhook", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Webhook or data group not found") })
    public Response unsubscribeDataGroups( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("dataGroupIds") List<String> dataGroupIds,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.unsubscribeDataGroups(webhookId,dataGroupIds,securityContext);
    }
    @POST
    @Path("/unsubscribeEvent")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Unsubscribe a publisher event for webhook", description = "Unsubscribe a publisher event for webhook", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Webhook or event not found") })
    public Response unsubscribeEvent( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("eventId") String eventId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.unsubscribeEvent(webhookId,eventId,securityContext);
    }
    @POST
    @Path("/unsubscribeEvents")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Unsubscribe publisher events for webhook", description = "Unsubscribe publisher events for webhook", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Webhook or event not found") })
    public Response unsubscribeEvents( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("eventIds") List<String> eventIds,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.unsubscribeEvents(webhookId,eventIds,securityContext);
    }
    @PUT
    @Path("/{webhookId}")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Update an existing webhook", description = "Update an existing webhook by webhookId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryWebhook.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Webhook not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response updateWebhookById(
@Parameter(description = "Update an existent webhook" ,required=true) com.plantssoil.webhook.core.registry.InMemoryWebhook body
, @PathParam("webhookId") String webhookId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.updateWebhookById(body,webhookId,securityContext);
    }
    @PUT
    @Path("/{webhookId}")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Update an existing webhook", description = "Update an existing webhook by webhookId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:webhooks",
"read:webhooks"
        })
    }, tags={ "webhook" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryWebhook.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Webhook not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response updateWebhookById(@Parameter(description = "", required=true)@FormParam("webhookId")  String webhookId2,@Parameter(description = "", required=true)@FormParam("subscriberId")  String subscriberId,@Parameter(description = "", required=true)@FormParam("appName")  String appName,@Parameter(description = "", required=true)@FormParam("appTag")  String appTag,@Parameter(description = "", required=true)@FormParam("description")  String description,@Parameter(description = "", required=true)@FormParam("webhookSecret")  String webhookSecret,@Parameter(description = "", required=true)@FormParam("publisherId")  String publisherId,@Parameter(description = "", required=true)@FormParam("publisherVersion")  String publisherVersion,@Parameter(description = "", required=true, schema=@Schema(allowableValues={ "SIGNATURE", "TOKEN", "NONE" })
)@FormParam("securityStrategy")  String securityStrategy,@Parameter(description = "", required=true)@FormParam("webhookUrl")  String webhookUrl,@Parameter(description = "", required=true)@FormParam("customizedHeaders")  String customizedHeaders,@Parameter(description = "", required=true)@FormParam("trustedIps")  List<String> trustedIps,@Parameter(description = "", required=true, schema=@Schema(allowableValues={ "TEST", "AWAITING_FOR_APPROVEL", "PRODUCTION", "INACTIVE" })
)@FormParam("webhookStatus")  String webhookStatus,@Parameter(description = "", required=true)@FormParam("createdBy")  String createdBy,@Parameter(description = "", required=true)@FormParam("creationTime")  Date creationTime, @PathParam("webhookId") String webhookId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.updateWebhookById(webhookId2,subscriberId,appName,appTag,description,webhookSecret,publisherId,publisherVersion,securityStrategy,webhookUrl,customizedHeaders,trustedIps,webhookStatus,createdBy,creationTime,webhookId,securityContext);
    }
}
