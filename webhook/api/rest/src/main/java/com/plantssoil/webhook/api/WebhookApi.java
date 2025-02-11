package com.plantssoil.webhook.api;

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

import com.plantssoil.webhook.beans.InlineResponse20010;
import com.plantssoil.webhook.beans.InlineResponse20011;
import com.plantssoil.webhook.beans.InlineResponse2005;
import com.plantssoil.webhook.beans.InlineResponse2006;
import com.plantssoil.webhook.beans.InlineResponse2007;
import com.plantssoil.webhook.beans.WebhookSubscribeDataGroupBody;
import com.plantssoil.webhook.beans.WebhookSubscribeDataGroupsBody;
import com.plantssoil.webhook.beans.WebhookSubscribeEventBody;
import com.plantssoil.webhook.beans.WebhookSubscribeEventsBody;
import com.plantssoil.webhook.beans.WebhookUnsubscribeDataGroupBody;
import com.plantssoil.webhook.beans.WebhookUnsubscribeDataGroupsBody;
import com.plantssoil.webhook.beans.WebhookUnsubscribeEventBody;
import com.plantssoil.webhook.beans.WebhookUnsubscribeEventsBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Path("/webhook")

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-10T20:32:04.596427700+08:00[Asia/Shanghai]")
public class WebhookApi {

    @Inject
    WebhookApiService service;

    @POST
    @Path("/activate")
    @Produces({ "application/json" })
    @Operation(summary = "Activate webhook", description = "Activate webhook and change webhook status to production", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the activated webhook object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse20010.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response activateWebhook(@NotNull @QueryParam("webhookId") String webhookId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.activateWebhook(webhookId, securityContext);
    }

    @POST
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Add a new webhook", description = "Add a new webhook", security = { @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the posted webhook object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse20010.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response addWebhook(@Parameter(description = "Create a new webhook", required = true) com.plantssoil.webhook.core.registry.InMemoryWebhook body,
            @Context SecurityContext securityContext) throws NotFoundException {
        return service.addWebhook(body, securityContext);
    }

