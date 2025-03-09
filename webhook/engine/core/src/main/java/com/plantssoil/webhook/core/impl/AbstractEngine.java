package com.plantssoil.webhook.core.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.mq.ChannelType;
import com.plantssoil.common.mq.IMessageConsumer;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.ILogging;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.Message;
import com.plantssoil.webhook.core.logging.InMemoryLogging;
import com.plantssoil.webhook.core.registry.InMemoryRegistry;

/**
 * The abstract engine, all engine should be subclass of this engine
 * 
 * @author danialdy
 * @Date 4 Dec 2024 3:25:58 pm
 */
public abstract class AbstractEngine {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractEngine.class.getName());
    final static String REGISTRY_CHANGE_MESSAGE_QUEUE_NAME = "com.plantssoil.mq.registry.channel";
    final static int PAGE_SIZE = 20;
    private IRegistry registry;
    private RegistryChangeListener registryChangeListener = new RegistryChangeListener(this);
    private ILogging logging;

    public AbstractEngine() {
        super();
        // load all defined & persisted publishers
        // load all consumer on the publishers
        LOGGER.info("Loading existing publishers & consumers...");
        loadExistingPublishers();
        loadExistingSubscribers();
        LOGGER.info("Add listener for registry changing...");
        addRegistryChangeListener();
    }

    private void loadExistingPublishers() {
        IRegistry r = getRegistry();
        int page = 0;
        List<IPublisher> publishers = r.findAllPublishers(page, PAGE_SIZE);
        while (publishers.size() > 0) {
            for (IPublisher publisher : publishers) {
                loadPublisher(publisher);
                loadExistingEvents(publisher);
                if (publisher.getSupportDataGroup()) {
                    loadExistingDataGroups(publisher);
                }
            }
            if (publishers.size() < PAGE_SIZE) {
                break;
            }
            page++;
            publishers = r.findAllPublishers(page, PAGE_SIZE);
        }
    }

    private void loadExistingEvents(IPublisher publisher) {
        IRegistry r = getRegistry();
        int page = 0;
        List<IEvent> events = r.findEvents(publisher.getPublisherId(), page, PAGE_SIZE);
        while (events.size() > 0) {
            for (IEvent event : events) {
                loadEvent(publisher, event);
            }
            if (events.size() < PAGE_SIZE) {
                break;
            }
            page++;
            events = r.findEvents(publisher.getPublisherId(), page, PAGE_SIZE);
        }
    }

    private void loadExistingDataGroups(IPublisher publisher) {
        IRegistry r = getRegistry();
        int page = 0;
        List<IDataGroup> dataGroups = r.findDataGroups(publisher.getPublisherId(), page, PAGE_SIZE);
        while (dataGroups.size() > 0) {
            for (IDataGroup dataGroup : dataGroups) {
                loadDataGroup(publisher, dataGroup);
            }
            if (dataGroups.size() < PAGE_SIZE) {
                break;
            }
            page++;
            dataGroups = r.findDataGroups(publisher.getPublisherId(), page, PAGE_SIZE);
        }

    }

    private void loadExistingSubscribers() {
        IRegistry r = getRegistry();
        int page = 0;
        List<ISubscriber> subscribers = r.findAllSubscribers(page, PAGE_SIZE);
        while (subscribers.size() > 0) {
            for (ISubscriber subscriber : subscribers) {
                loadSubscriber(subscriber);
                loadExistingWebhooks(subscriber);
            }
            if (subscribers.size() < PAGE_SIZE) {
                break;
            }
            page++;
            subscribers = r.findAllSubscribers(page, PAGE_SIZE);
        }
    }

    private void loadExistingWebhooks(ISubscriber subscriber) {
        IRegistry r = getRegistry();
        int page = 0;
        List<IWebhook> webhooks = r.findWebhooks(subscriber.getSubscriberId(), page, PAGE_SIZE);
        while (webhooks.size() > 0) {
            for (IWebhook webhook : webhooks) {
                loadWebhook(webhook);
                IPublisher publisher = r.findPublisher(webhook.getPublisherId());
                if (publisher != null && publisher.getSupportDataGroup()) {
                    loadExistingDataGroupsSubscribed(webhook);
                }
                loadExistingEventsSubscribed(webhook);
            }
            if (webhooks.size() < PAGE_SIZE) {
                break;
            }
            page++;
            webhooks = r.findWebhooks(subscriber.getSubscriberId(), page, PAGE_SIZE);
        }
    }

    void loadExistingEventsSubscribed(IWebhook webhook) {
        IRegistry r = getRegistry();
        int page = 0;
        List<IEvent> events = r.findSubscribedEvents(webhook.getWebhookId(), page, PAGE_SIZE);
        while (events.size() > 0) {
            loadSubscribedEvent(webhook, events);
            if (events.size() < PAGE_SIZE) {
                break;
            }
            page++;
            events = r.findSubscribedEvents(webhook.getWebhookId(), page, PAGE_SIZE);
        }
    }

    void loadExistingDataGroupsSubscribed(IWebhook webhook) {
        IRegistry r = getRegistry();
        int page = 0;
        List<IDataGroup> dataGroups = r.findSubscribedDataGroups(webhook.getWebhookId(), page, PAGE_SIZE);
        while (dataGroups.size() > 0) {
            for (IDataGroup dataGroup : dataGroups) {
                loadSubscribedDataGroup(webhook, dataGroup);
            }
            if (dataGroups.size() < PAGE_SIZE) {
                break;
            }
            page++;
            dataGroups = r.findSubscribedDataGroups(webhook.getWebhookId(), page, PAGE_SIZE);
        }
    }

    private void addRegistryChangeListener() {
        // message service factory
        IMessageServiceFactory<RegistryChangeMessage> f = IMessageServiceFactory
                .getFactoryInstance(com.plantssoil.common.mq.simple.MessageServiceFactory.class);
        // message consumer
        IMessageConsumer<RegistryChangeMessage> consumer = f.createMessageConsumer().consumerId("REGISTRY-CHANGE-CONSUMER")
                .channelName(REGISTRY_CHANGE_MESSAGE_QUEUE_NAME).channelType(ChannelType.TOPIC).addMessageListener(this.registryChangeListener);
        // consume message from message service
        consumer.consume(RegistryChangeMessage.class);
    }

    /**
     * Trigger message from the publisher
     * 
     * @param message The message to trigger
     */
    protected abstract void triggerMessage(Message message);

    /**
     * Trigger message (will logging the message if necessary)
     * 
     * @param message The message to trigger
     */
    public void trigger(Message message) {
        getLogging().triggerMessage(message);
        triggerMessage(message);
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
            synchronized (IRegistry.class) {
                if (this.registry == null) {
                    this.registry = (IRegistry) ConfigurableLoader.getInstance().createConfigurable(LettuceConfiguration.WEBHOOK_ENGINE_REGISTRY_CONFIGURABLE,
                            InMemoryRegistry.class.getName());
                    LOGGER.info("Webhook engine registry service loaded: " + this.registry.getClass().getName());
                }
            }
        }
        return this.registry;
    }

    public ILogging getLogging() {
        if (this.logging == null) {
            synchronized (ILogging.class) {
                if (this.logging == null) {
                    this.logging = (ILogging) ConfigurableLoader.getInstance().createSingleton(LettuceConfiguration.WEBHOOK_ENGINE_LOGGING_CONFIGURABLE,
                            InMemoryLogging.class.getName());
                    LOGGER.info("Webhook engine logging service loaded: " + this.logging.getClass().getName());
                }
            }
        }
        return this.logging;
    }

    /**
     * Load publisher into engine, should update publisher if already loaded
     * 
     * @param publisher publisher to load
     */
    abstract void loadPublisher(IPublisher publisher);

    /**
     * Load event into engine, should update event if already loaded
     * 
     * @param publisher the publisher which event belongs to
     * @param event     the event to load
     */
    abstract void loadEvent(IPublisher publisher, IEvent event);

    /**
     * Load data group into engine, should update data group if already loaded
     * 
     * @param publisher the publisher which data group belongs to
     * @param dataGroup the data group to load
     */
    abstract void loadDataGroup(IPublisher publisher, IDataGroup dataGroup);

    /**
     * Load subscriber into engine, should update subscriber if already loaded
     * 
     * @param subscriber subscriber to load
     */
    abstract void loadSubscriber(ISubscriber subscriber);

    /**
     * Unload subscriber from engine, including belongs (webhooks, events
     * subscribed, datagroups subscribed, etc.)
     * 
     * @param subscriber the subscriber to unload
     */
    abstract void unloadSubscriber(ISubscriber subscriber);

    /**
     * Load webhook into webhook engine, should update webhook if already loaded
     * 
     * @param webhook the webhook to load
     */
    abstract void loadWebhook(IWebhook webhook);

    /**
     * Unload webhook from engine, including belongs (events subscribed, datagroups
     * subscribed, etc.)
     * 
     * @param webhook the webhook to unload
     */
    abstract void unloadWebhook(IWebhook webhook);

    /**
     * Load subscribed event into webhook engine
     * 
     * @param webhook the webhook which the event belongs to
     * @param event   the event to load
     */
    abstract void loadSubscribedEvent(IWebhook webhook, List<IEvent> events);

    /**
     * Unload event subscribed from engine
     * 
     * @param webhook the webhook which event belongs to
     * @param events  the events to unload
     */
    abstract void unloadSubscribedEvent(IWebhook webhook, List<IEvent> events);

    /**
     * Load subscribed data group into webhook engine
     * 
     * @param webhook   the webhook which the data group belongs to
     * @param dataGroup the data group to load
     */
    abstract void loadSubscribedDataGroup(IWebhook webhook, IDataGroup dataGroup);

    /**
     * Unload data group subscribed from webhook engine
     * 
     * @param webhook   the webhook which data group belongs to
     * @param dataGroup the data group to unload
     */
    abstract void unloadSubscribedDataGroup(IWebhook webhook, IDataGroup dataGroup);
}
