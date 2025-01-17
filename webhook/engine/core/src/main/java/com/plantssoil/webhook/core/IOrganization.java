package com.plantssoil.webhook.core;

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
public interface IOrganization {
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
