package com.plantssoil.webhook.core.logging;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import com.plantssoil.common.persistence.EntityIdUtility;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.beans.WebhookEventLog;
import com.plantssoil.webhook.beans.WebhookEventLogLine;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;

/**
 * Webhook HTTP Post Logging
 * 
 * @author danialdy
 * @Date 18 Nov 2024 2:16:06 pm
 */
public class WebhookPostLogging implements IWebhookLogging {

    @Override
    public void logBefore(Object proxy, Method method, Object[] args) {
        if (args.length != 2) {
            return;
        }
        if (args[0] == null || !(args[0] instanceof Message)) {
            return;
        }
        if (args[1] == null || !(args[1] instanceof IWebhook)) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            Message message = (Message) args[0];
            IWebhook subscriber = (IWebhook) args[1];
            try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
                IEntityQuery<WebhookEventLog> query = persists.createQuery(WebhookEventLog.class);
                WebhookEventLog log = query.singleResult(message.getRequestId()).get();
                if (log != null) {
                    WebhookEventLogLine line = new WebhookEventLogLine();
                    line.setLogLineId(EntityIdUtility.getInstance().generateUniqueId());
                    line.setRequestId(message.getRequestId());
                    line.setSubscriberId(subscriber.getWebhookId());
                    line.setExecuteMillseconds(System.currentTimeMillis());
                    line.setPostTime(new Date());
                    persists.create(line);
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
