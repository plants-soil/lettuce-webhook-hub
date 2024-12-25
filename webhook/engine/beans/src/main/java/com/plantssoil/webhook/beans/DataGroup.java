package com.plantssoil.webhook.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "LETTUCE_DATAGROUP", uniqueConstraints = @UniqueConstraint(columnNames = "organizationId,dataGroupName"), indexes = {
        @Index(name = "idx_datagroup_orgid", columnList = "organizationId") })
public class DataGroup implements Serializable {
    private static final long serialVersionUID = 114811278647950932L;
    @Id
    private String dataGroupId;
    private String organizationId;
    private String dataGroupName;

    public String getDataGroupId() {
        return dataGroupId;
    }

    public void setDataGroupId(String dataGroupId) {
        this.dataGroupId = dataGroupId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getDataGroupName() {
        return dataGroupName;
    }

    public void setDataGroupName(String dataGroupName) {
        this.dataGroupName = dataGroupName;
    }

}
