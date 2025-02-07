package com.plantssoil.webhook.core.registry;

import com.plantssoil.webhook.core.ClonableBean;
import com.plantssoil.webhook.core.ISubscriber;

/**
 * The in-memory implementation of ISubscriber<br/>
 * All data will be lost when JVM shutdown<br/>
 * It's only for demonstration purpose, SHOULD AVOID be used in production
 * environment<br/>
 * 
 * @author danialdy
 * @Date 2 Jan 2025 5:09:49 pm
 */
public class InMemorySubscriber extends ClonableBean implements ISubscriber {
    private static final long serialVersionUID = 3791881991898699931L;

    private String subscriberId;
    private String organizationId;

    @Override
    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Override
    public String getSubscriberId() {
        return this.subscriberId;
    }

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
