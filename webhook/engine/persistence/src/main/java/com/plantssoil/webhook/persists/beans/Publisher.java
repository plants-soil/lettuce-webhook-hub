package com.plantssoil.webhook.persists.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.plantssoil.webhook.core.ClonableBean;
import com.plantssoil.webhook.core.IPublisher;

/**
 * The publisher which will publish events & messages to subscribers
 */
@Entity
@Table(name = "LETTUCE_PUBLISHER", indexes = { @Index(name = "idx_publisher_orgid", columnList = "organizationId") })
public class Publisher extends ClonableBean implements IPublisher, Serializable {
    private static final long serialVersionUID = -1820991401679378737L;
    @Id
    private String publisherId;
    private String organizationId;
    private boolean supportDataGroup;
    private String version;
    private String createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    public Publisher() {
    }

    @Override
    public String getPublisherId() {
        return publisherId;
    }

    @Override
    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public boolean isSupportDataGroup() {
        return supportDataGroup;
    }

    @Override
    public void setSupportDataGroup(boolean supportDataGroup) {
        this.supportDataGroup = supportDataGroup;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

}
