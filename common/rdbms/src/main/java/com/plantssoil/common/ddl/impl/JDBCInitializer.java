package com.plantssoil.common.ddl.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
		if (this.driverClass == null || this.connectionUrl == null || this.userName == null || this.password == null || this.driverClass.strip().length() == 0
				|| this.connectionUrl.strip().length() == 0 || this.userName.strip().length() == 0 || this.password.strip().length() == 0) {
			throw new RuntimeException("Database connection (driverClass, connectionUrl, userName, password) can't be null!");
		}
		try {
			Class.forName(this.driverClass);
			return DriverManager.getConnection(this.connectionUrl, this.userName, this.password);
		} catch (ClassNotFoundException | SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
