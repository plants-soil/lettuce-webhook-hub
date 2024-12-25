package com.plantssoil.webhook.core;

/**
 * The webhook engine, the core of webhook<br/>
 * Could get IRegistry to manage publishers & subscribers, subscriber could also
 * subscribe events from specific publisher, so subscriber could receive events
 * from publisher via webhook URL after that.<br/>
 * Publisher could trigger events, subscribers could receive the events if they
 * subscribed the events<br/>
 * Could use IEngineFactory to get webhook engine instance: e.g:
 * 
 * <pre>
 * <code>
 *   IEngineFactory factory = IEngineFactory.getFactoryInstance();
 *   IEngine engine = factory.getEngine();
 *   ...
 * </code>
 * </pre>
 * 
 * @author danialdy
 * @Date 12 Nov 2024 10:53:36 am
 */
public interface IEngine {
    /**
     * Get the version of current webhook engine
     * 
     * @return webhook engine version
     */
    public String getVersion();

    /**
     * Get the registry of current webhook engine<br/>
     * 
     * @return Registry of publisher & subscribe, by which could manage publisher &
     *         subscribe and the event subscription
     * @see IRegistry
     */
    public IRegistry getRegistry();

    /**
     * Publisher trigger event with payload, the subscribers who subscribed will
     * receive this event via webhook URL post method.
     * 
     * @param message the message to trigger, which includes publisherId / version /
     *                data group / request id / payload / event related / etc.
     * 
     */
    public void trigger(Message message);
}
