package com.plantssoil.webhook.persists.logging;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;
import com.plantssoil.webhook.persists.beans.WebhookLog;
import com.plantssoil.webhook.persists.beans.WebhookLogLine;

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
                IEntityQuery<WebhookLog> query = persists.createQuery(WebhookLog.class);
                WebhookLog log = query.singleResult(message.getRequestId()).get();
                if (log != null) {
                    WebhookLogLine line = new WebhookLogLine();
                    line.setLogLineId(EntityUtils.getInstance().createUniqueObjectId());
                    line.setRequestId(message.getRequestId());
                    line.setSubscriberId(subscriber.getWebhookId());
                    line.setLogTime(new Date());
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
