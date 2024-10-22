package com.plantssoil.common.ddl.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

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
