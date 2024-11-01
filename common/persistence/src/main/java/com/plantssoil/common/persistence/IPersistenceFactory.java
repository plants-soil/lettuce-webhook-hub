package com.plantssoil.common.persistence;

import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.exception.PersistenceException;

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
            String err = String.format("The class %s don't implements %s!", configurable.getClass().getName(), IPersistenceFactory.class.getName());
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13001, err);
        }
    }
}
