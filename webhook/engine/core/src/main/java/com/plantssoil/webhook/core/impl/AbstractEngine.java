package com.plantssoil.webhook.core.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.registry.InMemoryRegistry;

/**
 * The abstract engine, all engine should be subclass of this engine
 * 
 * @author danialdy
 * @Date 4 Dec 2024 3:25:58 pm
 */
public abstract class AbstractEngine {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractEngine.class.getName());
    final static int PAGE_SIZE = 50;
    /**
     * Registry
     */
    private IRegistry registry;

    public AbstractEngine() {
        super();
        // load all defined & persisted publishers
        // load all consumer on the publishers
        LOGGER.info("Loading existing publishers & consumers...");
        loadExistingPublishers();
        loadExistingSubscribers();
    }

    private void loadExistingPublishers() {
        IRegistry r = getRegistry();
        int page = 0;
        List<IPublisher> publishers = r.findAllPublishers(page, PAGE_SIZE);
        while (publishers.size() > 0) {
            for (IPublisher publisher : publishers) {
                loadPublisher(publisher);
            }
            if (publishers.size() < PAGE_SIZE) {
                break;
            }
            page++;
            publishers = r.findAllPublishers(page, PAGE_SIZE);
        }
    }

    private void loadExistingSubscribers() {
        IRegistry r = getRegistry();
        int page = 0;
        List<ISubscriber> subscribers = r.findAllSubscribers(page, PAGE_SIZE);
        while (subscribers.size() > 0) {
            for (ISubscriber subscriber : subscribers) {
                loadSubscriber(subscriber);
            }
            if (subscribers.size() < PAGE_SIZE) {
                break;
            }
            page++;
            subscribers = r.findAllSubscribers(page, PAGE_SIZE);
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
                    String registryImpl = ConfigFactory.getInstance().getConfiguration().getString(LettuceConfiguration.WEBHOOK_ENGINE_REGISTRY_CONFIGURABLE,
                            InMemoryRegistry.class.getName());
                    if (registryImpl.equals(InMemoryRegistry.class.getName())) {
                        this.registry = new InMemoryRegistry();
                    } else {
                        this.registry = (IRegistry) ConfigurableLoader.getInstance()
                                .createConfigurable(LettuceConfiguration.WEBHOOK_ENGINE_REGISTRY_CONFIGURABLE);
                    }
                    ((AbstractRegistry) this.registry).setEngine(this);
                }
            }
        }
        return this.registry;
    }

    /**
     * Load publisher into engine
     * 
     * @param publisher publisher to load
     */
    abstract void loadPublisher(IPublisher publisher);

    /**
     * Re-load publisher of engine, the publisher id should keep same
     * 
     * @param publisher publisher to re-load
     */
    void reloadPublisher(IPublisher publisher) {
        unloadPublisher(publisher);
        loadPublisher(publisher);
    }

    /**
     * Unload publisher from engine
     * 
     * @param publisher the publisher to unload
     */
    abstract void unloadPublisher(IPublisher publisher);

    /**
     * Load subscriber into engine
     * 
     * @param subscriber subscriber to load
     */
    abstract void loadSubscriber(ISubscriber subscriber);

    /**
     * Re-load subscriber of engine, the subscriber id should keep same
     * 
     * @param subscriber subscriber to re-load
     */
    void reloadSubscriber(ISubscriber subscriber) {
        unloadSubscriber(subscriber);
        loadSubscriber(subscriber);
    }

    /**
     * Unload subscriber from engine
     * 
     * @param subscriber the subscriber to unload
     */
    abstract void unloadSubscriber(ISubscriber subscriber);
}
