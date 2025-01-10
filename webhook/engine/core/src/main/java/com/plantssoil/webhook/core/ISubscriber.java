package com.plantssoil.webhook.core;

import java.util.List;

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
	 * Add webhook<br/>
	 * Only put the webhook into memory without persistence.<br/>
	 * Should call {@link IRegistry#updateSubscriber(ISubscriber)} if need to reload
	 * consumer for subscriber or to save Persistence Service)
	 * 
	 * @param webhook webhook to add
	 */
	public void addWebhook(IWebhook webhook);

	/**
	 * Update existing webhook<br/>
	 * Only put the webhook into memory without persistence.<br/>
	 * Should call {@link IRegistry#updateSubscriber(ISubscriber)} if need to reload
	 * consumer for subscriber or to update in Persistence Service)
	 * 
	 * @param webhook webhook to update
	 */
	public void updateWebhook(IWebhook webhook);

	/**
	 * Remove existing webhook<br/>
	 * Only put the webhook into memory without persistence.<br/>
	 * Should call {@link IRegistry#updateSubscriber(ISubscriber)} if need to reload
	 * consumer for subscriber or to delete from Persistence Service)
	 * 
	 * @param webhook webhook to remove
	 */
	public void removeWebhook(IWebhook webhook);

	/**
	 * Get the subscriber id
	 * 
	 * @return subscriber id
	 */
	public String getSubscriberId();

	/**
	 * Find all webhooks, support pagination
	 * 
	 * @param page     the page index
	 * @param pageSize maximum events in current page
	 * @return webhook in current page
	 */
	public List<IWebhook> findWebhooks(int page, int pageSize);
}
