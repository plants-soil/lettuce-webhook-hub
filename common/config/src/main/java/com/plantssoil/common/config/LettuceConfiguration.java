package com.plantssoil.common.config;

/**
 * All configuration item names which contained in lettuce.properties
 * 
 * @author danialdy
 *
 */
public class LettuceConfiguration {
	/**
	 * RDBMS (DDL, JPA, etc.) Configurations
	 */
	public final static String RDBMS_INIT_DDL_CONFIGURABLE = "rdbms.ddl.init.configurable"; // DDL Initialization Configuration
	public final static String RDBMS_JPA_PERSISTENCE_CONFIGURABLE = "rdbms.jpa.persistence.configurable"; // JPA Persistence Configuration
	//
	/**
	 * Engine (CORE, API, platforms, etc.) Configurations
	 */
	public final static String ENGINE_CORE_DATASOURCE = "engine.datasource"; // The datasource JNDI naming which used in webserver
	public final static String ENGINE_CORE_DATABASE_DRIVER = "engine.database.driver"; // database configuration in not webserver(1): driver class,
	public final static String ENGINE_CORE_DATABASE_URL = "engine.database.url"; // database configuration in not webserver(2): connection URL
	public final static String ENGINE_CORE_DATABASE_USERNAME = "engine.database.username"; // database configuration in not webserver(3): user name
	public final static String ENGINE_CORE_DATABASE_PASSWORD = "engine.database.password"; // database configuration in not webserver(4): password
	public final static String ENGINE_CORE_DATABASE_POOLSIZE = "engine.database.poolsize"; // database configuration in not webserver(5): connection pool size
	public final static String ENGINE_CORE_DATABASE_SHOWSQL = "engine.database.showsql"; // database configuration in not webserver(6): show sql for debug
}
