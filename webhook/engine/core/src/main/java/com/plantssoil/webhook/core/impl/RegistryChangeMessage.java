package com.plantssoil.webhook.core.impl;

import java.util.Set;

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
        PUBLISHER, EVENT, DATAGROUP, SUBSCRIBER, WEBHOOK, EVENT_SUBSCRIBE, DATAGROUP_SUBSCRIBE
    }

    /**
     * Change type to identify what kind of change made to the registry
     * 
     * @author danialdy
     * @Date 3 Jan 2025 9:30:58 am
     */
    public enum ChangeType {
        ADD, UPDATE, REMOVE, ACTIVATE, DEACTIVATE, SUBSCRIBE, UNSUBSCRIBE
    }

    private RegistryType registryType;
    private ChangeType changeType;
    private String publisherId;
    private String subscriberId;
    private String webhookId;
    private Set<String> registryId;

    public RegistryChangeMessage() {
    }

    public RegistryChangeMessage(RegistryType registryType, ChangeType changeType) {
        super();
        this.registryType = registryType;
        this.changeType = changeType;
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

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getWebhookId() {
        return webhookId;
    }

    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    public Set<String> getRegistryId() {
        return registryId;
    }

    public void setRegistryId(Set<String> registryId) {
        this.registryId = registryId;
    }
}
