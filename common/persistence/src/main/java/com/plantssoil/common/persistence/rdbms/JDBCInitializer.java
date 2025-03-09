package com.plantssoil.common.persistence.rdbms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.exception.PersistenceException;

/**
 * Initialize database base on (driver class, connection url, userName,
 * password), generally used in standalone environment
 * 
 * @author danialdy
 *
 */
public class JDBCInitializer extends AbstractLiquibaseInitializer {
    private String driverClass;
    private String connectionUrl;
    private String userName;
    private String password;

    public JDBCInitializer() {
        IConfiguration config = ConfigFactory.getInstance().getConfiguration();
        if (config.containsKey(LettuceConfiguration.RDBMS_DATABASE_DRIVER)) {
            this.driverClass = config.getString(LettuceConfiguration.RDBMS_DATABASE_DRIVER);
        }
        if (config.containsKey(LettuceConfiguration.PERSISTENCE_DATABASE_URL)) {
            this.connectionUrl = config.getString(LettuceConfiguration.PERSISTENCE_DATABASE_URL);
        }
        if (config.containsKey(LettuceConfiguration.PERSISTENCE_DATABASE_USERNAME)) {
            this.userName = config.getString(LettuceConfiguration.PERSISTENCE_DATABASE_USERNAME);
        }
        if (config.containsKey(LettuceConfiguration.PERSISTENCE_DATABASE_PASSWORD)) {
            this.password = config.getString(LettuceConfiguration.PERSISTENCE_DATABASE_PASSWORD);
        }
    }

    public JDBCInitializer(String driverClass, String connectionUrl, String userName, String password) {
        super();
        this.driverClass = driverClass;
        this.connectionUrl = connectionUrl;
        this.userName = userName;
        this.password = password;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected Connection getConnection() {
        if (this.driverClass == null || this.connectionUrl == null || this.driverClass.strip().length() == 0 || this.connectionUrl.strip().length() == 0
                || this.userName.strip().length() == 0 || this.password.strip().length() == 0) {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13010,
                    "Database connection (driverClass, connectionUrl) can't be null!");
        }
        LOGGER.info("RDBMS is initializing by JDBC with driver: " + this.driverClass);
        try {
            Class.forName(this.driverClass);
            Connection connection = null;
            if (this.userName == null) {
                connection = DriverManager.getConnection(this.connectionUrl);
            } else {
                connection = DriverManager.getConnection(this.connectionUrl, this.userName, this.password);
            }
            connection.setAutoCommit(false);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13011, e);
        } catch (SQLException e) {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13009, e);
        }
    }

}
