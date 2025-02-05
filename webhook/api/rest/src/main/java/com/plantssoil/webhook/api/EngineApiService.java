package com.plantssoil.webhook.api;

import com.plantssoil.webhook.api.*;
import com.plantssoil.webhook.beans.*;

import com.plantssoil.webhook.beans.ModelApiResponse;

import java.util.List;
import java.util.Map;
import com.plantssoil.webhook.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-05T11:58:30.837020300+08:00[Asia/Shanghai]")
public interface EngineApiService {
      Response getEngineVersion(SecurityContext securityContext) throws NotFoundException;
      Response trigger(com.plantssoil.webhook.core.Message body,SecurityContext securityContext) throws NotFoundException;
      Response trigger(String publisherId,String version,String eventType,String eventTag,String contentType,String charset,String dataGroup,String requestId,String payload,SecurityContext securityContext) throws NotFoundException;
}
