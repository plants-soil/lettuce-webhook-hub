package com.plantssoil.common.config;

/**
 * All configuration item names which contained in lettuce.properties
 * 
 * @author danialdy
 *
 */
public class LettuceConfiguration {
    /**
     * OS env name - lettuce home
     */
    public final static String LETTUCE_HOME_ENV_NAME = "LETTUCE_HOME";

    /**
     * OS env name - jboss home
     */
    public final static String JBOSS_HOME_ENV_NAME = "JBOSS_HOME";

    /**
     * OS env name - catalina home
     */
    public final static String CATALINA_HOME_ENV_NAME = "CATALINA_HOME";

    /**
     * System property name - lettuce configuration directory
     */
    public final static String CONF_DIRECTORY_PROPERTY_NAME = "lettuce.config.dir";

    /**
     * System property name - lettuce data directory
     */
    public final static String DATA_DIRECTORY_PROPERTY_NAME = "lettuce.data.dir";

    /**
     * System property name - keystore type
     */
    public final static String KEYSTORE_TYPE_PROPERTY_NAME = "lettuce.keystore.type";

    /**
     * System property name - keystore file name
     */
    public final static String KEYSTORE_FILENAME_PROPERTY_NAME = "lettuce.keystore.file";

    /**
     * System property name - keystore password
     */
    public final static String KEYSTORE_PASSWORD_PROPERTY_NAME = "lettuce.keystore.password";

    /**
     * Lettuce Configuration File Name
     */
    public final static String CONFIGURATION_FILE_NAME = "lettuce.properties";

    /**
     * Persistence Factory Configuration
     */
    public final static String PERSISTENCE_FACTORY_CONFIGURABLE = "persistence.fatory.configurable"; // Persistence Factory Configuration

    /**
     * RDBMS DDL Configurations to initialize database
     */
    public final static String RDBMS_INIT_DDL_CONFIGURABLE = "rdbms.init.ddl.configurable"; // DDL Initialization Configuration

    /**
     * Engine (CORE, API, platforms, etc.) Configurations
     */
    public final static String RDBMS_DATASOURCE = "rdbms.datasource"; // The datasource JNDI naming which used in webserver
    public final static String RDBMS_DATASOURCE_DIALECT = "rdbms.datasource.dialect"; // The datasource dialect, @see org.hibernate.dialect.Dialect
    public final static String RDBMS_DATABASE_DRIVER = "rdbms.database.driver"; // database configuration in not webserver(1): driver class,
    public final static String RDBMS_DATABASE_URL = "rdbms.database.url"; // database configuration in not webserver(2): connection URL
    public final static String RDBMS_DATABASE_USERNAME = "rdbms.database.username"; // database configuration in not webserver(3): user name
    public final static String RDBMS_DATABASE_PASSWORD = "rdbms.database.password"; // database configuration in not webserver(4): password
    public final static String RDBMS_DATABASE_POOLSIZE = "rdbms.database.poolsize"; // database configuration in not webserver(5): connection pool size
    public final static String RDBMS_DATABASE_SHOWSQL = "rdbms.database.showsql"; // database configuration in not webserver(6): show sql for debug

    /**
     * Message service factory configuration
     */
    public final static String MESSAGE_SERVICE_FACTORY_CONFIGURABLE = "message.service.factory.configurable"; // Message Service factory configurable

    /**
     * Message service MQ Server URI. e.g:<br/>
     * <ul>
     * <li>RabbitMQ:
     * <code>amqp://username:password@domain.com:5672/vhost</code></li>
     * <li>ActiveMQ: <code>tcp://username:password@domain.com:61616</code></li></li>
     * <li>Redis: <code>redis://username:password@domain.com:6379</code></li>
     * </ul>
     * 
     */
    public final static String MESSAGE_SERVICE_URI = "message.service.uri";

    /**
     * Message Queue server max connections (defaults to 18)
     */
    public final static String MESSAGE_SERVICE_MAX_CONNECTIONS = "message.service.max.connections";

    /**
     * Message Queue server max sessions per connection (defaults to 500)
     */
    public final static String MESSAGE_SERVICE_MAX_SESSIONS_PER_CONNECTION = "message.service.max.sessions.per.connection";

    /**
     * the connection timeout value for getting Connections from this pool in
     * Milliseconds (defaults to 30 seconds)
     */
    public final static String MESSAGE_SERVICE_CONNECTION_TIMEOUT = "message.service.connection.timeout";

    /**
     * Sets the idle timeout value for Connection's that are created by this pool in
     * Milliseconds (defaults to 30 seconds)<br/>
     * Used for Active MQ<br/>
     */
    public final static String MESSAGE_SERVICE_IDLE_TIMEOUT = "message.service.idle.timeout";

    /**
     * allow connections to expire, irrespective of load or idle time. This is
     * useful with failover to force a reconnect from the pool, to reestablish load
     * balancing or use of the master post recovery<br/>
     * Used for Active MQ<br/>
     */
    public final static String MESSAGE_SERVICE_EXPIRY_TIMEOUT = "message.service.expiry.timeout";

}
