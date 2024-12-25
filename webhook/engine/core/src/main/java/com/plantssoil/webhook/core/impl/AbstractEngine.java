package com.plantssoil.webhook.core.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;

/**
 * The abstract engine, all engine should be subclass of this engine
 * 
 * @author danialdy
 * @Date 4 Dec 2024 3:25:58 pm
 */
abstract class AbstractEngine {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractEngine.class.getName());
    private IRegistry registry;
    final static int PAGE_SIZE = 50;

    public AbstractEngine() {
        super();
        // load all defined & persisted publishers
        // load all consumer on the publishers
        LOGGER.info("Loading existing publishers & consumers...");
        loadPublishersAndConsumers();
    }

    private void loadPublishersAndConsumers() {
        IRegistry r = this.getRegistry();
        if (r == null) {
            return;
        }
        int page = 0;
        List<IPublisher> publishers = r.findAllPublishers(page, PAGE_SIZE);
        while (publishers.size() > 0) {
            for (IPublisher publisher : publishers) {
                r.addPublisher(publisher);
            }
            // if result-set size less than page size, break directly to reduce 1 registry
            // invocation
            if (publishers.size() < PAGE_SIZE) {
                break;
            }
            // find next page until no more publishers return
            page++;
            publishers = r.findAllPublishers(page, PAGE_SIZE);
        }
    }

    public String getVersion() {
        IConfiguration configuraiton = ConfigFactory.getInstance().getConfiguration();
        if (configuraiton.containsKey(LettuceConfiguration.WEBHOOK_ENGINE_VERSION)) {
            return configuraiton.getString(LettuceConfiguration.WEBHOOK_ENGINE_VERSION);
        }
        return "";
    }

    public IRegistry getRegistry() {
        if (this.registry == null) {
            synchronized (this) {
                if (this.registry == null) {
                    this.registry = createRegistryInstance();
                }
            }
        }
        return this.registry;
    }

    /**
     * Create registry instance
     * 
     * @return registry instance
     */
    abstract IRegistry createRegistryInstance();

}
