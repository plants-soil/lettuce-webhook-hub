package com.plantssoil.webhook.core.registry;

import com.plantssoil.webhook.core.IOrganization;

/**
 * The in-memory implementation of IOragnization<br/>
 * All data will be lost when JVM shutdown<br/>
 * It's only for demonstration purpose, SHOULD AVOID be used in production
 * environment<br/>
 * 
 * @author danialdy
 * @Date 17 Jan 2025 4:35:28 pm
 */
public class InMemoryOrganization implements IOrganization {
    private String organizationId;

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

}
