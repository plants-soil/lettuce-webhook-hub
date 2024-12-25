package com.plantssoil.webhook.core.impl;

import java.util.concurrent.SubmissionPublisher;

import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.Message;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * The in-memory implementation of webhook engine<br/>
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
 * @Date 16 Nov 2024 9:06:04 pm
 */
class InMemoryEngine extends AbstractEngine implements IEngine {
    public InMemoryEngine() {
        super();
    }

    @Override
    IRegistry createRegistryInstance() {
        return new InMemoryRegistry();
    }

    @Override
    public void trigger(Message message) {
        SubmissionPublisher<Message> publisher = ((InMemoryRegistry) getRegistry()).getPublisher(message.getPublisherId(), message.getVersion(),
                message.getDataGroup());
        if (publisher == null) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20002,
                    String.format("Can't found the publisher (publisherId: %s, version: %s, dataGroup: %s)", message.getPublisherId(), message.getVersion(),
                            message.getDataGroup() == null ? "NULL" : message.getDataGroup()));
        }
        // don't need publish the message if there is no subscriber
        // otherwise publisher.submit() will block after the messages exceed the maximum
        // capacity
        if (publisher.getSubscribers().size() <= 0) {
            return;
        }
        // publish the message
        publisher.submit(message);
    }
}
