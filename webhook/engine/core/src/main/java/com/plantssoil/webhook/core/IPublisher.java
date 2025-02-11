package com.plantssoil.webhook.core;

import java.io.Serializable;

/**
 * The publisher, each instance of the IPublisher presents one publisher
 * <ul>
 * <li>publisher could have multiple data groups</li>
 * <li>publisher could have multiple events</li>
 * </ul>
 * If publisher changed, need call {@link IRegistry#updatePublisher(IPublisher)}
 * to reload consumers for subscriber<br/>
 * 
 * @author danialdy
 * @Date 29 Nov 2024 11:10:53 am
 */
public interface IPublisher extends Serializable {
    /**
     * Get the organization id which current publisher belongs to
     * 
     * @return The organization id
     */
    public String getOrganizationId();

    /**
     * Set the organization id which current publisher belongs to
     * 
     * @param organizationId The organization id
     */
    public void setOrganizationId(String organizationId);

    /**
     * Set the publisher id
     * 
     * @param publisherId publisher id
     */
    public void setPublisherId(String publisherId);

    /**
     * Tell the publisher support data group or not, defaults to false - don't
     * support data group.<br/>
     * Exception will happen to call {@link IPublisher#addDataGroup} if
     * supportDataGroup is false<br/>
     * 
     * @param supportDataGroup true - support data group, false - don't support data
     *                         group
     */
    public void setSupportDataGroup(Boolean supportDataGroup);

    /**
     * Set the event version, an event may have multiple versions
     * 
     * @param version event version
     */
    public void setVersion(String version);

    /**
     * Get the publisher id
     * 
     * @return publisher id
     */
    public String getPublisherId();

    /**
     * The publisher support data group or not
     * 
     * @return true - support data group, false - don't support data group
     */
    public Boolean getSupportDataGroup();

    /**
     * Get the event version
     * 
     * @return event version
     */
    public String getVersion();
}
