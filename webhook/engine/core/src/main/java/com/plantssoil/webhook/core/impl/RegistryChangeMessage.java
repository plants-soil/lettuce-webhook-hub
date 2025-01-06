package com.plantssoil.webhook.core.impl;

/**
 * The message used to notify webhook engine via MQ when registry changed.
 * 
 * @author danialdy
 * @Date 3 Jan 2025 9:29:35 am
 */
public class RegistryChangeMessage implements java.io.Serializable {
    private static final long serialVersionUID = 330328342775488047L;

    /**
     * Registry type to identify which registry changed
     * 
     * @author danialdy
     * @Date 3 Jan 2025 9:30:22 am
     */
    public enum RegistryType {
        PUBLISHER, SUBSCRIBER
    }

    /**
     * Change type to identify what kind of change made to the registry
     * 
     * @author danialdy
     * @Date 3 Jan 2025 9:30:58 am
     */
    public enum ChangeType {
        ADD, UPDATE, REMOVE
    }

    private RegistryType registryType;
    private ChangeType changeType;
    private String registryId;

    public RegistryChangeMessage() {
    }

    public RegistryChangeMessage(RegistryType registryType, ChangeType changeType, String registryId) {
        super();
        this.registryType = registryType;
        this.changeType = changeType;
        this.registryId = registryId;
    }

    public RegistryType getRegistryType() {
        return registryType;
    }

    public void setRegistryType(RegistryType registryType) {
        this.registryType = registryType;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public String getRegistryId() {
        return registryId;
    }

    public void setRegistryId(String registryId) {
        this.registryId = registryId;
    }
}
