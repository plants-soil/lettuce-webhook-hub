package com.plantssoil.webhook.persists.registry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IEntityQuery.FilterOperator;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.beans.DataGroup;
import com.plantssoil.webhook.beans.DataGroupSubscribed;
import com.plantssoil.webhook.beans.Event;
import com.plantssoil.webhook.beans.EventSubscribed;
import com.plantssoil.webhook.beans.Webhook;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.persists.exception.EnginePersistenceException;

/**
 * The IWebhook bean implementation with persistence<br/>
 * Used by {@link PersistedRegistry}<br/>
 * 
 * @author danialdy
 *
 */
public class PersistedWebhook implements IWebhook {
	private Webhook webhookBean;
	private List<IEvent> eventsSubscribed = new ArrayList<>();
	private List<IDataGroup> dataGroupsSubscribed = new ArrayList<>();

	/**
	 * Default constructor
	 */
	public PersistedWebhook() {
	}

	/**
	 * Constructor base on webhook bean, used for query result
	 * 
	 * @param webhookBean webhook bean
	 */
	public PersistedWebhook(Webhook webhookBean) {
		this.webhookBean = webhookBean;
	}

	/**
	 * Get the webhook bean
	 * 
	 * @return the webhook bean
	 */
	Webhook getWebhookBean() {
		if (this.webhookBean == null) {
			this.webhookBean = new Webhook();
			this.webhookBean.setWebhookId(EntityUtils.getInstance().createUniqueObjectId());
		}
		return this.webhookBean;
	}

	/**
	 * Set app name presents webhook
	 * 
	 * @param appName the app name
	 */
	public void setAppName(String appName) {
		getWebhookBean().setAppName(appName);
	}

	/**
	 * Get app name
	 * 
	 * @return the app name
	 */
	public String getAppName() {
		return getWebhookBean().getAppName();
	}

	/**
	 * Set app tag
	 * 
	 * @param appTag the app tag
	 */
	public void setAppTag(String appTag) {
		getWebhookBean().setAppTag(appTag);
	}

	/**
	 * Get app tag
	 * 
	 * @return the app tag
	 */
	public String getAppTag() {
		return getWebhookBean().getAppTag();
	}

	/**
	 * Set the description of webhook
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		getWebhookBean().setDescription(description);
	}

	/**
	 * Get the description of webhook
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return getWebhookBean().getDescription();
	}

	/**
	 * Set created by
	 * 
	 * @param createdBy created by
	 */
	public void setCreatedBy(String createdBy) {
		getWebhookBean().setCreatedBy(createdBy);
	}

	/**
	 * Get created by
	 * 
	 * @return created by
	 */
	public String getCreatedBy() {
		return getWebhookBean().getCreatedBy();
	}

	/**
	 * Get creation time
	 * 
	 * @param creationTime creation time
	 */
	public void setCreationTime(Date creationTime) {
		getWebhookBean().setCreationTime(creationTime);
	}

	/**
	 * Set creation time
	 * 
	 * @return creation time
	 */
	public Date getCreationTime() {
		return getWebhookBean().getCreationTime();
	}

	/**
	 * Get the events newly subscribed
	 * 
	 * @return the events added
	 */
	List<IEvent> getEventsSubscribed() {
		return this.eventsSubscribed;
	}

	/**
	 * Get the data groups newly subscribed
	 * 
	 * @return the data groups added
	 */
	List<IDataGroup> getDataGroupsSubscribed() {
		return this.dataGroupsSubscribed;
	}

	@Override
	public void setWebhookId(String webhookId) {
		getWebhookBean().setWebhookId(webhookId);
	}

	@Override
	public void setSubscriberId(String subscriberId) {
		getWebhookBean().setSubscriberId(subscriberId);
	}

	@Override
	public void setWebhookSecret(String webhookSecret) {
		getWebhookBean().setWebhookSecret(webhookSecret);
	}

	@Override
	public void setSecurityStrategy(SecurityStrategy securityStrategy) {
		getWebhookBean().setSecurityStrategy(Webhook.SecurityStrategy.valueOf(securityStrategy.name()));
	}

	@Override
	public void setWebhookUrl(String webhookUrl) {
		getWebhookBean().setWebhookUrl(webhookUrl);
	}

	@Override
	public void setCustomizedHeaders(Map<String, String> customizedHeaders) {
		getWebhookBean().setCustomizedHeaders(customizedHeaders);
	}

	@Override
	public void setTrustedIps(String[] trustedIps) {
		getWebhookBean().setTrustedIps(trustedIps);
	}

	@Override
	public void setWebhookStatus(WebhookStatus webhookStatus) {
		getWebhookBean().setWebhookStatus(Webhook.WebhookStatus.valueOf(webhookStatus.name()));
	}

	@Override
	public void setPublisherId(String publisherId) {
		getWebhookBean().setPublisherId(publisherId);
	}

