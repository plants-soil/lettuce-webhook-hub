package com.plantssoil.webhook.api;


import com.plantssoil.webhook.api.EngineApiService;

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
@Path("/engine")


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-08T22:55:59.560416600+08:00[Asia/Shanghai]")
public class EngineApi  {

    @Inject EngineApiService service;

    @GET
    @Path("/allPublisherWebhookLogs")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find all webhook logs which triggered by specific publisher with pagination", description = "Will find the webhook logs on the page specified (page, pageSize)", security = {
        @SecurityRequirement(name = "webhooklog_auth", scopes = {
            "write:webhooklogs",
"read:webhooklogs"
        })
    }, tags={ "engine" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.logging.InMemoryWebhookLog.class)))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findAllPublisherWebhookLogs( @NotNull  @QueryParam("publisherId") String publisherId, @NotNull  @QueryParam("page") Integer page, @NotNull  @QueryParam("pageSize") Integer pageSize,  @QueryParam("dataGroup") String dataGroup,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findAllPublisherWebhookLogs(publisherId,page,pageSize,dataGroup,securityContext);
    }
    @GET
    @Path("/allSubscriberWebhookLogs")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find all webhook logs which dispatched to specific subscriber with pagination", description = "Will find the webhook logs on the page specified (page, pageSize)", security = {
        @SecurityRequirement(name = "webhooklog_auth", scopes = {
            "write:webhooklogs",
"read:webhooklogs"
        })
    }, tags={ "engine" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.logging.InMemoryWebhookLog.class)))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findAllSubscriberWebhookLogs( @NotNull  @QueryParam("webhookId") String webhookId, @NotNull  @QueryParam("page") Integer page, @NotNull  @QueryParam("pageSize") Integer pageSize,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findAllSubscriberWebhookLogs(webhookId,page,pageSize,securityContext);
    }
    @GET
    @Path("/webhookLines")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find all webhook log lines which belong to specific request & webhook", description = "Will find the webhook log lines", security = {
        @SecurityRequirement(name = "webhooklog_auth", scopes = {
            "write:webhooklogs",
"read:webhooklogs"
        })
    }, tags={ "engine" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.logging.InMemoryWebhookLogLine.class)))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findWebhookLogLines( @NotNull  @QueryParam("requestId") String requestId, @NotNull  @QueryParam("webhookId") String webhookId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findWebhookLogLines(requestId,webhookId,securityContext);
    }
    @GET
    @Path("/version")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Get the webhook engine version", description = "Get the webhook engine version", security = {
        @SecurityRequirement(name = "engine_auth", scopes = {
            "write:engine",
"read:engine"
        })
    }, tags={ "engine" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response getEngineVersion(@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.getEngineVersion(securityContext);
    }
    @POST
    @Path("/trigger")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Trigger a webhook event and post a message", description = "Trigger a webhook event and post a message", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "engine_auth", scopes = {
            "write:engine",
"read:engine"
        })
    }, tags={ "engine" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response trigger(
@Parameter(description = "Trigger a webhook event and post a message" ,required=true) com.plantssoil.webhook.core.Message body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.trigger(body,securityContext);
    }
    @POST
    @Path("/trigger")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Trigger a webhook event and post a message", description = "Trigger a webhook event and post a message", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "engine_auth", scopes = {
            "write:engine",
"read:engine"
        })
    }, tags={ "engine" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response trigger(@Parameter(description = "", required=true)@FormParam("publisherId")  String publisherId,@Parameter(description = "", required=true)@FormParam("version")  String version,@Parameter(description = "", required=true)@FormParam("eventType")  String eventType,@Parameter(description = "", required=true)@FormParam("eventTag")  String eventTag,@Parameter(description = "", required=true)@FormParam("contentType")  String contentType,@Parameter(description = "", required=true)@FormParam("charset")  String charset,@Parameter(description = "", required=true)@FormParam("dataGroup")  String dataGroup,@Parameter(description = "", required=true)@FormParam("requestId")  String requestId,@Parameter(description = "", required=true)@FormParam("payload")  String payload,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.trigger(publisherId,version,eventType,eventTag,contentType,charset,dataGroup,requestId,payload,securityContext);
    }
}
