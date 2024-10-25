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
     * Get default implementation of this factory (which is singleton)
     * 
     * @return default implementation singleton
     */
    public static IPersistenceFactory getDefaultFactory() {
        IConfigurable configurable = ConfigurableLoader.getInstance().createSingleton(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE);
        if (configurable instanceof IPersistenceFactory) {
            return (IPersistenceFactory) configurable;
        } else {
            String err = String.format("The class %s don't implements com.plantssoil.common.persistence.IPersistenceFactory!",
                    configurable.getClass().getName());
            throw new RuntimeException(err);
        }
    }
}
