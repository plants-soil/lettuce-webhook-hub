package com.plantssoil.webhook.core.registry;

import com.plantssoil.webhook.core.ClonableBean;
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
public class InMemoryDataGroup extends ClonableBean implements IDataGroup {
    private static final long serialVersionUID = -7836745692972021937L;

    private String dataGroupId;
    private String dataGroup;
    private String accessToken;
    private String refreshToken;

    @Override
    public String getDataGroupId() {
        return dataGroupId;
    }

    @Override
    public void setDataGroupId(String dataGroupId) {
        this.dataGroupId = dataGroupId;
    }

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
