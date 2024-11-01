package com.plantssoil.common.persistence;

import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.exception.PersistenceException;

/**
 * Initializer<br/>
 * For example: Will execute all DDL to create tables, indexes, constraints,
 * etc. when use RDBMS<br/>
 * For example: Will create indexes or other essential components when use
 * NOSQL<br/>
 * Will initialize all basic data which used by system functions.<br/>
 * 
 * @author danialdy
 *
 */
public interface IInitializer extends IConfigurable {
    /**
     * Do the initialization<br/>
     * 
     */
    public void initialize();

    /**
     * create default instance of IInitializer
     * 
     * @return new instance of IInitializer
     */
    public static IInitializer createDefaultInitializer() {
        IConfigurable configurable = ConfigurableLoader.getInstance().createConfigurable(LettuceConfiguration.RDBMS_INIT_DDL_CONFIGURABLE);
        if (configurable instanceof IInitializer) {
            return (IInitializer) configurable;
        } else {
            String err = String.format("The class %s don't implements %s!", configurable.getClass().getName(), IInitializer.class.getName());
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13002, err);
        }
    }
}
