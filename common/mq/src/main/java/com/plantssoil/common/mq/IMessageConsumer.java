package com.plantssoil.common.mq;

/**
 * Consume message from specific publisher + version + data group<br/>
 * Consumer could get messages from Message Queue Server which publisher created
 * (so the MQ Queue Name should be consistent with publisher's Queue Name)<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 4:55:17 pm
 */
public interface IMessageConsumer {
    /**
     * Provide the identity of publisher to consume, (mandatory)<br/>
     * Which is used to determine MQ Queue Name (with version, and data group)<br/>
     * Mostly it's the organization id of the publisher<br/>
     * 
     * @param publisherId publisher id
     * @return current consumer instance
     * 
     * @see IMessageConsumer#version(String)
     * @see IMessageConsumer#dataGroup(String)
     */
    public IMessageConsumer publisherId(String publisherId);

    /**
     * Provide the version of publisher to consume, (mandatory)<br/>
     * Which is used to determine MQ Queue Name (with publisher id, and data
     * group)<br/>
     *
     * @param version publisher version
     * @return current consumer instance
     * 
     * @see IMessageConsumer#publisherId(String)
     * @see IMessageConsumer#dataGroup(String)
     */
    public IMessageConsumer version(String version);

    /**
     * Provide the data group of publisher to consume, (NOT mandatory)<br/>
     * Which is used to determine MQ Queue Name (with publisher id, and
     * version)<br/>
     * 
     * @param dataGroup data group of current message
     * @return current consumer instance
     * 
     * @see IMessageConsumer#publisherId(String)
     * @see IMessageConsumer#version(String)
     */
    public IMessageConsumer dataGroup(String dataGroup);

    /**
     * Add the listener which to receive and process messages from publisher<br/>
     * Could call this method multi-times to add multiple listeners<br/>
     * 
     * @param listener the message receiver and handler
     * @return current consumer instance
     */
    public IMessageConsumer addMessageListener(IMessageListener listener);

    /**
     * set the consumer id which will be brought to {@link IMessageListener} <br/>
     * if not set consumer id via this method, null consumerTag will be passed to
     * {@link DeliverCallback#}<br/>
     * 
     * @param consumerId consumer id
     */
    public IMessageConsumer consumerId(String consumerId);

    /**
     * Consume the message from specific publisher + version + data group<br/>
     * Consumer could get message from Message Queue Server which publisher created
     * (so the MQ topic name should be consistent with publisher)<br/>
     * 
     */
    public void consume();
}
