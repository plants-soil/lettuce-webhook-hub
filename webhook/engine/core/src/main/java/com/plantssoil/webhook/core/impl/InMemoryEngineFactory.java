package com.plantssoil.webhook.core.impl;

import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;

/**
 * The in-memory implementation of Webhook Engine Factory.<br/>
 * Should avoid create this factory by it's constructor directly, if you can't
 * ensure it's the unique instance in the JVM<br/>
 * 
 * @see IEngineFactory#getFactoryInstance()
 * 
 * @author danialdy
 * @Date 15 Nov 2024 3:26:33 pm
 */
public class InMemoryEngineFactory extends AbstractEngineFactory {

    @Override
    IEngine createEngineInstance() {
        LOGGER.info("Creating webhook engine: " + InMemoryEngine.class.getName());
        return new InMemoryEngine();
    }

}
