package com.plantssoil.webhook.persists.logging;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.core.Message;
import com.plantssoil.webhook.persists.beans.WebhookEventLog;

/**
 * Webhook Publish Logging
 * 
 * @author danialdy
 * @Date 18 Nov 2024 2:07:37 pm
 */
public class WebhookTriggerLogging implements IWebhookLogging {

    @Override
    public void logBefore(Object proxy, Method method, Object[] args) {
        if (args.length != 1) {
            return;
        }
        if (args[0] == null || !(args[0] instanceof Message)) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            Message event = (Message) args[0];
            try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
                IEntityQuery<WebhookEventLog> query = persists.createQuery(WebhookEventLog.class);
                WebhookEventLog log = query.singleResult(event.getRequestId()).get();
                if (log == null) {
                    log = new WebhookEventLog();
                    log.setPublisherId(event.getPublisherId());
                    log.setVersion(event.getVersion());
                    log.setDataGroup(event.getDataGroup());
                    log.setEventType(event.getEventType());
                    log.setEventTag(event.getEventTag());
                    log.setContentType(event.getContentType());
                    log.setCharset(event.getCharset());
                    log.setRequestId(event.getRequestId());
                    log.setPayload(event.getPayload());
                    log.setWebhookStatus("publish");
                    log.setTriggerTime(new Date());
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
