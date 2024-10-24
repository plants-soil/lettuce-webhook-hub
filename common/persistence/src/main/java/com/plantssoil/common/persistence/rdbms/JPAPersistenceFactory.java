package com.plantssoil.common.persistence.rdbms;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.configuration.Configuration;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;

/**
 * 
 * Manage connection pool (via entity manager factory)<br/>
 * Create persistence instance, the implementation is defined via configuration
 * {@link LettuceConfiguration.RDBMS_JPA_PERSISTENCE_CONFIGURABLE}
 * 
 * @author danialdy
 *
 */
public class JPAPersistenceFactory implements IPersistenceFactory {
    private String persistenceUnitName;
    private String datasourceName;
    private DatabaseConnectionConfig databaseConnectionConfig;
    private volatile EntityManagerFactory entityManagerFactory;

    public JPAPersistenceFactory() {
    }

    @Override
    public IPersistence create() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        JPAPersistence persistence = new JPAPersistence();
        persistence.setEntityManager(em);
        return persistence;
    }

    @Override
    public void close() throws Exception {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    /**
     * Set datasource JNDI name (when deployed in container)<br/>
     * Database connection will be decided by precedence:
     * <ul>
     * <li>Datasource name specified by calling this method</li>
     * <li>Database connection config specified by calling
     * {@link JPAPersistenceFactory#setDatabaseConnectionConfig(DatabaseConnectionConfig)}</li>
     * <li>Engine datasource name specified in lettuce.properties</li>
     * <li>Engine database connection configurations speicified in
     * lettuce.properties</li>
     * </ul>
     * 
     * @param datasourceName datasource JNDI name
     */
    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    /**
     * Set database connection config<br/>
     * Database connection will be decided by precedence:
     * <ul>
     * <li>Datasource name specified by calling
     * {@link JPAPersistenceFactory#setDatasourceName(String)}</li>
     * <li>Database connection config specified by calling this method</li>
     * <li>Engine datasource name specified in lettuce.properties</li>
     * <li>Engine database connection configurations speicified in
     * lettuce.properties</li>
     * </ul>
     * 
     * @param databaseConnectionConfig the database connection config object
     */
    public void setDatabaseConnectionConfig(DatabaseConnectionConfig databaseConnectionConfig) {
        this.databaseConnectionConfig = databaseConnectionConfig;
    }

    /**
     * set JPA persistence unit name, which is defined in JPA persistence.xml
     * 
     * @param persistenceUnitName persistence unit name
     */
    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    private EntityManagerFactory getEntityManagerFactory() {
        if (this.entityManagerFactory == null) {
            synchronized (this) {
                if (this.entityManagerFactory == null) {
                    Map<String, String> connectionProperties = getConnectionProperties();
                    this.entityManagerFactory = Persistence.createEntityManagerFactory(getPersistenceUnitName(), connectionProperties);
                }
            }
        }
        return this.entityManagerFactory;
    }

    private String getPersistenceUnitName() {
        if (this.persistenceUnitName == null) {
            return "lettuce-persistence-unit";
        } else {
            return this.persistenceUnitName;
        }
    }

    private Map<String, String> getConnectionProperties() {
        if (this.datasourceName != null) {
            return getDataSourceProperties(this.datasourceName);
        }

        if (this.databaseConnectionConfig != null) {
            return getDataConnectionProperties(this.databaseConnectionConfig);
        }

        Configuration configuration = ConfigFactory.getInstance().getConfiguration();
        if (configuration.containsKey(LettuceConfiguration.ENGINE_CORE_DATASOURCE)) {
            return getDataSourceProperties(configuration.getString(LettuceConfiguration.ENGINE_CORE_DATASOURCE));
        } else {
            return getDataConnectionProperties(getEngineDatabaseConnectionConfig());
        }
    }

    private Map<String, String> getDataSourceProperties(String datasourceName) {
        Map<String, String> properties = new HashMap<>();
        properties.put("jta-data-source", datasourceName);

//        <property name="hibernate.connection.driver_class">com.mysql.jdbc.Driver</property>
//        properties.put("hibernate.connection.driver_class", org.h2.Driver.class.getName());
//        <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
//        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//        <property name="hibernate.connection.datasource">java:comp/env/jdbc/MyLocalDB</property>
        properties.put("hibernate.connection.datasource", datasourceName); // Hibernate can't load the datasource without this line
//        <property name="hibernate.current_session_context_class">thread</property>
        properties.put("hibernate.current_session_context_class", "thread");
        return properties;
    }

    private Map<String, String> getDataConnectionProperties(DatabaseConnectionConfig databaseConfig) {
        Map<String, String> properties = new HashMap<>();
        if (databaseConfig.getDatabaseDriver() != null) {
            properties.put("javax.persistence.jdbc.driver", databaseConfig.getDatabaseDriver());
        }
        if (databaseConfig.getConnectionUrl() != null) {
            properties.put("javax.persistence.jdbc.url", databaseConfig.getConnectionUrl());
        }
        if (databaseConfig.getUserName() != null) {
            properties.put("javax.persistence.jdbc.user", "sa");
        }
        if (databaseConfig.getPassword() != null) {
            properties.put("javax.persistence.jdbc.password", "sa");
        }
        hibernateSettings(properties, databaseConfig.getConnectionPoolSize(), databaseConfig.isShowSql());

        return properties;
    }

    private DatabaseConnectionConfig getEngineDatabaseConnectionConfig() {
        Configuration conf = ConfigFactory.getInstance().getConfiguration();
        String driver = conf.getString(LettuceConfiguration.ENGINE_CORE_DATABASE_DRIVER);
        String url = conf.getString(LettuceConfiguration.ENGINE_CORE_DATABASE_URL);
        String username = conf.getString(LettuceConfiguration.ENGINE_CORE_DATABASE_USERNAME);
        String password = conf.getString(LettuceConfiguration.ENGINE_CORE_DATABASE_PASSWORD);
        int poolsize = 20;
        boolean showsql = false;
        if (conf.containsKey(LettuceConfiguration.ENGINE_CORE_DATABASE_POOLSIZE)) {
            poolsize = conf.getInt(LettuceConfiguration.ENGINE_CORE_DATABASE_POOLSIZE);
        }
        if (conf.containsKey(LettuceConfiguration.ENGINE_CORE_DATABASE_SHOWSQL)) {
            showsql = conf.getBoolean(LettuceConfiguration.ENGINE_CORE_DATABASE_SHOWSQL);
        }
        DatabaseConnectionConfig dbconfig = new DatabaseConnectionConfig(driver, url, username, password);
        dbconfig.setConnectionPoolSize(poolsize);
        dbconfig.setShowSql(showsql);
        return dbconfig;
    }

    private void hibernateSettings(Map<String, String> properties, int poolSize, boolean showsql) {
//        <!-- c3p0 config http://www.hibernate.org/214.html -->
//        <property name="connection.provider_class">org.hibernate.connection.C3P0ConnectionProvider</property>        
        properties.put("connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
//      <property name="hibernate.c3p0.acquire_increment">1</property>
        properties.put("hibernate.c3p0.acquire_increment", "1");
//      <property name="hibernate.c3p0.idle_test_period">60</property>
        properties.put("hibernate.c3p0.idle_test_period", "60");
//      <property name="hibernate.c3p0.min_size">1</property>
        properties.put("hibernate.c3p0.min_size", "1");
//      <property name="hibernate.c3p0.max_size">2</property>
        properties.put("hibernate.c3p0.max_size", String.valueOf(poolSize));
//      <property name="hibernate.c3p0.max_statements">50</property>
        properties.put("hibernate.c3p0.max_statements", "50");
//      <property name="hibernate.c3p0.timeout">0</property>
        properties.put("hibernate.c3p0.timeout", "0");
//      <property name="hibernate.c3p0.acquireRetryAttempts">1</property>
        properties.put("hibernate.c3p0.acquireRetryAttempts", "1");
//      <property name="hibernate.c3p0.acquireRetryDelay">250</property>
        properties.put("hibernate.c3p0.acquireRetryDelay", "250");
//      <property name="hibernate.use_sql_comments">true</property>
        properties.put("hibernate.use_sql_comments", "true");
//      <property name="hibernate.transaction.factory_class">org.hibernate.transaction.JDBCTransactionFactory</property>
        properties.put("hibernate.transaction.factory_class", ">org.hibernate.transaction.JDBCTransactionFactory");
//      <property name="hibernate.current_session_context_class">thread</property>
        properties.put("hibernate.current_session_context_class", "thread");
//      <property name="hibernate.show_sql">true</property>
        properties.put("hibernate.show_sql", String.valueOf(showsql));
    }

}
