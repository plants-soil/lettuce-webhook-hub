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
     * Lettuce Webhook engine version
     */
    public final static String WEBHOOK_ENGINE_VERSION = "webhook.engine.version";

    /**
     * Lettuce Webhook Engine Factory implementation configuration
     */
    public final static String WEBHOOK_ENGINE_FACTORY_CONFIGURABLE = "webhook.engine.factory.configurable";

    /**
     * Lettuce Webhook Engine max idle http connection pool, defaults to 5
     */
    public final static String WEBHOOK_ENGINE_CONNECTION_POOL_SIZE = "webhook.engine.connection.pool.size";

    /**
     * Lettuce Webhook Engine max requests concurrently requesting, defaults to 64
     */
    public final static String WEBHOOK_ENGINE_MAX_REQUESTS = "webhook.engine.max.requests";

    /**
     * Lettuce Webhook Engine max requests concurrently requesting per host,
     * defaults to 5
     */
    public final static String WEBHOOK_ENGINE_MAX_REQUESTS_PER_HOST = "webhook.engine.max.requests.per.host";

    /**
     * Lettuce Webhook Engine processor retry queue capacity 5 seconds delay: task
     * will be push into RETRY_QUEUE_5 (which will be retry 5 seconds later) if the
     * first process failed, defaults to 10,000
     */
    public final static String WEBHOOK_ENGINE_RETRY_QUEUE_CAPACITY5 = "webhook.engine.retry.queue.capacity5";

    /**
     * Lettuce Webhook Engine processor retry queue capacity 30 seconds delay: task
     * will be push into RETRY_QUEUE_30 (which will be retry 30 seconds later) if
     * the first retry failed, defaults to 10,000
     */
    public final static String WEBHOOK_ENGINE_RETRY_QUEUE_CAPACITY30 = "webhook.engine.retry.queue.capacity30";

    /**
     * Persistence Factory implementation configuration
     */
    public final static String PERSISTENCE_FACTORY_CONFIGURABLE = "persistence.fatory.configurable"; // Persistence Factory Configuration

    /**
     * Persistence database configuration in non-webserver: connection URL (RDBMS
     * and NOSQL both use this URL)<br/>
     * This configure is no need if the following configured:<br/>
     * <ul>
     * <li><code>{@link PERSISTENCE_FACTORY_CONFIGURABLE} = {@link com.plantssoil.common.persistence.rdbms.JPAPersistenceFactory}</code></li>
     * <li><code>{@link RDBMS_DATASOURCE}</li>
     * </ul>
     */
    public final static String PERSISTENCE_DATABASE_URL = "persistence.database.url";

    /**
     * Database configuration in non-webserver: user name (RDBMS and NOSQL both use
     * this URL)<br/>
     * <p>
     * Could ignore this configure, and include the username & password
     * ({@link PERSISTENCE_DATABASE_PASSWORD}) in database connection url:
     * <code>{@link PERSISTENCE_DATABASE_URL}</code>
     * </p>
     * 
     * This configure is no need if the following configured:<br/>
     * <ul>
     * <li><code>{@link PERSISTENCE_FACTORY_CONFIGURABLE} = {@link com.plantssoil.common.persistence.rdbms.JPAPersistenceFactory}</code></li>
     * <li><code>{@link RDBMS_DATASOURCE}</li>
     * </ul>
     */
    public final static String PERSISTENCE_DATABASE_USERNAME = "persistence.database.username";

    /**
     * Database configuration in non-webserver: password (RDBMS and NOSQL both use
     * this URL)<br/>
     * <p>
     * Could ignore this configure, and include username
     * ({@link PERSISTENCE_DATABASE_USERNAME}) & password in database connection
     * url: <code>{@link PERSISTENCE_DATABASE_URL}</code>
     * </p>
     * 
     * This configure is no need if the following configured:<br/>
     * <ul>
     * <li><code>{@link PERSISTENCE_FACTORY_CONFIGURABLE} = {@link com.plantssoil.common.persistence.rdbms.JPAPersistenceFactory}</code></li>
     * <li><code>{@link RDBMS_DATASOURCE}</li>
     * </ul>
     */
    public final static String PERSISTENCE_DATABASE_PASSWORD = "persistence.database.password";

    /**
     * Database configuration in non-webserver: connection pool (RDBMS and NOSQL
     * both use this URL)<br/>
     * This configure is no need if the following configured:<br/>
     * <ul>
     * <li><code>{@link PERSISTENCE_FACTORY_CONFIGURABLE} = {@link com.plantssoil.common.persistence.rdbms.JPAPersistenceFactory}</code></li>
     * <li><code>{@link RDBMS_DATASOURCE}</li>
     * </ul>
     */
    public final static String PERSISTENCE_DATABASE_POOLSIZE = "persistence.database.poolsize";

    /**
     * Persistence Initializer implementation configuration which is used to
     * initialize database
     */
    public final static String PERSISTENCE_INITIALIZER_CONFIGURABLE = "persistence.initializer.configurable";

    // RDBMS connection related configurations
    /**
     * The RDBMS datasource JNDI naming which used in webserver.<br/>
     * <p>
     * This configure is effective when
     * <code>{@link PERSISTENCE_FACTORY_CONFIGURABLE} =
     * {@link com.plantssoil.common.persistence.rdbms.JPAPersistenceFactory}</code>
     * </p>
     * And the following configures are no need when this one configured:
     * <ul>
     * <li><code>{@link PERSISTENCE_DATABASE_URL}</code></li>
     * <li><code>{@link PERSISTENCE_DATABASE_USERNAME}</code></li>
     * <li><code>{@link PERSISTENCE_DATABASE_PASSWORD}</code></li>
     * <li><code>{@link PERSISTENCE_DATABASE_POOLSIZE}</code></li>
     * <li><code>{@link RDBMS_DATABASE_DRIVER}</code></li>
     * <li><code>{@link RDBMS_DATABASE_SHOWSQL}</code></li>
     * </ul>
     */
    public final static String RDBMS_DATASOURCE = "rdbms.datasource";

    /**
     * The datasource dialect, @see org.hibernate.dialect.Dialect This configure is
     * effective when <code>{@link PERSISTENCE_FACTORY_CONFIGURABLE} =
     * {@link com.plantssoil.common.persistence.rdbms.JPAPersistenceFactory}</code>
     */
    public final static String RDBMS_DATASOURCE_DIALECT = "rdbms.datasource.dialect";

    /**
     * Database configuration in non-webserver, driver class This configure is
     * effective when <code>{@link PERSISTENCE_FACTORY_CONFIGURABLE} =
     * {@link com.plantssoil.common.persistence.rdbms.JPAPersistenceFactory}</code>
     */
    public final static String RDBMS_DATABASE_DRIVER = "rdbms.database.driver";

    /**
     * Database configuration in non-webserver, show SQL for debug purpose This
     * configure is effective when <code>{@link PERSISTENCE_FACTORY_CONFIGURABLE} =
     * {@link com.plantssoil.common.persistence.rdbms.JPAPersistenceFactory}</code>
     */
    public final static String RDBMS_DATABASE_SHOWSQL = "rdbms.database.showsql";

    /**
     * Message service factory implementation configuration
     */
    public final static String MESSAGE_SERVICE_FACTORY_CONFIGURABLE = "message.service.factory.configurable";

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
