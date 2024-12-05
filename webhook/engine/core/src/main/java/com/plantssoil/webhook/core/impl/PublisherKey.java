package com.plantssoil.webhook.core.impl;

import java.util.Objects;

/**
 * The key used for identify publisher<br/>
 * Subscriber could subscribe via publisherId + version + dataGroup<br/>
 * The publisherId & version is mandatory, dataGroup is optional<br/>
 * 
 * @author danialdy
 * @Date 3 Dec 2024 3:46:17 pm
 */
public class PublisherKey {
    private final static String NULL_STRING = "##NULL##";
    private String publisherId;
    private String version;
    private String dataGroup = NULL_STRING;

    /**
     * Constructor
     * 
     * @param publisherId publisher id, should not be null
     * @param version     version, should not be null
     * @param dataGroup   data group, optional
     */
    public PublisherKey(String publisherId, String version, String dataGroup) {
        super();
        this.publisherId = publisherId;
        this.version = version;
        if (dataGroup != null) {
            this.dataGroup = dataGroup;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataGroup, publisherId, version);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PublisherKey other = (PublisherKey) obj;
        return Objects.equals(dataGroup, other.dataGroup) && Objects.equals(publisherId, other.publisherId) && Objects.equals(version, other.version);
    }

    /**
     * Get publisher id
     * 
     * @return publisher id
     */
    public String getPublisherId() {
        return publisherId;
    }

    /**
     * Get version
     * 
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get data group
     * 
     * @return data group, null if not set
     */
    public String getDataGroup() {
        return NULL_STRING.equals(this.dataGroup) ? null : this.dataGroup;
    }

}
