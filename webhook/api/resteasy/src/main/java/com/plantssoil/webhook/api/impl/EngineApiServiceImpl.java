package com.plantssoil.webhook.api.impl;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.plantssoil.webhook.api.ApiResponseMessage;
import com.plantssoil.webhook.api.EngineApiService;
import com.plantssoil.webhook.api.NotFoundException;

@RequestScoped
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-07T18:18:51.966634600+08:00[Asia/Shanghai]")
public class EngineApiServiceImpl implements EngineApiService {
    @Override
    public Response findAllPublisherWebhookLogs(String publisherId, Integer page, Integer pageSize, String dataGroup, SecurityContext securityContext)
            throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response findAllSubscriberWebhookLogs(String webhookId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response findWebhookLogLines(String requestId, String webhookId, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response getEngineVersion(SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response trigger(com.plantssoil.webhook.core.Message body, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response trigger(String publisherId, String version, String eventType, String eventTag, String contentType, String charset, String dataGroup,
            String requestId, String payload, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
