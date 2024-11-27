package com.plantssoil.webhook.core.logging;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.beans.WebhookEventLog;
import com.plantssoil.webhook.core.impl.DefaultWebhookMessage;

/**
 * Webhook Consume Logging
 * 
 * @author danialdy
 * @Date 18 Nov 2024 2:14:13 pm
 */
public class WebhookConsumLogging implements IWebhookLogging {

    @Override
    public void logBefore(Object proxy, Method method, Object[] args) {
    }

    @Override
    public void logAfter(Object proxy, Method method, Object[] args) {
        if (args.length != 2) {
            return;
        }
        if (args[0] == null || !(args[0] instanceof DefaultWebhookMessage)) {
            return;
        }
        if (args[1] != null && !(args[1] instanceof String)) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            DefaultWebhookMessage message = (DefaultWebhookMessage) args[0];
            try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
                IEntityQuery<WebhookEventLog> query = persists.createQuery(WebhookEventLog.class);
                WebhookEventLog log = query.singleResult(message.getRequestId()).get();
                if (log != null) {
                    log.setWebhookStatus("webhook called");
                    persists.update(log);
                }
            } catch (Exception e) {
                // no need throw exception for logging, no need interrupt the main process
                // thread
                e.printStackTrace();
            }
        });
    }

}
