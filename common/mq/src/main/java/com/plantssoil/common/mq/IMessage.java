package com.plantssoil.common.mq;

import java.io.Serializable;

/**
 * Message
 * 
 * @author danialdy
 * @Date 1 Nov 2024 4:35:47 pm
 */
public interface IMessage extends Serializable {
    /**
     * Get publisher id (mandatory)<br/>
     * Mostly it's the organization id of the publisher<br/>
     * 
     * @return publisher id, which is used to create MQ Queue (with version, and
     *         data group)
     * 
     */
    public String getPublisherId();

    /**
     * Get version (mandatory), e.g: 1.0<br/>
     * Application may have different versions<br/>
     * 
     * @return version, which is used to create MQ Queue (with publisher id and data
     *         group)
     */
    public String getVersion();

    /**
     * Get data group (optional), e.g: AmazonStore<br/>
     * Application may need separate data into different groups, and data is not
     * shared between groups
     * 
     * @return data group name, which is used to create MQ Queue (with publisher id
     *         and version)
     */
    public String getDataGroup();

    /**
     * Get the message (mostly be payload JSON / xml)
     * 
     * @return payload text
     */
    public String getMessage();
}
