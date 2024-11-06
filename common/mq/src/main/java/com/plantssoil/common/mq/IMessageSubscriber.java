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
     * Subscribe the message from specific publisher + version + data group<br/>
     * Subscriber could get message from Message Queue Server which publisher
     * created (so the MQ topic name should be consistent with publisher)<br/>
     * 
     * @param publisherId The publisher id (in most practice may be the organization
     *                    id)
     * @param version     The application version
     * @param dataGroup   Some organization need separate data between internal
     *                    business unit or merchants, could separate data with this
     *                    parameter
     */
    public void subscribe(String publisherId, String version, String dataGroup);

    /**
     * Add the listener which to receive and process messages from publisher
     * 
     * @param listener the message receiver and handler
     */
    public void addMessageListener(IMessageListener listener);

    /**
     * set the consumer id as the consumerTag for DeliverCallback method<br/>
     * if not set consumer id via this method, null consumerTag will be passed to
     * {@link DeliverCallback#}<br/>
     * 
     * @param consumerId
     */
    public void setConsumerId(String consumerId);
}
