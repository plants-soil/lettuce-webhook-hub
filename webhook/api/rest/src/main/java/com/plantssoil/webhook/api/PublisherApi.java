package com.plantssoil.webhook.api;

import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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

import com.plantssoil.webhook.beans.InlineResponse2002;
import com.plantssoil.webhook.beans.InlineResponse2003;
import com.plantssoil.webhook.beans.InlineResponse2004;
import com.plantssoil.webhook.beans.InlineResponse2005;
import com.plantssoil.webhook.beans.InlineResponse2006;
import com.plantssoil.webhook.beans.InlineResponse2007;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Path("/publisher")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-09T17:41:01.999402200+08:00[Asia/Shanghai]")
public class PublisherApi {

    @Inject
    PublisherApiService service;

    @POST
    @Path("/{publisherId}/datagroup")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Add a data group to an existing publisher", description = "add a data group to an existing publisher by publisherId", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "datagroup_auth", scopes = { "write:datagroups", "read:datagroups" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the posted data group object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2006.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response addDataGroup(
            @Parameter(description = "add a data group to an existing publisher", required = true) com.plantssoil.webhook.core.registry.InMemoryDataGroup body,
            @PathParam("publisherId") String publisherId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.addDataGroup(body, publisherId, securityContext);
    }

    @POST
    @Path("/{publisherId}/datagroups")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Add data groups to an existing publisher", description = "add data groups to an existing publisher by publisherId", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "datagroup_auth", scopes = { "write:datagroups", "read:datagroups" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contain posted data group array in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2007.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response addDataGroups(
            @Parameter(description = "add data groups to an existing publisher", required = true) List<com.plantssoil.webhook.core.registry.InMemoryDataGroup> body,
            @PathParam("publisherId") String publisherId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.addDataGroups(body, publisherId, securityContext);
    }

    @POST
    @Path("/{publisherId}/event")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Add an event to an existing publisher", description = "add an event to an existing publisher by publisherId", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "event_auth", scopes = { "write:events", "read:events" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the posted event object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2004.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response addEvent(
            @Parameter(description = "add an event to an existing publisher", required = true) com.plantssoil.webhook.core.registry.InMemoryEvent body,
            @PathParam("publisherId") String publisherId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.addEvent(body, publisherId, securityContext);
    }

    @POST
    @Path("/{publisherId}/events")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Add events to an existing publisher", description = "add events to an existing publisher by publisherId", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "event_auth", scopes = { "write:events", "read:events" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contain posted event object array in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2005.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response addEvents(
            @Parameter(description = "add events to an existing publisher", required = true) List<com.plantssoil.webhook.core.registry.InMemoryEvent> body,
            @PathParam("publisherId") String publisherId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.addEvents(body, publisherId, securityContext);
    }

    @POST
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Add a new publisher", description = "Add a new publisher", security = { @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "publisher_auth", scopes = { "write:publishers", "read:publishers" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the posted publisher object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2002.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response addPublisher(
            @Parameter(description = "Create a new publisher", required = true) com.plantssoil.webhook.core.registry.InMemoryPublisher body,
            @Context SecurityContext securityContext) throws NotFoundException {
        return service.addPublisher(body, securityContext);
    }

    @GET
    @Path("/all")
    @Produces({ "application/json" })
    @Operation(summary = "Find all publishers with pagination", description = "Will find the publishers on the page specified (page, pageSize)", security = {
            @SecurityRequirement(name = "publisher_auth", scopes = { "write:publishers", "read:publishers" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contain publisher object array in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2003.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findAllPublishers(@NotNull @QueryParam("page") Integer page, @NotNull @QueryParam("pageSize") Integer pageSize,
            @Context SecurityContext securityContext) throws NotFoundException {
        return service.findAllPublishers(page, pageSize, securityContext);
    }

    @GET
    @Path("/datagroup/{dataGroupId}")
    @Produces({ "application/json" })
    @Operation(summary = "Find data group by ID", description = "Returns a single data group", security = {
            @SecurityRequirement(name = "datagroup_auth", scopes = { "write:datagroups", "read:datagroups" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the data group object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2006.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findDataGroupById(@PathParam("dataGroupId") String dataGroupId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.findDataGroupById(dataGroupId, securityContext);
    }

    @GET
    @Path("/{publisherId}/datagroup")
    @Produces({ "application/json" })
    @Operation(summary = "Find data group which belong to the specific publisher", description = "Find data group which belong to the specific publisher with data group name", security = {
            @SecurityRequirement(name = "datagroup_auth", scopes = { "write:datagroups", "read:datagroups" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the data group object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2006.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findDataGroupByName(@PathParam("publisherId") String publisherId, @NotNull @QueryParam("dataGroup") String dataGroup,
            @Context SecurityContext securityContext) throws NotFoundException {
        return service.findDataGroupByName(publisherId, dataGroup, securityContext);
    }

    @GET
    @Path("/{publisherId}/allDataGroups")
    @Produces({ "application/json" })
    @Operation(summary = "Find all data groups which belong to the specific publisher", description = "Find all data groups which belong to the specific publisher with pagination", security = {
            @SecurityRequirement(name = "datagroup_auth", scopes = { "write:datagroups", "read:datagroups" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contain data group array in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2007.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findDataGroups(@PathParam("publisherId") String publisherId, @NotNull @QueryParam("page") Integer page,
            @NotNull @QueryParam("pageSize") Integer pageSize, @Context SecurityContext securityContext) throws NotFoundException {
        return service.findDataGroups(publisherId, page, pageSize, securityContext);
    }

    @GET
    @Path("/event/{eventId}")
    @Produces({ "application/json" })
    @Operation(summary = "Find event by ID", description = "Returns a single event", security = { @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "event_auth", scopes = { "write:events", "read:events" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the event object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2004.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findEventById(@PathParam("eventId") String eventId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.findEventById(eventId, securityContext);
    }

    @GET
    @Path("/{publisherId}/allEvents")
    @Produces({ "application/json" })
    @Operation(summary = "Find all events which belong to the specific publisher", description = "Find all events which belong to the specific publisher with pagination", security = {
            @SecurityRequirement(name = "event_auth", scopes = { "write:events", "read:events" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contain event object array in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2005.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findEvents(@PathParam("publisherId") String publisherId, @NotNull @QueryParam("page") Integer page,
            @NotNull @QueryParam("pageSize") Integer pageSize, @Context SecurityContext securityContext) throws NotFoundException {
        return service.findEvents(publisherId, page, pageSize, securityContext);
    }

    @GET
    @Path("/{publisherId}")
    @Produces({ "application/json" })
    @Operation(summary = "Find publisher by ID", description = "Returns a single publisher", security = {
            @SecurityRequirement(name = "publisher_auth", scopes = { "write:publishers", "read:publishers" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the publisher object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2002.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findPublisherById(@PathParam("publisherId") String publisherId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.findPublisherById(publisherId, securityContext);
    }

    @PUT
    @Path("/{publisherId}")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Update an existing publisher", description = "Update an existing publisher by publisherId", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "publisher_auth", scopes = { "write:publishers", "read:publishers" }) }, tags = { "publisher" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the updated publisher object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2002.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response updatePublisherById(
            @Parameter(description = "Update an existent publisher", required = true) com.plantssoil.webhook.core.registry.InMemoryPublisher body,
            @PathParam("publisherId") String publisherId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.updatePublisherById(body, publisherId, securityContext);
    }
}
