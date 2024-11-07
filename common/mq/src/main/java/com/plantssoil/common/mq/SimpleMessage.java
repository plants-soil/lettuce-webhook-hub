package com.plantssoil.common.mq;

/**
 * Simple Message
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:53:23 pm
 */
public class SimpleMessage implements IMessage {
    private static final long serialVersionUID = 4519170869670477272L;
    private String publisherId;
    private String version;
    private String dataGroup;
    private String consumerId;
    private String message;

    public SimpleMessage(String publisherId, String version, String dataGroup, String consumerId, String message) {
        this.publisherId = publisherId;
        this.version = version;
        this.dataGroup = dataGroup;
        this.consumerId = consumerId;
        this.message = message;
    }

    @Override
    public String getPublisherId() {
        return this.publisherId;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getDataGroup() {
        return this.dataGroup;
    }

    @Override
    public String getConsumerId() {
        return this.consumerId;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
