package com.plantssoil.webhook.persists.logging;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.httpclient.impl.NamedThreadFactory;
import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IEntityQuery.FilterOperator;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.core.ILogging;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.IWebhookLog;
import com.plantssoil.webhook.core.IWebhookLogLine;
import com.plantssoil.webhook.core.Message;
import com.plantssoil.webhook.persists.beans.WebhookLog;
import com.plantssoil.webhook.persists.beans.WebhookLogLine;
import com.plantssoil.webhook.persists.exception.EnginePersistenceException;

/**
 * The persistence logging implementation which will persists logging
 * information
 * 
 * @author danialdy
 * @Date 22 Jan 2025 4:29:20 pm
 */
public class PersistedLogging implements ILogging, IConfigurable {
    private ExecutorService loggingExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("Persisted-Logging-Executor"));
    private static String PERSISTENCE_FACTORY_CONFIGURABLE = ConfigFactory.getInstance().getConfiguration()
            .getString(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE);

    @Override
    public void triggerMessage(Message message) {
        if (PERSISTENCE_FACTORY_CONFIGURABLE == null) {
            return;
        }

        loggingExecutor.submit(() -> {
            try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
                IEntityQuery<WebhookLog> query = persists.createQuery(WebhookLog.class);
                WebhookLog log = query.singleResult(message.getRequestId()).get();
                if (log == null) {
                    log = new WebhookLog();
                    log.setPublisherId(message.getPublisherId());
                    log.setVersion(message.getVersion());
                    log.setDataGroup(message.getDataGroup());
                    log.setEventType(message.getEventType());
                    log.setEventTag(message.getEventTag());
                    log.setContentType(message.getContentType());
                    log.setCharset(message.getCharset());
                    log.setRequestId(message.getRequestId());
                    log.setPayload(message.getPayload());
                    log.setWebhookStatus("Published");
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
    public void dispatchMessage(Message message, IWebhook webhook, int tryTime) {
        if (PERSISTENCE_FACTORY_CONFIGURABLE == null) {
            return;
        }

        loggingExecutor.submit(() -> {
            try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
                WebhookLogLine line = new WebhookLogLine();
                line.setLogLineId(EntityUtils.getInstance().createUniqueObjectId());
                line.setSubscriberId(webhook.getWebhookId());
                line.setWebhookId(webhook.getWebhookId());
                line.setRequestId(message.getRequestId());
                line.setLogType("Dispatched");
                line.setLogTime(new Date());
                line.setTryTime(tryTime);
                persists.create(line);
            } catch (Exception e) {
                // no need throw exception for logging, no need interrupt the main process
                // thread
                e.printStackTrace();
            }
        });
    }

    @Override
    public void responseMessage(Message message, IWebhook webhook, String responseType, String information) {
        if (PERSISTENCE_FACTORY_CONFIGURABLE == null) {
            return;
        }

        loggingExecutor.submit(() -> {
            try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
                WebhookLogLine line = new WebhookLogLine();
                line.setLogLineId(EntityUtils.getInstance().createUniqueObjectId());
                line.setSubscriberId(webhook.getWebhookId());
                line.setWebhookId(webhook.getWebhookId());
                line.setRequestId(message.getRequestId());
                line.setLogType(responseType);
                line.setInformation(information);
                line.setLogTime(new Date());
                persists.create(line);
            } catch (Exception e) {
                // no need throw exception for logging, no need interrupt the main process
                // thread
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<IWebhookLog> findAllWebhookLogs(String publisherId, String dataGroup, int page, int pageSize) {
        if (publisherId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21018,
                    "Publisher id should not be null when query webhook logs!");
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            IEntityQuery q = p.createQuery(WebhookLog.class).firstResult(page * pageSize).maxResults(pageSize);
            q.filter("publisherId", FilterOperator.equals, publisherId);
            if (dataGroup != null) {
                q.filter("dataGroup", FilterOperator.equals, dataGroup);
            }
            return (List<IWebhookLog>) q.resultList().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21018, e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<IWebhookLog> findAllWebhookLogs(String webhookId, int page, int pageSize) {
        if (webhookId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21018,
                    "Webhook id should not be null when query webhook logs!");
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            IEntityQuery<WebhookLogLine> q = p.createQuery(WebhookLogLine.class).firstResult(page * pageSize).maxResults(pageSize);
            q.filter("webhookId", FilterOperator.equals, webhookId);
            List<String> rids = q.distinct(String.class, "requestId");
            IEntityQuery ql = p.createQuery(WebhookLog.class).filter("requestId", FilterOperator.in, rids).firstResult(page * pageSize).maxResults(pageSize);
            return (List<IWebhookLog>) ql.resultList().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21018, e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<IWebhookLogLine> findWebhookLogLines(String requestId, String webhookId) {
        if (requestId == null || webhookId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21018,
                    "Request id or webhook id should not be null when query webhook log lines!");
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            IEntityQuery q = p.createQuery(WebhookLogLine.class).maxResults(200);
            q.filter("requestId", FilterOperator.equals, requestId);
            q.filter("webhookId", FilterOperator.equals, webhookId);
            return (List<IWebhookLogLine>) q.resultList().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21018, e);
        }
    }

}
