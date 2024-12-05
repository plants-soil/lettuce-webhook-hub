package com.plantssoil.webhook.core;

import java.util.List;

/**
 * The publisher, each instance of the IPublisher presents one publisher
 * <ul>
 * <li>publisher could have multiple data groups</li>
 * <li>publisher could add & query data groups</li>
 * <li>publisher could add & query events</li>
 * </ul>
 * If publisher changed (any property, especially datagroup / event, or event
 * property changed), need call {@link IRegistry#updatePublisher(IPublisher)} to
 * reload consumers for subscriber<br/>
 * 
 * @author danialdy
 * @Date 29 Nov 2024 11:10:53 am
 */
public interface IPublisher {
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
    public void setSupportDataGroup(boolean supportDataGroup);

    /**
     * Set the event version, an event may have multiple versions
     * 
     * @param version event version
     */
    public void setVersion(String version);

    /**
     * Add data group, if the publisher has data group
     * 
     * @param dataGroup data group name
     */
    public void addDataGroup(String dataGroup);

    /**
     * Add multiple data group, if the publisher has data group
     * 
     * @param dataGroups data group names
     */
    public void addDataGroup(List<String> dataGroups);

    /**
     * Add event
     * 
     * @param event event to add
     */
    public void addEvent(IEvent event);

    /**
     * Add events
     * 
     * @param events events to add
     */
    public void addEvent(List<IEvent> events);

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
    public boolean isSupportDataGroup();

    /**
     * Get the event version
     * 
     * @return event version
     */
    public String getVersion();

    /**
     * Find all data groups, support pagination
     * 
     * @param page     the page index
     * @param pageSize maximum data groups in current page
     * @return data groups in current page
     */
    public List<String> findDataGroups(int page, int pageSize);

    /**
     * Find events, by version + eventType
     * 
     * @param version   event version
     * @param eventType event Type
     * @return Event found, null otherwise
     */
    public IEvent findEvent(String version, String eventType);

    /**
     * Find all events, support pagination
     * 
     * @param page     the page index
     * @param pageSize maximum events in current page
     * @return events in current page
     */
    public List<IEvent> findEvents(int page, int pageSize);
}
