package com.plantssoil.common.mq.rabbit;

import com.plantssoil.common.mq.IMessage;

/**
 * Simple Message
 * 
 * @author danialdy
 * @Date 3 Nov 2024 8:53:23 pm
 */
public class Message implements IMessage {
    private static final long serialVersionUID = 4519170869670477272L;
    private String publisherId;
    private String version;
    private String dataGroup;
    private String message;

    public Message(String publisherId, String version, String dataGroup, String message) {
        this.publisherId = publisherId;
        this.version = version;
        this.dataGroup = dataGroup;
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
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getDataGroup() {
        return this.dataGroup;
    }

}
