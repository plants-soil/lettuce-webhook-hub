package com.plantssoil.common.mq;

/**
 * Message Publisher<br/>
 * Message sender could publish message to message queue<br/>
 * This Publisher will choose which channel should be used to send message by
 * {@link IMessagePublisher#publisherId()} + {@link IMessagePublisher#version()}
 * + {@link IMessagePublisher#dataGroup()}<br/>
 * Subscribers will subscribe messages from different organization + version +
 * dataGroup binding<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 4:48:32 pm
 */
public interface IMessagePublisher {
    /**
     * Provide the identity of publisher, (mandatory)<br/>
     * Which is used to create MQ Queue (with version, and data group)<br/>
     * Mostly it's the organization id of the publisher<br/>
     * 
     * @param publisherId which is used to create MQ Queue (with version, and data
     *                    group)
     * @return current publisher instance
     * 
     * @see IMessagePublisher#version(String)
     * @see IMessagePublisher#dataGroup(String)
     */
    public IMessagePublisher publisherId(String publisherId);

    /**
     * Provide the version of publisher, (mandatory)<br/>
     * Which is used to create MQ Queue (with publisher id, and data group)<br/>
     *
     * @param version publisher version
     * @return current publisher instance
     * 
     * @see IMessagePublisher#publisherId(String)
     * @see IMessagePublisher#dataGroup(String)
     */
    public IMessagePublisher version(String version);

    /**
     * Provide the data group of publisher, (NOT mandatory)<br/>
     * Which is used to create MQ Queue (with publisher id, and version)<br/>
     * 
     * @param dataGroup data group of current message
     * @return current publisher instance
     * 
     * @see IMessagePublisher#publisherId(String)
     * @see IMessagePublisher#version(String)
     */
    public IMessagePublisher dataGroup(String dataGroup);

    /**
     * Send message to message queue<br/>
     * This Publisher will choose which channel should be used to send message by
     * {@link IMessagePublisher#publisherId()} + {@link IMessagePublisher#version()}
     * + {@link IMessagePublisher#dataGroup()}<br/>
     * Subscribers should subscribe messages from different organization + version +
     * dataGroup binding<br/>
     * 
     * @param message informations to be sent
     */
    public void publish(String message);
}
