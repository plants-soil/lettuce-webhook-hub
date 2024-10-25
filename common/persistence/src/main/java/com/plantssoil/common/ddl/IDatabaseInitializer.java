package com.plantssoil.common.ddl;

import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;

/**
 * RDBMS Initializer<br/>
 * Will execute all DDL to create tables, indexes, constraints, etc.<br/>
 * Will initialize all basic data which used by system functions.<br/>
 * 
 * @author danialdy
 *
 */
public interface IDatabaseInitializer extends IConfigurable {
    /**
     * Do the initialization<br/>
     * 
     */
    public void initialize();

    /**
     * create default instance of IDatabaseInitializer
     * 
     * @return new instance of IDatabaseInitializer
     */
    public static IDatabaseInitializer createDefaultInitializer() {
        IConfigurable configurable = ConfigurableLoader.getInstance().createConfigurable(LettuceConfiguration.RDBMS_INIT_DDL_CONFIGURABLE);
        if (configurable instanceof IDatabaseInitializer) {
            return (IDatabaseInitializer) configurable;
        } else {
            String err = String.format("The class %s don't implements com.plantssoil.common.ddl.IDatabaseInitializer!", configurable.getClass().getName());
            throw new RuntimeException(err);
        }
    }
}
