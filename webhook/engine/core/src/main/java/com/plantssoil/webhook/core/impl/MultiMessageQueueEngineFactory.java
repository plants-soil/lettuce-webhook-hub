package com.plantssoil.webhook.core.impl;

import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;

/**
 * The EngineFactory which implements base on MessageQueue technical<br/>
 * Should avoid create this factory by it's constructor directly, if you can't
 * ensure it's the unique instance in the JVM<br/>
 * 
 * @see IEngineFactory#getFactoryInstance()
 * 
 * @author danialdy
 * @Date 4 Dec 2024 3:36:34 pm
 */
public class MultiMessageQueueEngineFactory extends AbstractEngineFactory {

    @Override
    IEngine createEngineInstance() {
        return new MultiMessageQueueEngine();
    }

}
