package com.plantssoil.webhook.api;

import com.plantssoil.webhook.beans.*;
import com.plantssoil.webhook.api.EngineApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.plantssoil.webhook.beans.ModelApiResponse;

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


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-05T11:58:30.837020300+08:00[Asia/Shanghai]")
public class EngineApi  {

    @Inject EngineApiService service;

    @GET
    @Path("/version")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Get the webhook engine version", description = "Get the webhook engine version", security = {
        @SecurityRequirement(name = "webhook_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "engine" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ModelApiResponse.class))),
        
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
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "engine" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ModelApiResponse.class))),
        
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
@SecurityRequirement(name = "webhook_auth", scopes = {
            "write:organizations",
"read:organizations"
        })
    }, tags={ "engine" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ModelApiResponse.class))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response trigger(@Parameter(description = "", required=true)@FormParam("publisherId")  String publisherId,@Parameter(description = "", required=true)@FormParam("version")  String version,@Parameter(description = "", required=true)@FormParam("eventType")  String eventType,@Parameter(description = "", required=true)@FormParam("eventTag")  String eventTag,@Parameter(description = "", required=true)@FormParam("contentType")  String contentType,@Parameter(description = "", required=true)@FormParam("charset")  String charset,@Parameter(description = "", required=true)@FormParam("dataGroup")  String dataGroup,@Parameter(description = "", required=true)@FormParam("requestId")  String requestId,@Parameter(description = "", required=true)@FormParam("payload")  String payload,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.trigger(publisherId,version,eventType,eventTag,contentType,charset,dataGroup,requestId,payload,securityContext);
    }
}
