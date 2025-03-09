package com.plantssoil.common.persistence.rdbms;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.fs.DatabaseChangeLogFileLocator;
import com.plantssoil.common.persistence.IPersistenceInitializer;
import com.plantssoil.common.persistence.exception.PersistenceException;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.core.DerbyDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
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
abstract class AbstractLiquibaseInitializer implements IPersistenceInitializer {
    final static Logger LOGGER = LoggerFactory.getLogger(AbstractLiquibaseInitializer.class.getName());

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
            if (url == null) {
                url = DatabaseChangeLogFileLocator.getDatabaseChangeLogFile("lettuce-master.yaml");
            }
            if (url == null) {
                url = DatabaseChangeLogFileLocator.getDatabaseChangeLogFile("lettuce-master.json");
            }
            if (url == null) {
                throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13003,
                        "Can't find database change log file (xml, yaml, json) in any location!");
            }
            return new File(url.toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13004, e);
        }
    }

    @Override
    public void initialize() {
        LOGGER.info("Loading Liquibase to initialize RDBMS...");
        String changeLogFileWithFullPath = getChangeLogFile();
        int lastSeperator = changeLogFileWithFullPath.lastIndexOf(File.separator);
        String path = changeLogFileWithFullPath.substring(0, lastSeperator);
        String changeLogFileName = changeLogFileWithFullPath.substring(lastSeperator + 1);

        // Can't put Connection & Database into try () clause
        // Liquibase will try to close the Connection and Database
        // Then exception will happen if put Connection & Database into try () clause
        Connection connection = getConnection();
        Database database = null;
        try {
            database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        } catch (DatabaseException e) {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13005, e);
        }

        // only put Liquibase object into try ()
        try (Liquibase liquibase = new Liquibase(changeLogFileName, new FileSystemResourceAccessor(new File(path)), database)) {
            // call liquibase to initialize
            database.setDefaultSchemaName(getDefaultSchema());
            liquibase.update(getContexts());
            if (database instanceof DerbyDatabase) {
                ((DerbyDatabase) database).setShutdownEmbeddedDerby(false);
            }
        } catch (LiquibaseException e) {
            throw new PersistenceException(PersistenceException.BUSINESS_EXCEPTION_CODE_13006, e);
        }
        LOGGER.info("RDBMS initialized or updated.");
    }
}
