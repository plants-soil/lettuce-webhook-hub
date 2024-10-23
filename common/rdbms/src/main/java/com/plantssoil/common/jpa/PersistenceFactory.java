package com.plantssoil.common.jpa;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.configuration.Configuration;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;

/**
 * 
 * Manage connection pool (via entity manager factory)<br/>
 * Create persistence instance, the implementation is defined via configuration
 * {@link LettuceConfiguration.RDBMS_JPA_PERSISTENCE_CONFIGURABLE}
 * 
 * @author danialdy
 *
 */
public class PersistenceFactory {
	private static volatile Map<String, PersistenceFactory> factories;
	private String persistenceUnitName;
	private String datasourceName;
	private DatabaseConnectionConfig databaseConnectionConfig;
	private volatile EntityManagerFactory entityManagerFactory;

	private PersistenceFactory(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
	}

	/**
	 * Get the factory instance
	 * 
	 * @param persistenceUnitName JPA Persistence Unit Name, will use different
	 *                            connection pool for different PersistentUnit
	 * @return this factory
	 */
	public static PersistenceFactory getInstance(String persistenceUnitName) {
		if (factories == null) {
			synchronized (PersistenceFactory.class) {
				if (factories == null) {
					factories = new ConcurrentHashMap<>();
				}
			}
		}

		// assumes ConcurrentHashMap works well
		PersistenceFactory factory = factories.get(persistenceUnitName);
		if (factory == null) {
			factory = new PersistenceFactory(persistenceUnitName);
			factories.put(persistenceUnitName, factory);
		}
		return factory;
	}

	/**
	 * Read the persistence implementation
	 * {@link LettuceConfiguration.RDBMS_JPA_PERSISTENCE_CONFIGURABLE} from
	 * lettuce.properties<br/>
	 * Create new instance of the persistence, and assign the entity manager to it
	 * 
	 * @return persistence instance created
	 */
	public IPersistence getPersistence() {
		Class<IConfigurable> clazz = ConfigurableLoader.getInstance().loadConfigurable(LettuceConfiguration.RDBMS_JPA_PERSISTENCE_CONFIGURABLE);
		if (!IPersistence.class.isAssignableFrom(clazz)) {
			String err = String.format("The implementation (%s) with configure name (%s) does not implement 'com.plantssoil.common.jpa.IPersistence'!",
					clazz.getName(), LettuceConfiguration.RDBMS_JPA_PERSISTENCE_CONFIGURABLE);
			throw new RuntimeException(err);
		}
		try {
			IPersistence persistence = (IPersistence) clazz.getConstructor().newInstance();
			EntityManager em = getEntityManagerFactory().createEntityManager();
			persistence.setEntityManager(em);
			return persistence;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set datasource JNDI name (when deployed in container)
	 * 
	 * @param datasourceName datasource JNDI name
	 */
	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}

	/**
	 * set database connection config if it's different from engine default database
	 * 
	 * @param databaseConnectionConfig the database connection config object
	 */
	public void setDatabaseConnectionConfig(DatabaseConnectionConfig databaseConnectionConfig) {
		this.databaseConnectionConfig = databaseConnectionConfig;
	}

	private DatabaseConnectionConfig getEngineDatabaseConnectionConfig() {
		Configuration conf = ConfigFactory.getInstance().getConfiguration();
		String driver = conf.getString(LettuceConfiguration.ENGINE_CORE_DATABASE_DRIVER);
		String url = conf.getString(LettuceConfiguration.ENGINE_CORE_DATABASE_URL);
		String username = conf.getString(LettuceConfiguration.ENGINE_CORE_DATABASE_USERNAME);
		String password = conf.getString(LettuceConfiguration.ENGINE_CORE_DATABASE_PASSWORD);
		int poolsize = conf.getInt(LettuceConfiguration.ENGINE_CORE_DATABASE_POOLSIZE);
		boolean showsql = conf.getBoolean(LettuceConfiguration.ENGINE_CORE_DATABASE_SHOWSQL);
		DatabaseConnectionConfig dbconfig = new DatabaseConnectionConfig(driver, url, username, password);
		dbconfig.setConnectionPoolSize(poolsize);
		dbconfig.setShowSql(showsql);
		return dbconfig;
	}

	private EntityManagerFactory getEntityManagerFactory() {
		if (this.entityManagerFactory == null) {
			synchronized (this.persistenceUnitName) {
				if (this.entityManagerFactory == null) {
					Map<String, String> connectionProperties = getConnectionProperties();
					this.entityManagerFactory = Persistence.createEntityManagerFactory(this.persistenceUnitName, connectionProperties);
				}
			}
		}
		return this.entityManagerFactory;
	}

	private Map<String, String> getConnectionProperties() {
		Map<String, String> properties = new HashMap<>();
		if (this.datasourceName != null) {
			properties.put("jta-data-source", this.datasourceName);
		}

		DatabaseConnectionConfig databaseConfig = this.databaseConnectionConfig == null ? getEngineDatabaseConnectionConfig() : this.databaseConnectionConfig;
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
		properties.put("hibernate.connection.pool_size", String.valueOf(databaseConfig.getConnectionPoolSize()));
		properties.put("hibernate.show_sql", String.valueOf(databaseConfig.isShowSql()));
		return properties;
	}
}
