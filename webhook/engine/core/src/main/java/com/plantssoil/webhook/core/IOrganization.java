package com.plantssoil.webhook.core;

import java.io.Serializable;

/**
 * The organization, could be company, business unit, or any kinds of
 * organizations.<br/>
 * In lettuce system structure, publishers and subscribers are different kinds
 * of organizations, means one publisher / subscriber should belong to one
 * organization<br/>
 * 
 * @author danialdy
 * @Date 17 Jan 2025 4:24:14 pm
 */
public interface IOrganization extends Serializable {
    /**
     * The organization status enumeration
     * 
     * @author danialdy
     * @Date 10 Feb 2025 10:51:58 am
     */
    public enum OrganizationStatus {
        ACTIVE, INACTIVE, LOCKED
    }

    /**
     * Get the organization id
     * 
     * @return The organization id
     */
    public String getOrganizationId();

    /**
     * Set the organization id
     * 
     * @param organizationId The organization id
     */
    public void setOrganizationId(String organizationId);
}
