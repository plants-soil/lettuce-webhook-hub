package com.plantssoil.common.persistence.rdbms;

/**
 * Database Connection Information
 * 
 * @author danialdy
 *
 */
class DatabaseConnectionConfig {
    private String databaseDriver;
    private String connectionUrl;
    private String userName;
    private String password;
    private int connectionPoolSize = 20;
    private boolean showSql = false;

    public DatabaseConnectionConfig() {
    }

    public DatabaseConnectionConfig(String databaseDriver, String connectionUrl, String userName, String password) {
        super();
        this.databaseDriver = databaseDriver;
        this.connectionUrl = connectionUrl;
        this.userName = userName;
        this.password = password;
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }

    public void setDatabaseDriver(String databaseDriver) {
        this.databaseDriver = databaseDriver;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public boolean isShowSql() {
        return showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }
}
