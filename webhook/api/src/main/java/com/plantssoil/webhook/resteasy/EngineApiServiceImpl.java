package com.plantssoil.webhook.resteasy;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.plantssoil.webhook.api.EngineApiService;
import com.plantssoil.webhook.api.NotFoundException;
import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.ILogging;
import com.plantssoil.webhook.core.IWebhookLog;
import com.plantssoil.webhook.core.IWebhookLogLine;

@ApplicationScoped
@Default
public class EngineApiServiceImpl implements EngineApiService {
    public EngineApiServiceImpl() {
    }

    @Override
    public Response findAllPublisherWebhookLogs(String publisherId, Integer page, Integer pageSize, String dataGroup, SecurityContext securityContext)
            throws NotFoundException {
        try {
            ILogging logging = IEngineFactory.getFactoryInstance().getEngine().getLogging();
            List<IWebhookLog> webhookLogs = logging.findAllWebhookLogs(publisherId, dataGroup, page, pageSize);
            return ResponseBuilder.ok().data(webhookLogs).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findAllSubscriberWebhookLogs(String webhookId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException {
        try {
            ILogging logging = IEngineFactory.getFactoryInstance().getEngine().getLogging();
            List<IWebhookLog> webhookLogs = logging.findAllWebhookLogs(webhookId, page, pageSize);
            return ResponseBuilder.ok().data(webhookLogs).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findWebhookLogLines(String requestId, String webhookId, SecurityContext securityContext) throws NotFoundException {
        try {
            ILogging logging = IEngineFactory.getFactoryInstance().getEngine().getLogging();
            List<IWebhookLogLine> webhookLogs = logging.findWebhookLogLines(requestId, webhookId);
            return ResponseBuilder.ok().data(webhookLogs).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response getEngineVersion(SecurityContext securityContext) throws NotFoundException {
        try {
            IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
            String version = engine.getVersion();
            return ResponseBuilder.ok().message(version).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response trigger(com.plantssoil.webhook.core.Message body, SecurityContext securityContext) throws NotFoundException {
        try {
            IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
            engine.trigger(body);
            return ResponseBuilder.ok().build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response trigger(String publisherId, String version, String eventType, String eventTag, String contentType, String charset, String dataGroup,
            String requestId, String payload, SecurityContext securityContext) throws NotFoundException {
        com.plantssoil.webhook.core.Message msg = new com.plantssoil.webhook.core.Message(publisherId, version, eventType, eventTag, contentType, charset,
                dataGroup, requestId, payload);
        return trigger(msg, securityContext);
    }
}