    @POST
    @Path("/deactivate")
    @Produces({ "application/json" })
    @Operation(summary = "Deactivate webhook", description = "Deactivate webhook and change webhook status to inactive", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the deactivated webhook object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse20010.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response deactivateWebhook(@NotNull @QueryParam("webhookId") String webhookId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.deactivateWebhook(webhookId, securityContext);
    }

    @GET
    @Path("/all")
    @Produces({ "application/json" })
    @Operation(summary = "Find all webhooks which belong to specific subscriber with pagination", description = "Will find the webhooks on the page specified (page, pageSize)", security = {
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contain webhook array in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse20011.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findAllWebhooks(@NotNull @QueryParam("subscriberId") String subscriberId, @NotNull @QueryParam("page") Integer page,
            @NotNull @QueryParam("pageSize") Integer pageSize, @Context SecurityContext securityContext) throws NotFoundException {
        return service.findAllWebhooks(subscriberId, page, pageSize, securityContext);
    }

    @GET
    @Path("/subscribedDataGroup")
    @Produces({ "application/json" })
    @Operation(summary = "Find the subscribed data group which belong to specific webhook with data group name", description = "Will find the subscribed data group which belong to specific webhook with data group name", security = {
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the data group object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2006.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findSubscribedDataGroup(@NotNull @QueryParam("webhookId") String webhookId, @NotNull @QueryParam("dataGroup") String dataGroup,
            @Context SecurityContext securityContext) throws NotFoundException {
        return service.findSubscribedDataGroup(webhookId, dataGroup, securityContext);
    }

    @GET
    @Path("/allSubscribedDataGroups")
    @Produces({ "application/json" })
    @Operation(summary = "Find all subscribed data groups which belong to specific webhook with pagination", description = "Will find the subscribed data groups on the page specified (page, pageSize)", security = {
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contain data group array in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2007.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findSubscribedDataGroups(@NotNull @QueryParam("webhookId") String webhookId, @NotNull @QueryParam("page") Integer page,
            @NotNull @QueryParam("pageSize") Integer pageSize, @Context SecurityContext securityContext) throws NotFoundException {
        return service.findSubscribedDataGroups(webhookId, page, pageSize, securityContext);
    }

    @GET
    @Path("/allSubscribedEvents")
    @Produces({ "application/json" })
    @Operation(summary = "Find all subscribed events which belong to specific webhook with pagination", description = "Will find the subscribed events on the page specified (page, pageSize)", security = {
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contain event object array in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse2005.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findSubscribedEvents(@NotNull @QueryParam("webhookId") String webhookId, @NotNull @QueryParam("page") Integer page,
            @NotNull @QueryParam("pageSize") Integer pageSize, @Context SecurityContext securityContext) throws NotFoundException {
        return service.findSubscribedEvents(webhookId, page, pageSize, securityContext);
    }

    @GET
    @Path("/{webhookId}")
    @Produces({ "application/json" })
    @Operation(summary = "Find webhook by ID", description = "Returns a single webhook", security = { @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the webhook object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse20010.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response findWebhookById(@PathParam("webhookId") String webhookId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.findWebhookById(webhookId, securityContext);
    }

    @POST
    @Path("/subscribeDataGroup")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Subscribe a publisher data group for webhook", description = "Subscribe a publisher data group for webhook", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response subscribeDataGroup(@Parameter(description = "") WebhookSubscribeDataGroupBody body, @Context SecurityContext securityContext)
            throws NotFoundException {
        return service.subscribeDataGroup(body.getWebhookId(), body.getDataGroupId(), securityContext);
    }

    @POST
    @Path("/subscribeDataGroups")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Subscribe publisher data groups for webhook", description = "Subscribe publisher data groups for webhook", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response subscribeDataGroups(@Parameter(description = "") WebhookSubscribeDataGroupsBody body, @Context SecurityContext securityContext)
            throws NotFoundException {
        return service.subscribeDataGroups(body.getWebhookId(), body.getDataGroupIds(), securityContext);
    }

    @POST
    @Path("/subscribeEvent")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Subscribe a publisher event for webhook", description = "Subscribe a publisher event for webhook", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response subscribeEvent(@Parameter(description = "") WebhookSubscribeEventBody body, @Context SecurityContext securityContext)
            throws NotFoundException {
        return service.subscribeEvent(body.getWebhookId(), body.getEventId(), securityContext);
    }

    @POST
    @Path("/subscribeEvents")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Subscribe publisher events for webhook", description = "Subscribe publisher events for webhook", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response subscribeEvents(@Parameter(description = "") WebhookSubscribeEventsBody body, @Context SecurityContext securityContext)
            throws NotFoundException {
        return service.subscribeEvents(body.getWebhookId(), body.getEventIds(), securityContext);
    }

    @POST
    @Path("/unsubscribeDataGroup")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Unsubscribe a publisher data group for webhook", description = "Unsubscribe a publisher data group for webhook", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response unsubscribeDataGroup(@Parameter(description = "") WebhookUnsubscribeDataGroupBody body, @Context SecurityContext securityContext)
            throws NotFoundException {
        return service.unsubscribeDataGroup(body.getWebhookId(), body.getDataGroupId(), securityContext);
    }

    @POST
    @Path("/unsubscribeDataGroups")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Unsubscribe publisher events for webhook", description = "Unsubscribe publisher events for webhook", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response unsubscribeDataGroups(@Parameter(description = "") WebhookUnsubscribeDataGroupsBody body, @Context SecurityContext securityContext)
            throws NotFoundException {
        return service.unsubscribeDataGroups(body.getWebhookId(), body.getDataGroupIds(), securityContext);
    }

    @POST
    @Path("/unsubscribeEvent")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Unsubscribe a publisher event for webhook", description = "Unsubscribe a publisher event for webhook", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response unsubscribeEvent(@Parameter(description = "") WebhookUnsubscribeEventBody body, @Context SecurityContext securityContext)
            throws NotFoundException {
        return service.unsubscribeEvent(body.getWebhookId(), body.getEventId(), securityContext);
    }

    @POST
    @Path("/unsubscribeEvents")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Unsubscribe publisher events for webhook", description = "Unsubscribe publisher events for webhook", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.plantssoil.webhook.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response unsubscribeEvents(@Parameter(description = "") WebhookUnsubscribeEventsBody body, @Context SecurityContext securityContext)
            throws NotFoundException {
        return service.unsubscribeEvents(body.getWebhookId(), body.getEventIds(), securityContext);
    }

    @PUT
    @Path("/{webhookId}")
    @Consumes({ "application/json", "application/x-www-form-urlencoded" })
    @Produces({ "application/json" })
    @Operation(summary = "Update an existing webhook", description = "Update an existing webhook by webhookId", security = {
            @SecurityRequirement(name = "api_key"),
            @SecurityRequirement(name = "webhook_auth", scopes = { "write:webhooks", "read:webhooks" }) }, tags = { "webhook" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation, will contains the updated webhook object in ApiResponse.data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InlineResponse20010.class))),
            @ApiResponse(responseCode = "400", description = "Bad request of the web service"),
            @ApiResponse(responseCode = "401", description = "Unauthorized of the web service"),
            @ApiResponse(responseCode = "404", description = "Not found the web service") })
    public Response updateWebhookById(
            @Parameter(description = "Update an existent webhook", required = true) com.plantssoil.webhook.core.registry.InMemoryWebhook body,
            @PathParam("webhookId") String webhookId, @Context SecurityContext securityContext) throws NotFoundException {
        return service.updateWebhookById(body, webhookId, securityContext);
    }
}
