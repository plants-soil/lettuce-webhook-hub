package com.plantssoil.webhook.core;

/**
 * The subscriber, each instance of the ISubscriber presents one subscriber<br/>
 * <p>
 * Subscriber could have multiple webhooks (webhook could be considered as
 * client application, subscriber could have multiple webhooks if have multiple
 * client application).
 * </p>
 * If subscriber's any property changed, especially the webhooks added / updated
 * / removed, such as security strategy / request headers / webhook id / webhook
 * secret / events subscribed / etc. changed, should call
 * {@link IRegistry#updateSubscriber(ISubscriber)} to reload consumer for
 * subscriber
 * 
 * @author danialdy
 * @Date 29 Nov 2024 1:41:54 pm
 */
public interface ISubscriber {
    /**
     * Set the subscriber id
     * 
     * @param subscriberId subscriber id
     */
    public void setSubscriberId(String subscriberId);

    /**
     * Get the subscriber id
     * 
     * @return subscriber id
     */
    public String getSubscriberId();

    /**
     * Get the organization id which current publisher belongs to
     * 
     * @return The organization id
     */
    public String getOrganizationId();

    /**
     * Set the organization id which current publisher belongs to
     * 
     * @param organizationId The organization id
     */
    public void setOrganizationId(String organizationId);
}
