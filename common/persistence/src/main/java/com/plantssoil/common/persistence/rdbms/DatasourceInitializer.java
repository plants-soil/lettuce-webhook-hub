package com.plantssoil.common.persistence.rdbms;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.LettuceConfiguration;

/**
 * Initialize database base on datasourceName context, generally used in web
 * server deployment<br/>
 * And should have database datasource JNDI naming in web server
 * 
 * @author danialdy
 *
 */
public class DatasourceInitializer extends AbstractLiquibaseInitializer {
    private String datasourceName;

    public DatasourceInitializer() {
        Configuration config = ConfigFactory.getInstance().getConfiguration();
        if (config.containsKey(LettuceConfiguration.ENGINE_CORE_DATASOURCE)) {
            this.datasourceName = config.getString(LettuceConfiguration.ENGINE_CORE_DATASOURCE);
        }
    }

    public DatasourceInitializer(String datasourceName) {
        super();
        this.datasourceName = datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    @Override
    protected Connection getConnection() {
        if (this.datasourceName == null || this.datasourceName.strip().length() == 0) {
            throw new RuntimeException("The datasource name for database connection can't be null!");
        }
        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup(this.datasourceName);
            return dataSource.getConnection();
        } catch (NamingException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
