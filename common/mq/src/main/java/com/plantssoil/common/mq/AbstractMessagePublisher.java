package com.plantssoil.common.mq;

/**
 * The abstract message publisher which hold publisher id + version + data group
 * 
 * @author danialdy
 * @Date 6 Nov 2024 7:37:19 pm
 */
public abstract class AbstractMessagePublisher implements IMessagePublisher {
    private String publisherId;
    private String version;
    private String dataGroup;

    @Override
    public IMessagePublisher publisherId(String publisherId) {
        this.publisherId = publisherId;
        return this;
    }

    @Override
    public IMessagePublisher version(String version) {
        this.version = version;
        return this;
    }

    @Override
    public IMessagePublisher dataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
        return this;
    }

    /**
     * Get publisher id (usually should be organization id)
     * 
     * @return publisher id
     */
    protected String getPublisherId() {
        return publisherId;
    }

    /**
     * Get publisher version
     * 
     * @return publisher version
     */
    protected String getVersion() {
        return version;
    }

    /**
     * Get data group, some organizations need separate data from business unit or
     * merchants
     * 
     * @return data group
     */
    protected String getDataGroup() {
        return dataGroup;
    }
}
