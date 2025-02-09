package com.plantssoil.webhook.api;

import com.plantssoil.webhook.api.*;
import com.plantssoil.webhook.beans.*;

import com.plantssoil.webhook.beans.InlineResponse2008;
import com.plantssoil.webhook.beans.InlineResponse2009;

import java.util.List;
import java.util.Map;
import com.plantssoil.webhook.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-09T17:41:01.999402200+08:00[Asia/Shanghai]")
public interface SubscriberApiService {
      Response addSubscriber(com.plantssoil.webhook.core.registry.InMemorySubscriber body,SecurityContext securityContext) throws NotFoundException;
      Response addSubscriber(String subscriberId,String organizationId,SecurityContext securityContext) throws NotFoundException;
      Response deleteSubscriber(String subscriberId,String apiKey,SecurityContext securityContext) throws NotFoundException;
      Response findAllSubscribers(Integer page,Integer pageSize,SecurityContext securityContext) throws NotFoundException;
      Response findSubscriberById(String subscriberId,SecurityContext securityContext) throws NotFoundException;
      Response updateSubscriberById(com.plantssoil.webhook.core.registry.InMemorySubscriber body,String subscriberId,SecurityContext securityContext) throws NotFoundException;
      Response updateSubscriberById(String subscriberId2,String organizationId,String subscriberId,SecurityContext securityContext) throws NotFoundException;
}
