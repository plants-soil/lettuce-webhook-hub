package com.plantssoil.webhook.persists.registry;

import java.util.ArrayList;
import java.util.List;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IEntityQuery.FilterOperator;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.beans.DataGroup;
import com.plantssoil.webhook.beans.DataGroupSubscribed;
import com.plantssoil.webhook.beans.Event;
import com.plantssoil.webhook.beans.EventSubscribed;
import com.plantssoil.webhook.beans.Publisher;
import com.plantssoil.webhook.beans.Subscriber;
import com.plantssoil.webhook.beans.Webhook;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.impl.AbstractRegistry;
import com.plantssoil.webhook.persists.exception.EnginePersistenceException;

/**
 * The registry implementation base on persistence<br/>
 * All publishers / subscribers and related information will be persisted<br/>
 * 
 * @author danialdy
 *
 */
public class PersistedRegistry extends AbstractRegistry {

	@Override
	public IPublisher findPublisher(String publisherId) {
		try {
			Publisher bean = queryEntity(Publisher.class, publisherId);
			if (bean != null) {
				return new PersistedPublisher(bean);
			}
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
		}
		return null;
	}

	@Override
	public List<IPublisher> findAllPublishers(int page, int pageSize) {
		IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = f.create()) {
			IEntityQuery<Publisher> q = p.createQuery(Publisher.class).firstResult(page * pageSize).maxResults(pageSize);
			List<Publisher> publishers = q.resultList().get();
			List<IPublisher> list = new ArrayList<>();
			for (Publisher publisher : publishers) {
				list.add(new PersistedPublisher(publisher));
			}
			return list;
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
		}
	}

	@Override
	public ISubscriber findSubscriber(String subscriberId) {
		try {
			Subscriber bean = queryEntity(Subscriber.class, subscriberId);
			if (bean != null) {
				return new PersistedSubscriber(bean);
			}
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
		}
		return null;
	}

	private <T> T queryEntity(Class<T> entityClass, String primaryKey) throws Exception {
		IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = f.create()) {
			T bean = p.createQuery(entityClass).singleResult(primaryKey).get();
			return bean;
		}
	}

	@Override
	public List<ISubscriber> findAllSubscribers(int page, int pageSize) {
		IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = f.create()) {
			IEntityQuery<Subscriber> q = p.createQuery(Subscriber.class).firstResult(page * pageSize).maxResults(pageSize);
			List<Subscriber> subscribers = q.resultList().get();
			List<ISubscriber> list = new ArrayList<>();
			for (Subscriber subscriber : subscribers) {
				list.add(new PersistedSubscriber(subscriber));
			}
			return list;
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
		}
	}

	@Override
	protected void saveNewPublisher(IPublisher publisher) {
		List<Object> entitiesToSave = prepareEntitiesForNewPublisher(publisher);
		// save
		IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = f.create()) {
			p.create(entitiesToSave);
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
		}
	}

	private List<Object> prepareEntitiesForNewPublisher(IPublisher publisher) {
		if (!(publisher instanceof PersistedPublisher)) {
			String msg = String.format("Only the instance of %s could be persisted!", PersistedPublisher.class.getName());
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, msg);
		}
		List<Object> entitiesToSave = new ArrayList<>();

		// prepare publisher bean
		PersistedPublisher pp = (PersistedPublisher) publisher;
		entitiesToSave.add(pp.getPublisherBean());

		// prepare event beans
		List<IEvent> events = pp.getEventsAdded();
		for (IEvent event : events) {
			if (!(event instanceof PersistedEvent)) {
				String msg = String.format("Only the instance of %s could be persisted!", PersistedEvent.class.getName());
				throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, msg);
			}
			PersistedEvent pe = (PersistedEvent) event;
			if (pe.getPublisherId() == null) {
				pe.setPublisherId(pp.getPublisherId());
			}
			entitiesToSave.add(pe.getEventBean());
		}

		// prepare data group beans
		List<String> dataGroups = pp.getDataGroupAdded();
		for (String dataGroup : dataGroups) {
			DataGroup dg = new DataGroup();
			dg.setDataGroupId(EntityUtils.getInstance().createUniqueObjectId());
			dg.setDataGroup(dataGroup);
			dg.setPublisherId(pp.getPublisherId());
			entitiesToSave.add(dg);
		}

		return entitiesToSave;
	}

	@Override
	protected void saveUpdatedPublisher(IPublisher publisher) {
		List<Object> entitiesToDelete = prepareEntitesForDeletePublisher(publisher);
		List<Object> entitiesToSave = prepareEntitiesForNewPublisher(publisher);
		// remove and create
		IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = f.create()) {
			p.remove(entitiesToDelete);
			p.create(entitiesToSave);
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
		}
	}

	@Override
	protected void deletePublisher(IPublisher publisher) {
		List<Object> entitiesToDelete = prepareEntitesForDeletePublisher(publisher);
		// remove and create
		IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = f.create()) {
			p.remove(entitiesToDelete);
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
		}
	}

	private List<Object> prepareEntitesForDeletePublisher(IPublisher publisher) {
		if (!(publisher instanceof PersistedPublisher)) {
			String msg = String.format("Only the instance of %s could be persisted!", PersistedPublisher.class.getName());
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, msg);
		}
		List<Object> entitiesToDelete = new ArrayList<>();

		// prepare publisher bean
		PersistedPublisher pp = (PersistedPublisher) publisher;
		entitiesToDelete.add(pp.getPublisherBean());

		// prepare event beans
		List<Event> es = queryEntities(Event.class, "publisherId", publisher.getPublisherId());
		entitiesToDelete.addAll(es);

		// prepare data group beans
		List<DataGroup> dgs = queryEntities(DataGroup.class, "publisherId", publisher.getPublisherId());
		entitiesToDelete.addAll(dgs);

		return entitiesToDelete;
	}

	private <T> List<T> queryEntities(Class<T> clazz, String filterAttribute, String attributeValue) {
		IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
		List<T> list = new ArrayList<>();
		try (IPersistence p = f.create()) {
			int page = 0, pageSize = 20;
			List<T> rs = p.createQuery(clazz).filter(filterAttribute, FilterOperator.equals, attributeValue).firstResult(page * pageSize).maxResults(pageSize)
					.resultList().get();
			while (rs.size() > 0) {
				list.addAll(rs);
				if (rs.size() < pageSize) {
					break;
				}
				page++;
				rs = p.createQuery(clazz).filter(filterAttribute, FilterOperator.equals, attributeValue).firstResult(page * pageSize).maxResults(pageSize)
						.resultList().get();
			}
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
		}
		return list;
	}

	@Override
	protected void saveNewSubscriber(ISubscriber subscriber) {
		List<Object> entitiesToSave = prepareEntitiesForNewSubscriber(subscriber);
		// save
		IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = f.create()) {
			p.create(entitiesToSave);
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
		}
	}

	private List<Object> prepareEntitiesForNewSubscriber(ISubscriber subscriber) {
		if (!(subscriber instanceof PersistedSubscriber)) {
			String msg = String.format("Only the instance of %s could be persisted!", PersistedSubscriber.class.getName());
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, msg);
		}
		List<Object> entitiesToSave = new ArrayList<>();

		// prepare subscriber bean
		PersistedSubscriber ps = (PersistedSubscriber) subscriber;
		entitiesToSave.add(ps.getSubscriberBean());

		List<IWebhook> webhooks = ps.getWebhooksAdded();
		for (IWebhook webhook : webhooks) {
			// prepare webhook bean
			if (!(webhook instanceof PersistedWebhook)) {
				String msg = String.format("Only the instance of %s could be persisted!", PersistedWebhook.class.getName());
				throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, msg);
			}
			PersistedWebhook pw = (PersistedWebhook) webhook;
			if (pw.getSubscriberId() == null) {
				pw.setSubscriberId(ps.getSubscriberId());
			}
			entitiesToSave.add(pw.getWebhookBean());

			// prepare event subscribed beans
			List<IEvent> events = pw.getEventsSubscribed();
			for (IEvent event : events) {
				EventSubscribed es = new EventSubscribed();
				es.setEventSubedId(EntityUtils.getInstance().createUniqueObjectId());
				es.setEventId(event.getEventId());
				es.setWebhookId(webhook.getWebhookId());
				entitiesToSave.add(es);
			}

			// prepare data group subscribed beans
			List<IDataGroup> dataGroups = pw.getDataGroupsSubscribed();
			for (IDataGroup dataGroup : dataGroups) {
				if (!(dataGroup instanceof PersistedDataGroup)) {
					String msg = String.format("Only the instance of %s could be persisted!", PersistedDataGroup.class.getName());
					throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, msg);
				}
				DataGroupSubscribed dgs = new DataGroupSubscribed();
				dgs.setDataGroupSubedId(EntityUtils.getInstance().createUniqueObjectId());
				dgs.setWebhookId(webhook.getWebhookId());
				dgs.setDataGroupId(((PersistedDataGroup) dataGroup).getDataGroup());
				entitiesToSave.add(dgs);
			}
		}
		return entitiesToSave;
	}

	@Override
	protected void saveUpdatedSubscriber(ISubscriber subscriber) {
		List<Object> entitiesToDelete = prepareEntitiesForDeleteSubscriber(subscriber);
		List<Object> entitiesToSave = prepareEntitiesForNewSubscriber(subscriber);
		// remove and create
		IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = f.create()) {
			p.remove(entitiesToDelete);
			p.create(entitiesToSave);
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
		}
	}

	@Override
	protected void deleteSubscriber(ISubscriber subscriber) {
		List<Object> entitiesToDelete = prepareEntitiesForDeleteSubscriber(subscriber);
		// remove and create
		IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
		try (IPersistence p = f.create()) {
			p.remove(entitiesToDelete);
		} catch (Exception e) {
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
		}
	}

	private List<Object> prepareEntitiesForDeleteSubscriber(ISubscriber subscriber) {
		if (!(subscriber instanceof PersistedSubscriber)) {
			String msg = String.format("Only the instance of %s could be persisted!", PersistedSubscriber.class.getName());
			throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, msg);
		}
		List<Object> entitiesToDelete = new ArrayList<>();

		// prepare subscriber bean
		PersistedSubscriber ps = (PersistedSubscriber) subscriber;
		entitiesToDelete.add(ps.getSubscriberBean());

		// prepare webhooks to delete
		List<Webhook> webhooks = queryEntities(Webhook.class, "subscriberId", subscriber.getSubscriberId());
		entitiesToDelete.addAll(webhooks);

		for (Webhook webhook : webhooks) {
			List<EventSubscribed> ess = queryEntities(EventSubscribed.class, "webhookId", webhook.getWebhookId());
			entitiesToDelete.addAll(ess);
			List<DataGroupSubscribed> dgs = queryEntities(DataGroupSubscribed.class, "webhookId", webhook.getWebhookId());
			entitiesToDelete.addAll(dgs);
		}
		return entitiesToDelete;
	}

}
