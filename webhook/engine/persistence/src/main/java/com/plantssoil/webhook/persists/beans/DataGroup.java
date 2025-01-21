package com.plantssoil.webhook.persists.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.plantssoil.webhook.core.ClonableBean;
import com.plantssoil.webhook.core.IDataGroup;

/**
 * The persistence entity of publisher data groups<br/>
 * The publisher which support multi-datagroups could save data groups in this
 * entity
 * 
 * @author danialdy
 * @Date 28 Dec 2024 12:19:39 pm
 */
@Entity
@Table(name = "LETTUCE_DATAGROUP", uniqueConstraints = @UniqueConstraint(columnNames = { "publisherId", "dataGroup" }), indexes = {
        @Index(name = "idx_datagroup_pubid", columnList = "publisherId") })
public class DataGroup extends ClonableBean implements IDataGroup, Serializable {
    private static final long serialVersionUID = 114811278647950932L;
    @Id
    private String dataGroupId;
    private String publisherId;
    private String dataGroup;
    private String accessToken;
    private String refreshToken;

    public DataGroup() {
    }

    @Override
    public String getDataGroupId() {
        return dataGroupId;
    }

    @Override
    public void setDataGroupId(String dataGroupId) {
        this.dataGroupId = dataGroupId;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
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
