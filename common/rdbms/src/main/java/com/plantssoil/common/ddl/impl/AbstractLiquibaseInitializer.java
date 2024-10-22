package com.plantssoil.common.ddl.impl;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.plantssoil.common.ddl.IDatabaseInitializer;
import com.plantssoil.common.fs.DatabaseChangeLogFileLocator;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.core.DerbyDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

/**
 * The IDatabaseInitializer implementation base on Liquibase<br/>
 * The Liquibase data change log file name should be lettuce-master.xml,
 * lettuce-master.yaml, or lettuce-master.json<br/>
 * Please refer
 * <a href="https://docs.liquibase.com/concepts/changelogs/home.html">Liquibase
 * Documentation</a> to write change log file<br/>
 * The file should be place in one of the following location:
 * <ul>
 * <li>OS variable: <code>$LETTUCE_HOME/data</code></li>
 * <li>System Property: <code>${lettuce.data.dir}</code></li>
 * <li>OS variable: <code>$JBOSS_HOME/standalone/data/lettuce</code></li>
 * <li>System Property: <code>${jboss.server.data.dir}</code></li>
 * <li>OS variable: <code>$CATALINA_HOME/data</code></li>
 * <li>System Property: <code>${tomcat.data.dir}</code></li>
 * <li>System Class Path</li>
 * </ul>
 * 
 * @author danialdy
 *
 */
public abstract class AbstractLiquibaseInitializer implements IDatabaseInitializer {
	private boolean isNewVersion = false;

	/**
	 * Get Database Connection on which to execute Liquibase database initialization
	 * 
	 * @return database connection
	 */
	protected abstract Connection getConnection();

	protected String getDefaultSchema() {
		return null;
	}

	protected String getContexts() {
		return null;
	}

	protected String getChangeLogFile() {
		try {
			URL url = DatabaseChangeLogFileLocator.getDatabaseChangeLogFile("lettuce-master.xml");
			if (url != null) {
				return new File(url.toURI()).getAbsolutePath();
			}

			url = DatabaseChangeLogFileLocator.getDatabaseChangeLogFile("lettuce-master.yaml");
			if (url != null) {
				return new File(url.toURI()).getAbsolutePath();
			}

			url = DatabaseChangeLogFileLocator.getDatabaseChangeLogFile("lettuce-master.json");
			if (url != null) {
				return new File(url.toURI()).getAbsolutePath();
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		throw new RuntimeException("Can't find database change log file (xml, yaml, json) in any location!");
	}

	@Override
	public void initialize(String version) {
		try (Connection connection = getConnection();
				Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
				Liquibase liquibase = new Liquibase(getChangeLogFile(), new FileSystemResourceAccessor(), database)) {
			// check the version information in db
			checkVersion(connection, version);
			// no need execute the initialization if not new version
			if (this.isNewVersion) {
				// call liquibase to initialize
				database.setDefaultSchemaName(getDefaultSchema());
				liquibase.update(getContexts());
				if (database instanceof DerbyDatabase) {
					((DerbyDatabase) database).setShutdownEmbeddedDerby(false);
				}
				// update latest version in db
				updateLatestVersion(connection, version);
			}
		} catch (SQLException | LiquibaseException e) {
			throw new RuntimeException(e);
		}

	}

	private void checkVersion(String version, String lastVersion) {
		String[] segments = version.split("\\.");
		if (segments.length >= 3) {
			String[] lastSegments = lastVersion.split("\\.");
			try {
				for (int i = 0; i < 3; i++) {
					int segment = Integer.parseInt(segments[i]);
					int lastSegment = Integer.parseInt(lastSegments[i]);
					if (segment > lastSegment) {
						this.isNewVersion = true;
						break;
					}
				}
			} catch (NumberFormatException e) {
				throw new RuntimeException("Version format (major.minor.building) is not correct!");
			}
		} else {
			throw new RuntimeException("Version format (major.minor.building) is not correct!");
		}
	}

	private void checkVersion(Connection connection, String version) {
		try (PreparedStatement stmt = connection.prepareStatement("SELECT latestversion FROM lettuce_version"); ResultSet rs = stmt.executeQuery()) {
			int currentRow = rs.getRow();
			if (currentRow > 0) {
				String lastVersion = rs.getString(1);
				checkVersion(version, lastVersion);
			} else {
				createFirstVersion(connection);
				this.isNewVersion = true;
			}
		} catch (SQLException e) {
			createVersionTable(connection);
			this.isNewVersion = true;
			e.printStackTrace();
		}
	}

	private void createVersionTable(Connection connection) {
		try (PreparedStatement stmt = connection.prepareStatement("CREATE TABLE lettuce_version (latestversion VARCHAR(100))")) {
			stmt.execute();
			createFirstVersion(connection);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void createFirstVersion(Connection connection) {
		try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO lettuce_version (latestversion) VALUES ('0.0.0')")) {
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void updateLatestVersion(Connection connection, String latestVersion) {
		try (PreparedStatement stmt = connection.prepareStatement("UPDATE lettuce_version SET latestversion = ?")) {
			stmt.setString(1, latestVersion);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
