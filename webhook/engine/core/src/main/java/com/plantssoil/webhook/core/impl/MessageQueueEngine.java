package com.plantssoil.webhook.core.impl;

import com.plantssoil.common.mq.IMessagePublisher;
import com.plantssoil.common.mq.IMessageServiceFactory;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.Message;

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
public class MessageQueueEngine extends AbstractEngine {

    public MessageQueueEngine() {
        super();
    }

    @Override
    IRegistry createRegistryInstance() {
        return new MessageQueueRegistry();
    }

    @Override
    public void trigger(Message message) {
        // message service factory
        IMessageServiceFactory<Message> f = IMessageServiceFactory.getFactoryInstance();
        // message publisher
        IMessagePublisher<Message> publisher = f.createMessagePublisher();
        // publish to message service
        publisher.queueName(getQueueName(message.getPublisherId(), message.getVersion(), message.getDataGroup())).publish(message);
    }

    private String getQueueName(String publisherId, String version, String dataGroup) {
        return String.format("%s#R#K#%s#R#K#%s", publisherId, version, dataGroup == null ? "NULL" : dataGroup);
    }

}
