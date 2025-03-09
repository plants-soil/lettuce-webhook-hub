package com.plantssoil.webhook.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;

/**
 * The abstract engine factory, all implementations should be subclass of this
 * factory
 * 
 * @author danialdy
 * @Date 4 Dec 2024 3:23:25 pm
 */
abstract class AbstractEngineFactory implements IConfigurable, IEngineFactory {
    final static Logger LOGGER = LoggerFactory.getLogger(AbstractEngineFactory.class.getName());
    private IEngine engine;

    /**
     * Constructor, create the IEngine proxy
     */
    public AbstractEngineFactory() {
        IEngine engineImpl = createEngineInstance();
        this.engine = engineImpl;
    }

    /**
     * Create the instance of engine implementation
     * 
     * @return engine instance
     */
    abstract IEngine createEngineInstance();

    @Override
    public IEngine getEngine() {
        return this.engine;
    }

}
