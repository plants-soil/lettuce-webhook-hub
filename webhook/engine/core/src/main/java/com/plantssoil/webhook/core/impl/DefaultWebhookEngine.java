package com.plantssoil.webhook.core.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessageListener;
import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.common.persistence.IPersistenceInitializer;
import com.plantssoil.webhook.core.IWebhookEngine;
import com.plantssoil.webhook.core.IWebhookEngineFactory;
import com.plantssoil.webhook.core.IWebhookEvent;
import com.plantssoil.webhook.core.IWebhookPublisher;
import com.plantssoil.webhook.core.IWebhookRegistry;
import com.plantssoil.webhook.core.exception.EngineException;
import com.plantssoil.webhook.core.logging.WebhookLoggingHandler;

/**
 * The default implementation of webhook engine<br/>
 * Could get this webhook engine instance via {@link IWebhookEngineFactory}<br/>
 * e.g:
 * 
 * <pre>
 * <code>
 *   IWebhookEngineFactory factory = IWebhookEngineFactory.getFactoryInstance();
 *   IWebhookEngine engine = factory.getWebhookEngine();
 *   ...
 * </code>
 * </pre>
 * 
 * @author danialdy
 * @Date 16 Nov 2024 9:06:04 pm
 */
public class DefaultWebhookEngine implements IWebhookEngine {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultWebhookEngine.class.getName());
    private final static int PAGE_SIZE = 50;
    private volatile AtomicInteger consumerId = new AtomicInteger(-1);

    public DefaultWebhookEngine() {
        // initialize and prepare persistence if needed
        LOGGER.info("Initializing persistence...");
        initializePersistence();
        // load all defined & persisted publishers
        // load all consumer on the publishers
        LOGGER.info("Loading existing publishers & consumers...");
        loadPublishersAndConsumers();
    }

    private void initializePersistence() {
        IPersistenceInitializer initializer = IPersistenceInitializer.createInitializerInstance();
        // only need do initialization when use RDBMS
        if (initializer != null) {
            initializer.initialize();
        }
    }

    private void loadPublishersAndConsumers() {
        IWebhookRegistry r = IWebhookRegistry.getRegistryInstance();
        if (r == null) {
            return;
        }
        int page = 0;
        CompletableFuture<List<IWebhookPublisher>> future = r.findPublishers(page, PAGE_SIZE);
        try {
            List<IWebhookPublisher> publishers = future.get();
            while (publishers.size() > 0) {
                for (IWebhookPublisher publisher : publishers) {
                    loadPublishersAndConsumers(publisher);
                }
                // if result-set size less than page size, break directly to reduce 1 registry
                // invocation
                if (publishers.size() < PAGE_SIZE) {
                    break;
                }
                // find next page until no more publishers return
                page++;
                future = r.findPublishers(page, PAGE_SIZE);
                publishers = future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20002, e);
        }
    }

    private void loadPublishersAndConsumers(IWebhookPublisher publisher) throws InterruptedException, ExecutionException {
        LOGGER.debug(String.format("Loading publisher %s (%s)...", publisher.getOrganizationName(), publisher.getOrganizationId()));
        Set<String> dataGroupSet = getDataGroups(publisher);
        if (dataGroupSet.size() == 0) {
            loadPublishersAndConsumers(publisher, null);
        } else {
            for (String dataGroup : dataGroupSet) {
                loadPublishersAndConsumers(publisher, dataGroup);
            }
        }
    }

    private Set<String> getDataGroups(IWebhookPublisher publisher) throws InterruptedException, ExecutionException {
        Set<String> dataGroupSet = new LinkedHashSet<>();
        IWebhookRegistry r = IWebhookRegistry.getRegistryInstance();
        if (r == null) {
            return dataGroupSet;
        }
        int page = 0;
        CompletableFuture<List<String>> future = r.findDataGroups(publisher.getOrganizationId(), page, PAGE_SIZE);
        List<String> dataGroups = future.get();
        while (dataGroups.size() > 0) {
            for (String dataGroup : dataGroups) {
                dataGroupSet.add(dataGroup);
            }
            if (dataGroups.size() < PAGE_SIZE) {
                break;
            }
            page++;
            future = r.findDataGroups(publisher.getOrganizationId(), page, PAGE_SIZE);
            dataGroups = future.get();
        }
        return dataGroupSet;
    }

    private void loadPublishersAndConsumers(IWebhookPublisher publisher, String dataGroup) throws InterruptedException, ExecutionException {
        IWebhookRegistry r = IWebhookRegistry.getRegistryInstance();
        if (r == null) {
            return;
        }
        int page = 0;
        CompletableFuture<List<IWebhookEvent>> future = r.findWebhooks(publisher.getOrganizationId(), page, PAGE_SIZE);
        List<IWebhookEvent> events = future.get();
        while (events.size() > 0) {
            for (IWebhookEvent event : events) {
                loadConsumer(event, dataGroup);
            }
            if (events.size() < PAGE_SIZE) {
                break;
            }
            page++;
            future = r.findWebhooks(publisher.getOrganizationId(), page, PAGE_SIZE);
            events = future.get();
        }
    }

    public void loadConsumer(IWebhookEvent event, String dataGroup) {
        // queue name
        String queueName = getQueueName(event.getPublisherId(), event.getVersion(), dataGroup);
        // message service factory
        IMessageServiceFactory<DefaultWebhookMessage> f = IMessageServiceFactory.getFactoryInstance();
        // message consumer
        IMessageConsumer<DefaultWebhookMessage> consumer = f.createMessageConsumer();
        // message listener (use proxy to AOP logging)
        DefaultWebhookEventListener listenerImpl = new DefaultWebhookEventListener();
        @SuppressWarnings("unchecked")
        IMessageListener<DefaultWebhookMessage> listener = (IMessageListener<DefaultWebhookMessage>) WebhookLoggingHandler.createProxy(listenerImpl);
        // consume message from message service
        consumer.consumerId("WEBHOOK-CONSUMER-" + this.consumerId.incrementAndGet()).queueName(queueName).addMessageListener(listener)
                .consume(DefaultWebhookMessage.class);
    }

    @Override
    public String getVersion() {
        IConfiguration configuraiton = ConfigFactory.getInstance().getConfiguration();
        if (configuraiton.containsKey(LettuceConfiguration.WEBHOOK_ENGINE_VERSION)) {
            return configuraiton.getString(LettuceConfiguration.WEBHOOK_ENGINE_VERSION);
        }
        return "";
    }

    @Override
    public CompletableFuture<Void> post(IWebhookEvent event, String dataGroup, String requestId, String payload) {
        return CompletableFuture.runAsync(() -> {
            // message service factory
            IMessageServiceFactory<DefaultWebhookMessage> f = IMessageServiceFactory.getFactoryInstance();
            // message publisher
            IMessagePublisher<DefaultWebhookMessage> publisher = f.createMessagePublisher();
            // construct message
            DefaultWebhookMessage message = new DefaultWebhookMessage().publisherId(event.getPublisherId()).version(event.getVersion())
                    .eventType(event.getEventType()).eventTag(event.getEventTag()).contentType(event.getContentType()).charset(event.getCharset())
                    .dataGroup(dataGroup).requestId(requestId).payload(payload);
            // publish to message service
            publisher.queueName(getQueueName(event.getPublisherId(), event.getVersion(), dataGroup)).publish(message);
        });
    }

    private String getQueueName(String publisherId, String version, String dataGroup) {
        return String.format("%s#R#K#%s#R#K#%s", publisherId, version, dataGroup == null ? "NULL" : dataGroup);
    }
}
