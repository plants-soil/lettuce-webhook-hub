package com.plantssoil.webhook.persists.registry;

import java.util.ArrayList;
import java.util.List;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IEntityQuery.FilterOperator;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.beans.Subscriber;
import com.plantssoil.webhook.beans.Webhook;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.persists.exception.EnginePersistenceException;

/**
 * The subscriber implementation with persistence<br/>
 * Used by {@link PersistedRegistry}<br/>
 * 
 * @author danialdy
 *
 */
public class PersistedSubscriber implements ISubscriber {
	private Subscriber subscriberBean;
	private List<IWebhook> webhooksAdded = new ArrayList<>();
	private List<IWebhook> webhooksUpdated = new ArrayList<>();
	private List<IWebhook> webhooksRemoved = new ArrayList<>();

	/**
	 * Default Constructor
	 */
	public PersistedSubscriber() {
	}

	/**
	 * Constructor base on subscriber bean, used for query result
	 * 
	 * @param subscriberBean the subscriber bean
	 */
	public PersistedSubscriber(Subscriber subscriberBean) {
		this.subscriberBean = subscriberBean;
	}

	/**
	 * Get the subscriber bean
	 * 
	 * @return the subscriber bean
	 */
	Subscriber getSubscriberBean() {
		if (this.subscriberBean == null) {
			this.subscriberBean = new Subscriber();
			this.subscriberBean.setSubscriberId(EntityUtils.getInstance().createUniqueObjectId());
		}
		return this.subscriberBean;
	}

	/**
	 * Get the webhooks added
	 * 
	 * @return the webhooks new added
	 */
	List<IWebhook> getWebhooksAdded() {
		return this.webhooksAdded;
	}

	/**
	 * Get the webhooks updated
	 * 
	 * @return the webhooks updated
	 */
	List<IWebhook> getWebhooksUpdated() {
		return this.webhooksUpdated;
	}

	/**
	 * Get the webhooks removed
	 * 
	 * @return the webhooks removed
	 */
	List<IWebhook> getWebhooksRemoved() {
		return this.webhooksRemoved;
	}

	/**
	 * Set organization id which current subscriber belongs to
	 * 
	 * @param organizationId the organization id
	 */
	public void setOrganizationId(String organizationId) {
		getSubscriberBean().setOrganizationId(organizationId);
	}

	/**
	 * Get organization id which current subscriber belongs to
	 * 
	 * @return the organization id
	 */
	public String getOrganizationId() {
		return getSubscriberBean().getOrganizationId();
	}

	@Override
	public void setSubscriberId(String subscriberId) {
		getSubscriberBean().setSubscriberId(subscriberId);
	}

	@Override
	public void addWebhook(IWebhook webhook) {
		this.webhooksAdded.add(webhook);
	}

	@Override
	public void updateWebhook(IWebhook webhook) {
		this.webhooksUpdated.add(webhook);
	}

	@Override
	public void removeWebhook(IWebhook webhook) {
		this.webhooksRemoved.add(webhook);
	}

	@Override
	public String getSubscriberId() {
		return getSubscriberBean().getSubscriberId();
	}

	@Override
	public List<IWebhook> findWebhooks(int page, int pageSize) {
		if (getSubscriberId() == null) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21002,
					"Subscriber id should not be null when query webhooks!");
		}
		IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = factory.create()) {
			IEntityQuery<Webhook> q = p.createQuery(Webhook.class).firstResult(page * pageSize).maxResults(pageSize).filter("subscriberId",
					FilterOperator.equals, getSubscriberId());
			List<Webhook> webhooks = q.resultList().get();
			List<IWebhook> list = new ArrayList<>();
			for (Webhook webhook : webhooks) {
				list.add(new PersistedWebhook(webhook));
			}
			return list;
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21002, e);
		}
	}

}
