package com.plantssoil.webhook.api;

import com.plantssoil.webhook.api.*;
import com.plantssoil.webhook.beans.*;

import com.plantssoil.webhook.beans.InlineResponse20012;
import com.plantssoil.webhook.beans.InlineResponse20013;

import java.util.List;
import java.util.Map;
import com.plantssoil.webhook.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-09T17:41:01.999402200+08:00[Asia/Shanghai]")
public interface EngineApiService {
      Response findAllPublisherWebhookLogs(String publisherId,Integer page,Integer pageSize,String dataGroup,SecurityContext securityContext) throws NotFoundException;
      Response findAllSubscriberWebhookLogs(String webhookId,Integer page,Integer pageSize,SecurityContext securityContext) throws NotFoundException;
      Response findWebhookLogLines(String requestId,String webhookId,SecurityContext securityContext) throws NotFoundException;
      Response getEngineVersion(SecurityContext securityContext) throws NotFoundException;
      Response trigger(com.plantssoil.webhook.core.Message body,SecurityContext securityContext) throws NotFoundException;
      Response trigger(String publisherId,String version,String eventType,String eventTag,String contentType,String charset,String dataGroup,String requestId,String payload,SecurityContext securityContext) throws NotFoundException;
}
