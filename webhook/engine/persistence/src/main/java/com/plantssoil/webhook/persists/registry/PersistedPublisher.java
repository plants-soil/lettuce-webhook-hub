package com.plantssoil.webhook.persists.registry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IEntityQuery.FilterOperator;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.beans.DataGroup;
import com.plantssoil.webhook.beans.Event;
import com.plantssoil.webhook.beans.Publisher;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.persists.exception.EnginePersistenceException;

/**
 * The publisher implementation with persistence<br/>
 * Used by {@link PersistedRegistry}<br/>
 * 
 * @author danialdy
 *
 */
public class PersistedPublisher implements IPublisher {
	private Publisher publisherBean;
	private List<IEvent> eventsAdded = new ArrayList<>();
	private List<String> dataGroupsAdded = new ArrayList<>();

	/**
	 * Default Constructor
	 */
	public PersistedPublisher() {
	}

	/**
	 * Constructor base on publisher bean, used for query result
	 * 
	 * @param publisherBean the publisher bean
	 */
	public PersistedPublisher(Publisher publisherBean) {
		this.publisherBean = publisherBean;
	}

	/**
	 * Get the publisher bean
	 * 
	 * @return the publisher bean
	 */
	Publisher getPublisherBean() {
		if (this.publisherBean == null) {
			this.publisherBean = new Publisher();
			this.publisherBean.setPublisherId(EntityUtils.getInstance().createUniqueObjectId());
		}
		return this.publisherBean;
	}

	/**
	 * Get the events added
	 * 
	 * @return the events added
	 */
	List<IEvent> getEventsAdded() {
		return this.eventsAdded;
	}

	/**
	 * Get the data groups added
	 * 
	 * @return the data groups added
	 */
	List<String> getDataGroupAdded() {
		return this.dataGroupsAdded;
	}

	/**
	 * Set organization id which current publisher belongs to
	 * 
	 * @param organizationId the organization id
	 */
	public void setOrganizationId(String organizationId) {
		getPublisherBean().setOrganizationId(organizationId);
	}

	/**
	 * Get organization id which current publisher belongs to
	 * 
	 * @return the organization id
	 */
	public String getOrganizationId() {
		return getPublisherBean().getOrganizationId();
	}

	/**
	 * Set created by
	 * 
	 * @param createdBy created by
	 */
	public void setCreatedBy(String createdBy) {
		getPublisherBean().setCreatedBy(createdBy);
	}

	/**
	 * Get created by
	 * 
	 * @return created by
	 */
	public String getCreatedBy() {
		return getPublisherBean().getCreatedBy();
	}

	/**
	 * Get creation time
	 * 
	 * @param creationTime creation time
	 */
	public void setCreationTime(Date creationTime) {
		getPublisherBean().setCreationTime(creationTime);
	}

	/**
	 * Set creation time
	 * 
	 * @return creation time
	 */
	public Date getCreationTime() {
		return getPublisherBean().getCreationTime();
	}

	@Override
	public void setPublisherId(String publisherId) {
		getPublisherBean().setPublisherId(publisherId);
	}

	@Override
	public void setSupportDataGroup(boolean supportDataGroup) {
		getPublisherBean().setSupportDataGroup(supportDataGroup);
	}

	@Override
	public void setVersion(String version) {
		getPublisherBean().setVersion(version);
	}

	@Override
	public void addDataGroup(String dataGroup) {
		this.dataGroupsAdded.add(dataGroup);
	}

	@Override
	public void addDataGroup(List<String> dataGroups) {
		this.dataGroupsAdded.addAll(dataGroups);
	}

	@Override
	public void addEvent(IEvent event) {
		this.eventsAdded.add(event);
	}

	@Override
	public void addEvent(List<IEvent> events) {
		this.eventsAdded.addAll(events);
	}

	@Override
	public String getPublisherId() {
		return getPublisherBean().getPublisherId();
	}

	@Override
	public boolean isSupportDataGroup() {
		return getPublisherBean().isSupportDataGroup();
	}

	@Override
	public String getVersion() {
		return getPublisherBean().getVersion();
	}

	@Override
	public List<String> findDataGroups(int page, int pageSize) {
		if (getPublisherId() == null) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001,
					"Publisher id should not be null when query data groups!");
		}
		IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = factory.create()) {
			IEntityQuery<DataGroup> q = p.createQuery(DataGroup.class).firstResult(page * pageSize).maxResults(pageSize).filter("publisherId",
					FilterOperator.equals, getPublisherId());
			List<DataGroup> datagroups = q.resultList().get();
			List<String> list = new ArrayList<>();
			for (DataGroup datagroup : datagroups) {
				list.add(datagroup.getDataGroup());
			}
			return list;
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001, e);
		}
	}

	@Override
	public List<IEvent> findEvents(int page, int pageSize) {
		if (getPublisherId() == null) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001,
					"Publisher id should not be null when query events!");
		}
		IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = factory.create()) {
			IEntityQuery<Event> q = p.createQuery(Event.class).firstResult(page * pageSize).maxResults(pageSize).filter("publisherId", FilterOperator.equals,
					getPublisherId());
			List<Event> es = q.resultList().get();
			List<IEvent> list = new ArrayList<>();
			for (Event e : es) {
				list.add(new PersistedEvent(e));
			}
			return list;
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001, e);
		}
	}

}
