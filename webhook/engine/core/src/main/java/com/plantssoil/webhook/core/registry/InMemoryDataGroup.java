package com.plantssoil.webhook.core.registry;

import com.plantssoil.webhook.core.IDataGroup;

/**
 * The in-memory implementation of IDataGroup<br/>
 * All data will be lost when JVM shutdown<br/>
 * It's only for demonstration purpose, SHOULD AVOID be used in production
 * environment<br/>
 * 
 * @author danialdy
 * @Date 2 Jan 2025 5:07:28 pm
 */
public class InMemoryDataGroup implements IDataGroup {
    private String dataGroup;
    private String accessToken;
    private String refreshToken;

    @Override
    public String getDataGroup() {
        return dataGroup;
    }

    @Override
    public void setDataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
