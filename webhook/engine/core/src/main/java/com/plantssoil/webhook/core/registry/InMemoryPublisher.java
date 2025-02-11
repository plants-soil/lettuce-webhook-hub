package com.plantssoil.webhook.core.registry;

import java.util.Date;

import com.plantssoil.webhook.core.ClonableBean;
import com.plantssoil.webhook.core.IPublisher;

/**
 * The in-memory implementation of IPublisher<br/>
 * All data will be lost when JVM shutdown<br/>
 * It's only for demonstration purpose, SHOULD AVOID be used in production
 * environment<br/>
 * 
 * @author danialdy
 * @Date 2 Jan 2025 5:09:23 pm
 */
public class InMemoryPublisher extends ClonableBean implements IPublisher {
    private static final long serialVersionUID = 5562109321658487714L;

    private String publisherId;
    private String organizationId;
    private Boolean supportDataGroup;
    private String version;
    private String createdBy;
    private Date creationTime;

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    @Override
    public void setSupportDataGroup(Boolean supportDataGroup) {
        this.supportDataGroup = supportDataGroup;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getPublisherId() {
        return this.publisherId;
    }

    @Override
    public Boolean getSupportDataGroup() {
        return this.supportDataGroup;
    }

    @Override
    public String getVersion() {
        return this.version;
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
