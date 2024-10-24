package com.plantssoil.common.persistence;

import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;

/**
 * The factory which could produce IPersistence
 */
public interface IPersistenceFactory extends IConfigurable, AutoCloseable {

    /**
     * Factory method to produce persistence instance
     * 
     * @return IPersistence instance to persist entities
     */
    public IPersistence create();

    /**
     * Get default implementation of this factory
     * 
     * @return default implementation
     */
    public static IPersistenceFactory getDefaultFactory() {
        return (IPersistenceFactory) ConfigurableLoader.getInstance().getSingleton(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE);
    }
}
