package com.plantssoil.common.mq;

/**
 * Subscribe message from specific publisher + version + data group<br/>
 * Subscriber could get messages from Message Queue Server which publisher
 * created (so the MQ Queue Name should be consistent with publisher's Queue
 * Name)<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 4:55:17 pm
 */
public interface IMessageSubscriber {
    /**
     * Provide the identity of publisher to describe, (mandatory)<br/>
     * Which is used to determine MQ Queue Name (with version, and data group)<br/>
     * Mostly it's the organization id of the publisher<br/>
     * 
     * @param publisherId publisher id
     * @return current subscriber instance
     * 
     * @see IMessageSubscriber#version(String)
     * @see IMessageSubscriber#dataGroup(String)
     */
    public IMessageSubscriber publisherId(String publisherId);

    /**
     * Provide the version of publisher to describe, (mandatory)<br/>
     * Which is used to determine MQ Queue Name (with publisher id, and data
     * group)<br/>
     *
     * @param version publisher version
     * @return current subscriber instance
     * 
     * @see IMessageSubscriber#publisherId(String)
     * @see IMessageSubscriber#dataGroup(String)
     */
    public IMessageSubscriber version(String version);

    /**
     * Provide the data group of publisher to describe, (NOT mandatory)<br/>
     * Which is used to determine MQ Queue Name (with publisher id, and
     * version)<br/>
     * 
     * @param dataGroup data group of current message
     * @return current subscriber instance
     * 
     * @see IMessageSubscriber#publisherId(String)
     * @see IMessageSubscriber#version(String)
     */
    public IMessageSubscriber dataGroup(String dataGroup);

    /**
     * Add the listener which to receive and process messages from publisher<br/>
     * Could call this method multi-times to add multiple listeners<br/>
     * 
     * @param listener the message receiver and handler
     * @return current subscriber instance
     */
    public IMessageSubscriber addMessageListener(IMessageListener listener);

    /**
     * set the consumer id which will be brought to {@link IMessageListener} <br/>
     * if not set consumer id via this method, null consumerTag will be passed to
     * {@link DeliverCallback#}<br/>
     * 
     * @param consumerId consumer id
     */
    public IMessageSubscriber consumerId(String consumerId);

    /**
     * Subscribe the message from specific publisher + version + data group<br/>
     * Subscriber could get message from Message Queue Server which publisher
     * created (so the MQ topic name should be consistent with publisher)<br/>
     * 
     */
    public void subscribe();
}
