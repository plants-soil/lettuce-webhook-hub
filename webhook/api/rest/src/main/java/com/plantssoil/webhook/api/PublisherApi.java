package com.plantssoil.webhook.api;


import com.plantssoil.webhook.api.PublisherApiService;

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
@Path("/publisher")


@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-08T22:55:59.560416600+08:00[Asia/Shanghai]")
public class PublisherApi  {

    @Inject PublisherApiService service;

    @POST
    @Path("/{publisherId}/datagroup")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add a data group to an existing publisher", description = "add a data group to an existing publisher by publisherId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "datagroup_auth", scopes = {
            "write:datagroups",
"read:datagroups"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryDataGroup.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Publisher not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addDataGroup(
@Parameter(description = "add a data group to an existing publisher" ,required=true) com.plantssoil.webhook.core.registry.InMemoryDataGroup body
, @PathParam("publisherId") String publisherId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addDataGroup(body,publisherId,securityContext);
    }
    @POST
    @Path("/{publisherId}/datagroup")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add a data group to an existing publisher", description = "add a data group to an existing publisher by publisherId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "datagroup_auth", scopes = {
            "write:datagroups",
"read:datagroups"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryDataGroup.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Publisher not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addDataGroup(@Parameter(description = "", required=true)@FormParam("dataGroupId")  String dataGroupId,@Parameter(description = "", required=true)@FormParam("publisherId")  String publisherId2,@Parameter(description = "", required=true)@FormParam("dataGroup")  String dataGroup, @PathParam("publisherId") String publisherId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addDataGroup(dataGroupId,publisherId2,dataGroup,publisherId,securityContext);
    }
    @POST
    @Path("/{publisherId}/datagroups")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add data groups to an existing publisher", description = "add data groups to an existing publisher by publisherId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "datagroup_auth", scopes = {
            "write:datagroups",
"read:datagroups"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryDataGroup.class)))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Publisher not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addDataGroups(
@Parameter(description = "add data groups to an existing publisher" ,required=true) List<com.plantssoil.webhook.core.registry.InMemoryDataGroup> body
, @PathParam("publisherId") String publisherId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addDataGroups(body,publisherId,securityContext);
    }
    @POST
    @Path("/{publisherId}/event")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add an event to an existing publisher", description = "add an event to an existing publisher by publisherId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "event_auth", scopes = {
            "write:events",
"read:events"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryEvent.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Publisher not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addEvent(
@Parameter(description = "add an event to an existing publisher" ,required=true) com.plantssoil.webhook.core.registry.InMemoryEvent body
, @PathParam("publisherId") String publisherId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addEvent(body,publisherId,securityContext);
    }
    @POST
    @Path("/{publisherId}/event")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add an event to an existing publisher", description = "add an event to an existing publisher by publisherId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "event_auth", scopes = {
            "write:events",
"read:events"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryEvent.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Publisher not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addEvent(@Parameter(description = "", required=true)@FormParam("eventId")  String eventId,@Parameter(description = "", required=true)@FormParam("publisherId")  String publisherId2,@Parameter(description = "", required=true)@FormParam("eventType")  String eventType,@Parameter(description = "", required=true)@FormParam("eventTag")  String eventTag,@Parameter(description = "", required=true)@FormParam("contentType")  String contentType,@Parameter(description = "", required=true)@FormParam("charset")  String charset,@Parameter(description = "", required=true, schema=@Schema(allowableValues={ "SUBMITTED", "PUBLISHED", "RETIRED" })
)@FormParam("eventStatus")  String eventStatus, @PathParam("publisherId") String publisherId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addEvent(eventId,publisherId2,eventType,eventTag,contentType,charset,eventStatus,publisherId,securityContext);
    }
    @POST
    @Path("/{publisherId}/events")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add events to an existing publisher", description = "add events to an existing publisher by publisherId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "event_auth", scopes = {
            "write:events",
"read:events"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryEvent.class)))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Publisher not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addEvents(
@Parameter(description = "add events to an existing publisher" ,required=true) List<com.plantssoil.webhook.core.registry.InMemoryEvent> body
, @PathParam("publisherId") String publisherId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addEvents(body,publisherId,securityContext);
    }
    @POST
    
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add a new publisher", description = "Add a new publisher", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "publisher_auth", scopes = {
            "write:publishers",
"read:publishers"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryPublisher.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addPublisher(
@Parameter(description = "Create a new publisher" ,required=true) com.plantssoil.webhook.core.registry.InMemoryPublisher body
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addPublisher(body,securityContext);
    }
    @POST
    
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Add a new publisher", description = "Add a new publisher", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "publisher_auth", scopes = {
            "write:publishers",
"read:publishers"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryPublisher.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response addPublisher(@Parameter(description = "", required=true)@FormParam("publisherId")  String publisherId,@Parameter(description = "", required=true)@FormParam("organizationId")  String organizationId,@Parameter(description = "", required=true)@FormParam("supportDataGroup")  Boolean supportDataGroup,@Parameter(description = "", required=true)@FormParam("version")  String version,@Parameter(description = "", required=true)@FormParam("createdBy")  String createdBy,@Parameter(description = "", required=true)@FormParam("creationTime")  Date creationTime,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.addPublisher(publisherId,organizationId,supportDataGroup,version,createdBy,creationTime,securityContext);
    }
    @GET
    @Path("/all")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find all publishers with pagination", description = "Will find the publishers on the page specified (page, pageSize)", security = {
        @SecurityRequirement(name = "publisher_auth", scopes = {
            "write:publishers",
"read:publishers"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryPublisher.class)))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findAllPublishers( @NotNull  @QueryParam("page") Integer page, @NotNull  @QueryParam("pageSize") Integer pageSize,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findAllPublishers(page,pageSize,securityContext);
    }
    @GET
    @Path("/datagroup/{dataGroupId}")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find data group by ID", description = "Returns a single data group", security = {
        @SecurityRequirement(name = "datagroup_auth", scopes = {
            "write:datagroups",
"read:datagroups"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryDataGroup.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Data Group not found") })
    public Response findDataGroupById( @PathParam("dataGroupId") String dataGroupId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findDataGroupById(dataGroupId,securityContext);
    }
    @GET
    @Path("/{publisherId}/datagroup")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find data group which belong to the specific publisher", description = "Find data group which belong to the specific publisher with data group name", security = {
        @SecurityRequirement(name = "datagroup_auth", scopes = {
            "write:datagroups",
"read:datagroups"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryDataGroup.class))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findDataGroupByName( @PathParam("publisherId") String publisherId, @NotNull  @QueryParam("dataGroup") String dataGroup,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findDataGroupByName(publisherId,dataGroup,securityContext);
    }
    @GET
    @Path("/{publisherId}/allDataGroups")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find all data groups which belong to the specific publisher", description = "Find all data groups which belong to the specific publisher with pagination", security = {
        @SecurityRequirement(name = "datagroup_auth", scopes = {
            "write:datagroups",
"read:datagroups"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryDataGroup.class)))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findDataGroups( @PathParam("publisherId") String publisherId, @NotNull  @QueryParam("page") Integer page, @NotNull  @QueryParam("pageSize") Integer pageSize,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findDataGroups(publisherId,page,pageSize,securityContext);
    }
    @GET
    @Path("/event/{eventId}")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find event by ID", description = "Returns a single event", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "event_auth", scopes = {
            "write:events",
"read:events"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryEvent.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Event not found") })
    public Response findEventById( @PathParam("eventId") String eventId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findEventById(eventId,securityContext);
    }
    @GET
    @Path("/{publisherId}/allEvents")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find all events which belong to the specific publisher", description = "Find all events which belong to the specific publisher with pagination", security = {
        @SecurityRequirement(name = "event_auth", scopes = {
            "write:events",
"read:events"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryEvent.class)))),
        
        @ApiResponse(responseCode = "400", description = "Validation exception") })
    public Response findEvents( @PathParam("publisherId") String publisherId, @NotNull  @QueryParam("page") Integer page, @NotNull  @QueryParam("pageSize") Integer pageSize,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findEvents(publisherId,page,pageSize,securityContext);
    }
    @GET
    @Path("/{publisherId}")
    
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Find publisher by ID", description = "Returns a single publisher", security = {
        @SecurityRequirement(name = "publisher_auth", scopes = {
            "write:publishers",
"read:publishers"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryPublisher.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        
        @ApiResponse(responseCode = "404", description = "Publisher not found") })
    public Response findPublisherById( @PathParam("publisherId") String publisherId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.findPublisherById(publisherId,securityContext);
    }
    @PUT
    @Path("/{publisherId}")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Update an existing publisher", description = "Update an existing publisher by publisherId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "publisher_auth", scopes = {
            "write:publishers",
"read:publishers"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryPublisher.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Publisher not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response updatePublisherById(
@Parameter(description = "Update an existent publisher" ,required=true) com.plantssoil.webhook.core.registry.InMemoryPublisher body
, @PathParam("publisherId") String publisherId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.updatePublisherById(body,publisherId,securityContext);
    }
    @PUT
    @Path("/{publisherId}")
    @Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Produces({ "application/json", "application/xml" })
    @Operation(summary = "Update an existing publisher", description = "Update an existing publisher by publisherId", security = {
        @SecurityRequirement(name = "api_key"),
@SecurityRequirement(name = "publisher_auth", scopes = {
            "write:publishers",
"read:publishers"
        })
    }, tags={ "publisher" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.core.registry.InMemoryPublisher.class))),
        
        @ApiResponse(responseCode = "400", description = "Required attributes cant't be null!"),
        
        @ApiResponse(responseCode = "404", description = "Publisher not found"),
        
        @ApiResponse(responseCode = "422", description = "Validation exception") })
    public Response updatePublisherById(@Parameter(description = "", required=true)@FormParam("publisherId")  String publisherId2,@Parameter(description = "", required=true)@FormParam("organizationId")  String organizationId,@Parameter(description = "", required=true)@FormParam("supportDataGroup")  Boolean supportDataGroup,@Parameter(description = "", required=true)@FormParam("version")  String version,@Parameter(description = "", required=true)@FormParam("createdBy")  String createdBy,@Parameter(description = "", required=true)@FormParam("creationTime")  Date creationTime, @PathParam("publisherId") String publisherId,@Context SecurityContext securityContext)
    throws NotFoundException {
        return service.updatePublisherById(publisherId2,organizationId,supportDataGroup,version,createdBy,creationTime,publisherId,securityContext);
    }
}
