package com.plantssoil.webhook.core.logging;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.beans.WebhookEventLog;
import com.plantssoil.webhook.core.IWebhookEvent;

/**
 * Webhook Publish Logging
 * 
 * @author danialdy
 * @Date 18 Nov 2024 2:07:37 pm
 */
public class WebhookPublishLogging implements IWebhookLogging {

    @Override
    public void logBefore(Object proxy, Method method, Object[] args) {
        if (args.length != 4) {
            return;
        }
        if (args[0] == null || !(args[0] instanceof IWebhookEvent)) {
            return;
        }
        if (args[1] != null && !(args[1] instanceof String)) {
            return;
        }
        if (args[2] == null || !(args[2] instanceof String)) {
            return;
        }
        if (args[3] == null || !(args[3] instanceof String)) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            IWebhookEvent event = (IWebhookEvent) args[0];
            String dataGroup = args[1] == null ? null : (String) args[1];
            String requestId = (String) args[2];
            String payload = (String) args[3];
            try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
                IEntityQuery<WebhookEventLog> query = persists.createQuery(WebhookEventLog.class);
                WebhookEventLog log = query.singleResult(requestId).get();
                if (log == null) {
                    log = new WebhookEventLog();
                    log.setPublisherId(event.getPublisherId());
                    log.setVersion(event.getVersion());
                    log.setDataGroup(dataGroup);
                    log.setEventType(event.getEventType());
                    log.setEventTag(event.getEventTag());
                    log.setContentType(event.getContentType());
                    log.setCharset(event.getCharset());
                    log.setRequestId(requestId);
                    log.setPayload(payload);
                    log.setWebhookStatus("publish");
                    persists.create(log);
                }
            } catch (Exception e) {
                // no need throw exception for logging, no need interrupt the main process
                // thread
                e.printStackTrace();
            }
        });
    }

    @Override
    public void logAfter(Object proxy, Method method, Object[] args) {
    }

}
