package com.plantssoil.webhook.core.registry;

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
    private String publisherId;
    private String organizationId;
    private boolean supportDataGroup;
    private String version;

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
    public void setSupportDataGroup(boolean supportDataGroup) {
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
    public boolean isSupportDataGroup() {
        return this.supportDataGroup;
    }

    @Override
    public String getVersion() {
        return this.version;
    }
}
