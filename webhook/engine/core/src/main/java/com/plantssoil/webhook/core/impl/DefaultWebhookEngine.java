package com.plantssoil.webhook.core.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.IPersistenceInitializer;
import com.plantssoil.webhook.core.IWebhookEngine;
import com.plantssoil.webhook.core.IWebhookEngineFactory;
import com.plantssoil.webhook.core.IWebhookEvent;
import com.plantssoil.webhook.core.IWebhookPublisher;
import com.plantssoil.webhook.core.IWebhookRegistry;
import com.plantssoil.webhook.core.exception.EngineException;

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
        IWebhookRegistry registry = getRegistry();
        if (registry == null) {
            return;
        }
        int page = 0;
        int pageSize = 50;
        CompletableFuture<List<IWebhookPublisher>> future = registry.findPublishers(page, pageSize);
        try {
            List<IWebhookPublisher> publishers = future.get();
            while (publishers.size() > 0) {
                for (IWebhookPublisher publisher : publishers) {

                }
                // if result-set size less than page size, break directly to reduce 1 registry
                // invocation
                if (publishers.size() < pageSize) {
                    break;
                }
                // find next page until no more publishers return
                page++;
                future = registry.findPublishers(page, pageSize);
                publishers = future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20002, e);
        }
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
    public IWebhookRegistry getRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<Void> publish(IWebhookEvent event, String requestId, String payload) {
        // TODO Auto-generated method stub
        return null;
    }

}
