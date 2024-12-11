package com.plantssoil.webhook.core.impl;

import com.plantssoil.webhook.core.IDataGroup;

public class SimpleDataGroup implements IDataGroup {
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