	@Override
	public void setPubliserhVersion(String publisherVersion) {
		getWebhookBean().setPublisherVersion(publisherVersion);
	}

	@Override
	public void setAccessToken(String accessToken) {
		getWebhookBean().setAccessToken(accessToken);
	}

	@Override
	public void setRefreshToken(String refreshToken) {
		getWebhookBean().setRefreshToken(refreshToken);
	}

	@Override
	public void subscribeEvent(IEvent event) {
		this.eventsSubscribed.add(event);
	}

	@Override
	public void subscribeEvent(List<IEvent> events) {
		this.eventsSubscribed.addAll(events);
	}

	@Override
	public void subscribeDataGroup(IDataGroup dataGroup) {
		this.dataGroupsSubscribed.add(dataGroup);
	}

	@Override
	public void subscribeDataGroup(List<IDataGroup> dataGroups) {
		this.dataGroupsSubscribed.addAll(dataGroups);
	}

	@Override
	public String getWebhookId() {
		return getWebhookBean().getWebhookId();
	}

	@Override
	public String getSubscriberId() {
		return getWebhookBean().getSubscriberId();
	}

	@Override
	public String getWebhookSecret() {
		return getWebhookBean().getWebhookSecret();
	}

	@Override
	public SecurityStrategy getSecurityStrategy() {
		return IWebhook.SecurityStrategy.valueOf(getWebhookBean().getSecurityStrategy().name());
	}

	@Override
	public String getWebhookUrl() {
		return getWebhookBean().getWebhookUrl();
	}

	@Override
	public Map<String, String> getCustomizedHeaders() {
		return getWebhookBean().getCustomizedHeaders();
	}

	@Override
	public String[] getTrustedIps() {
		return getWebhookBean().getTrustedIps();
	}

	@Override
	public WebhookStatus getWebhookStatus() {
		return IWebhook.WebhookStatus.valueOf(getWebhookBean().getWebhookStatus().name());
	}

	@Override
	public String getPublisherId() {
		return getWebhookBean().getPublisherId();
	}

	@Override
	public String getPublisherVersion() {
		return getWebhookBean().getPublisherVersion();
	}

	@Override
	public String getAccessToken() {
		return getWebhookBean().getAccessToken();
	}

	@Override
	public String getRefreshToken() {
		return getWebhookBean().getRefreshToken();
	}

	@Override
	public List<IEvent> findSubscribedEvents(int page, int pageSize) {
		if (getWebhookId() == null) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21003,
					"Webhook id should not be null when query subscribed events!");
		}
		IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = factory.create()) {
			IEntityQuery<EventSubscribed> q = p.createQuery(EventSubscribed.class).firstResult(page * pageSize).maxResults(pageSize).filter("webhookId",
					FilterOperator.equals, getWebhookId());
			List<EventSubscribed> eventsSubscribed = q.resultList().get();
			List<IEvent> list = new ArrayList<>();
			for (EventSubscribed eventSubscribed : eventsSubscribed) {
				list.add(new PersistedEvent(queryEntity(p, Event.class, eventSubscribed.getEventId())));
			}
			return list;
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21003, e);
		}
	}

	@Override
	public List<IDataGroup> findSubscribedDataGroups(int page, int pageSize) {
		if (getWebhookId() == null) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21003,
					"Webhook id should not be null when query subscribed data groups!");
		}
		IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = factory.create()) {
			IEntityQuery<DataGroupSubscribed> q = p.createQuery(DataGroupSubscribed.class).firstResult(page * pageSize).maxResults(pageSize).filter("webhookId",
					FilterOperator.equals, getWebhookId());
			List<DataGroupSubscribed> dataGroupsSubscribed = q.resultList().get();
			List<IDataGroup> list = new ArrayList<>();
			for (DataGroupSubscribed dataGroupSubscribed : dataGroupsSubscribed) {
				list.add(new PersistedDataGroup(queryEntity(p, DataGroup.class, dataGroupSubscribed.getDataGroupId())));
			}
			return list;
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21003, e);
		}
	}

	private <T> T queryEntity(IPersistence p, Class<T> entityClass, String primaryKey) throws Exception {
		return p.createQuery(entityClass).singleResult(primaryKey).get();
	}

	@Override
	public IDataGroup findSubscribedDataGroup(String dataGroup) {
		int page = 0, pageSize = 10;
		List<IDataGroup> dgs = findSubscribedDataGroups(page, pageSize);
		while (dgs.size() > 0) {
			for (IDataGroup dg : dgs) {
				if (dataGroup.equals(dg.getDataGroup())) {
					return dg;
				}
			}
			if (dgs.size() < pageSize) {
				break;
			}
			page++;
			dgs = findSubscribedDataGroups(page, pageSize);
		}
		return null;
	}

}
