package com.plantssoil.webhook.core.impl;

import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.Message;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * The implementation of webhook engine base on message queue <br/>
 * Could get this webhook engine instance via {@link IEngineFactory}<br/>
 * e.g:
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
 * @Date 5 Dec 2024 3:40:29 pm
 */
class MultiMessageQueueEngine extends AbstractEngine implements IEngine {

    public MultiMessageQueueEngine() {
        super();
    }

    @Override
    IRegistry createRegistryInstance() {
        return new MultiMessageQueueRegistry();
    }

    @Override
    public void trigger(Message message) {
        IRegistry registry = getRegistry();
        IPublisher publisher = registry.findPublisher(message.getPublisherId());
        if (publisher == null) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20004,
                    String.format("The publisher (%s) does not register yet!", message.getPublisherId()));
        }

        // message service factory
        IMessageServiceFactory<Message> f = IMessageServiceFactory.getFactoryInstance();
        // message publisher
        try (IMessagePublisher<Message> messagePublisher = f.createMessagePublisher()) {
            // publish to message service
            messagePublisher.channelName(getQueueName(message.getPublisherId(), message.getVersion(), message.getDataGroup())).publish(message);
        } catch (Exception e) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20006, e);
        }
    }

    private String getQueueName(String publisherId, String version, String dataGroup) {
        return String.format("%s#R#K#%s#R#K#%s", publisherId, version, dataGroup == null ? "NULL" : dataGroup);
    }

}
