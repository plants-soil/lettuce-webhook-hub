package com.plantssoil.webhook.core;

import java.io.Serializable;

/**
 * Data group, if publisher supports data group
 * 
 * @author danialdy
 * @Date 29 Nov 2024 7:21:42 pm
 */
public interface IDataGroup extends Serializable {
    /**
     * Set the data group id
     * 
     * @param dataGroupId data group id
     */
    public void setDataGroupId(String dataGroupId);

    /**
     * Set the data group
     * 
     * @param dataGroup data group name
     */
    public void setDataGroup(String dataGroup);

    /**
     * Set the access token on data group
     * 
     * @param accessToken access token on data group
     */
    public void setAccessToken(String accessToken);

    /**
     * Set the refresh token on data group
     * 
     * @param refreshToken refresh token on data group
     */
    public void setRefreshToken(String refreshToken);

    /**
     * Get the data group id
     * 
     * @return the data group id
     */
    public String getDataGroupId();

    /**
     * Get the data group
     * 
     * @return data group name
     */
    public String getDataGroup();

    /**
     * Get access token on data group
     * 
     * @return access token on data group
     */
    public String getAccessToken();

    /**
     * Get refresh token on data group
     * 
     * @return refresh token on data group
     */
    public String getRefreshToken();
}
