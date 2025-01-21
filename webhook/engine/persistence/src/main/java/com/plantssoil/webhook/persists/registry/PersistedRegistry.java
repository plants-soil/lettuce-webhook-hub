package com.plantssoil.webhook.persists.registry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IEntityQuery.FilterOperator;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IOrganization;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;
import com.plantssoil.webhook.core.impl.AbstractRegistry;
import com.plantssoil.webhook.persists.beans.DataGroup;
import com.plantssoil.webhook.persists.beans.DataGroupSubscribed;
import com.plantssoil.webhook.persists.beans.Event;
import com.plantssoil.webhook.persists.beans.EventSubscribed;
import com.plantssoil.webhook.persists.beans.Organization;
import com.plantssoil.webhook.persists.beans.Publisher;
import com.plantssoil.webhook.persists.beans.Subscriber;
import com.plantssoil.webhook.persists.beans.Webhook;
import com.plantssoil.webhook.persists.exception.EnginePersistenceException;

/**
 * The registry implementation base on persistence<br/>
 * All publishers / subscribers and related information will be persisted<br/>
 * The persistence strategy depends on the Lettuce Configuration:
 * {@link LettuceConfiguration#PERSISTENCE_FACTORY_CONFIGURABLE}
 * 
 * @see LettuceConfiguration#PERSISTENCE_DATABASE_URL
 * @see LettuceConfiguration#PERSISTENCE_DATABASE_USERNAME
 * @see LettuceConfiguration#PERSISTENCE_DATABASE_PASSWORD
 * @see LettuceConfiguration#PERSISTENCE_DATABASE_POOLSIZE
 * @see LettuceConfiguration#RDBMS_DATASOURCE
 * @see LettuceConfiguration#RDBMS_DATASOURCE_DIALECT
 * @ses LettuceConfiguration#RDBMS_DATABASE_DRIVER
 * @author danialdy
 *
 */
public class PersistedRegistry extends AbstractRegistry {

    @Override
    public void addOrganization(IOrganization organization) {
        super.addOrganization(organization);
        if (!(organization instanceof Organization)) {
            String msg = String.format("Only the instance of %s could be persisted!", Organization.class.getName());
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21005, msg);
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            if (p.createQuery(Organization.class).singleResult(organization.getOrganizationId()).get() != null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001,
                        String.format("The organization (organizationId: %s) arealdy exists!", organization.getOrganizationId()));
            }
            // save
            p.create(organization);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001, e);
        }
    }

    @Override
    public void updateOrganization(IOrganization organization) {
        super.updateOrganization(organization);
        if (!(organization instanceof Organization)) {
            String msg = String.format("Only the instance of %s could be persisted!", Organization.class.getName());
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21005, msg);
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            if (p.createQuery(Organization.class).singleResult(organization.getOrganizationId()).get() == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001,
                        String.format("The organization (organizationId: %s) does not exists!", organization.getOrganizationId()));
            }
            // save
            p.update(organization);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001, e);
        }
    }

    @Override
    protected void saveNewPublisher(IPublisher publisher) {
        if (!(publisher instanceof Publisher)) {
            String msg = String.format("Only the instance of %s could be persisted!", Publisher.class.getName());
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001, msg);
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            // check organization exists or not
            if (p.createQuery(Organization.class).singleResult(publisher.getOrganizationId()).get() == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001,
                        String.format("The organization (organizationId: %s) does not exists!", publisher.getOrganizationId()));
            }
            // check publisher id is unique or not, publisher id should be unique within
            // global
            Publisher existingPublisher = p.createQuery(Publisher.class).filter("publisherId", FilterOperator.equals, publisher.getPublisherId()).singleResult()
                    .get();
            if (existingPublisher != null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001,
                        String.format("The publisher (publisherId: %s) arealdy exists!", publisher.getPublisherId()));
            }
            // check version is unique or not (version should be unique within one
            // organizationId)
            Set<String> existingVersions = existingUniqueValues(p, Publisher.class, "organizationId", ((Publisher) publisher).getOrganizationId(),
                    Publisher.class.getMethod("getVersion"));
            if (existingVersions.contains(publisher.getVersion())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001,
                        String.format("The publisher version (%s) arealdy exists!", publisher.getVersion()));
            }
            // save
            p.create(publisher);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21001, e);
        }
    }

    @Override
    protected void saveUpdatedPublisher(IPublisher publisher) {
        if (!(publisher instanceof Publisher)) {
            String msg = String.format("Only the instance of %s could be persisted!", Publisher.class.getName());
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21002, msg);
        }
        // persist
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            Publisher old = p.createQuery(Publisher.class).singleResult(publisher.getPublisherId()).get();
            if (old == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21002,
                        String.format("The publisher (publisher id: %s) to be update does not exists!", publisher.getPublisherId()));
            }
            // the "support data group" & "version" attribute can't be changed
            if ((old.isSupportDataGroup() && !publisher.isSupportDataGroup()) || (!old.isSupportDataGroup() && publisher.isSupportDataGroup())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21002,
                        String.format("The property (support data group, old: %s, updated: %s) could not be changed!", old.isSupportDataGroup(),
                                publisher.isSupportDataGroup()));
            }
            if (!Objects.equals(old.getVersion(), publisher.getVersion())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21002,
                        String.format("The property (version, old: %s, updated: %s) could not be changed!", old.getVersion(), publisher.getVersion()));
            }
            if (!Objects.equals(old.getOrganizationId(), publisher.getOrganizationId())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21002, String.format(
                        "The property (organization id, old: %s, updated: %s) could not be changed!", old.getOrganizationId(), publisher.getOrganizationId()));
            }
            p.update(publisher);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21002, e);
        }
    }

    @Override
    protected void saveNewEvent(String publisherId, List<IEvent> events) {
        Set<String> eventTypes = new HashSet<>();
        // prepare event bean
        for (IEvent event : events) {
            if (!(event instanceof Event)) {
                String msg = String.format("Only the instance of %s could be persisted!", Event.class.getName());
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21003, msg);
            }
            // should avoid duplicated event type
            if (eventTypes.contains(event.getEventType())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21003,
                        String.format("The event type shoule be unique, however (%s) is duplicated!", event.getEventType()));
            }
            eventTypes.add(event.getEventType());
            ((Event) event).setPublisherId(publisherId);
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            if (p.createQuery(Publisher.class).singleResult(publisherId).get() == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21003,
                        String.format("The publisher (publisherId: %s) does not exist!", publisherId));
            }
            // should avoid event type duplicated with existing event types from persistence
            Set<String> existingEventTypes = existingUniqueValues(p, Event.class, "publisherId", publisherId, Event.class.getMethod("getEventType"));
            for (IEvent event : events) {
                if (existingEventTypes.contains(event.getEventType())) {
                    throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21003,
                            String.format("The event type shoule be unique, however (%s) is duplicated!", event.getEventType()));
                }
            }
            // persist
            p.create(events);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21003, e);
        }
    }

    @Override
    protected void saveNewDataGroup(String publisherId, List<IDataGroup> dataGroups) {
        Set<String> dataGroupNames = new HashSet<>();
        // prepare data group bean
        for (IDataGroup dataGroup : dataGroups) {
            if (!(dataGroup instanceof DataGroup)) {
                String msg = String.format("Only the instance of %s could be persisted!", DataGroup.class.getName());
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, msg);
            }
            if (dataGroupNames.contains(dataGroup.getDataGroup())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004,
                        String.format("The data group shoule be unique, however (%s) is duplicated!", dataGroup.getDataGroup()));
            }
            dataGroupNames.add(dataGroup.getDataGroup());
            ((DataGroup) dataGroup).setPublisherId(publisherId);
        }
        // persist
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            if (p.createQuery(Publisher.class).singleResult(publisherId).get() == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21003,
                        String.format("The publisher (publisherId: %s) does not exist!", publisherId));
            }
            Set<String> existingDataGroupsNames = this.existingUniqueValues(p, DataGroup.class, "publisherId", publisherId,
                    DataGroup.class.getMethod("getDataGroup"));
            for (IDataGroup dataGroup : dataGroups) {
                if (existingDataGroupsNames.contains(dataGroup.getDataGroup())) {
                    throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004,
                            String.format("The data group shoule be unique, however (%s) is duplicated!", dataGroup.getDataGroup()));
                }
            }
            p.create(dataGroups);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21004, e);
        }
    }

    @Override
    protected void saveNewSubscriber(ISubscriber subscriber) {
        if (!(subscriber instanceof Subscriber)) {
            String msg = String.format("Only the instance of %s could be persisted!", Subscriber.class.getName());
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21005, msg);
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            // check organization exists or not
            if (p.createQuery(Organization.class).singleResult(subscriber.getOrganizationId()).get() == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21005,
                        String.format("The organization (organizationId: %s) does not exists!", subscriber.getOrganizationId()));
            }
            // check the subscriber exists or not
            Subscriber existingSubscriber = p.createQuery(Subscriber.class).singleResult(subscriber.getSubscriberId()).get();
            if (existingSubscriber != null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21005,
                        String.format("The subscriber (subscriberId: %s) arealdy exists!", subscriber.getSubscriberId()));
            }
            // save
            p.create(subscriber);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21005, e);
        }
    }

    @Override
    protected void saveUpdatedSubscriber(ISubscriber subscriber) {
        if (!(subscriber instanceof Subscriber)) {
            String msg = String.format("Only the instance of %s could be persisted!", Subscriber.class.getName());
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21006, msg);
        }
        if (subscriber.getSubscriberId() == null || subscriber.getOrganizationId() == null) {
            String msg = String.format("The attributes (%s, %s) are required!", "subscriberId", "organizationId");
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21006, msg);
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            // check the subscriber exists or not
            Subscriber old = p.createQuery(Subscriber.class).singleResult(subscriber.getSubscriberId()).get();
            if (old == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21006,
                        String.format("The subscriber (subscriberId: %s) to be update does not exists!", subscriber.getSubscriberId()));
            }
            if (!Objects.equals(old.getOrganizationId(), subscriber.getOrganizationId())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21006, String.format(
                        "The attribute organization id (old: %s, new: %s) could not be change!", old.getOrganizationId(), subscriber.getOrganizationId()));
            }
            // persist
            p.update(subscriber);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21006, e);
        }
    }

    @Override
    protected void deleteSubscriber(ISubscriber subscriber) {
        List<Object> entitiesToDelete = prepareEntitiesForDeleteSubscriber(subscriber);
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            // check the subscriber exists or not
            Subscriber old = p.createQuery(Subscriber.class).singleResult(subscriber.getSubscriberId()).get();
            if (old == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21007,
                        String.format("The subscriber (subscriberId: %s) to be delete does not exists!", subscriber.getSubscriberId()));
            }
            // remove
            p.remove(entitiesToDelete);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21007, e);
        }
    }

    private List<Object> prepareEntitiesForDeleteSubscriber(ISubscriber subscriber) {
        if (!(subscriber instanceof Subscriber)) {
            String msg = String.format("Only the instance of %s could be persisted!", Subscriber.class.getName());
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21008, msg);
        }
        List<Object> entitiesToDelete = new ArrayList<>();

        // prepare subscriber bean
        entitiesToDelete.add(subscriber);

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

    @Override
    protected void saveNewWebhook(IWebhook webhook) {
        if (!(webhook instanceof Webhook)) {
            String msg = String.format("Only the instance of %s could be persisted!", Webhook.class.getName());
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21009, msg);
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            // check the publisher id & publisher version & subscriber id exist or not
            if (p.createQuery(Webhook.class).singleResult(webhook.getWebhookId()).get() != null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21009,
                        String.format("The webhook (webhookId: %s) arealdy exists!", webhook.getWebhookId()));
            }
            if (p.createQuery(Subscriber.class).singleResult(webhook.getSubscriberId()).get() == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21009,
                        String.format("The subscriber (subscriberId: %s) does not exists!", webhook.getSubscriberId()));
            }
            if (p.createQuery(Publisher.class).singleResult(webhook.getPublisherId()).get() == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21009,
                        String.format("The publisher (publisherId: %s) does not exists!", webhook.getPublisherId()));
            }
            // save
            p.create(webhook);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21009, e);
        }
    }

    @Override
    protected void saveUpdatedWebhook(IWebhook webhook) {
        if (!(webhook instanceof Webhook)) {
            String msg = String.format("Only the instance of %s could be persisted!", Webhook.class.getName());
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21010, msg);
        }
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            // check the publisher id & publisher version & subscriber id can't be changed
            Webhook old = p.createQuery(Webhook.class).singleResult(webhook.getWebhookId()).get();
            if (old == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21010,
                        String.format("The webhook (webhookId: %s) to be update does not exists!", webhook.getWebhookId()));
            }
            if (!Objects.equals(webhook.getPublisherId(), old.getPublisherId())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21010,
                        String.format("The publisher id (old: %s, new: %s) can't be changed!", old.getPublisherId(), webhook.getPublisherId()));
            }
            if (!Objects.equals(webhook.getPublisherVersion(), old.getPublisherVersion())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21010,
                        String.format("The publisher version (old: %s, new: %s) can't be changed!", old.getPublisherVersion(), webhook.getPublisherVersion()));
            }
            if (!Objects.equals(webhook.getSubscriberId(), old.getSubscriberId())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21010,
                        String.format("The subscriber id (old: %s, new: %s) can't be changed!", old.getSubscriberId(), webhook.getSubscriberId()));
            }
            // persist
            p.update(webhook);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21010, e);
        }
    }

    @Override
    protected void saveActivatedWebhook(IWebhook webhook) {
        if (!(webhook instanceof Webhook)) {
            String msg = String.format("Only the instance of %s could be persisted!", Webhook.class.getName());
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21011, msg);
        }
        webhook.setWebhookStatus(WebhookStatus.PRODUCTION);
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            // check webhook is already activated or not
            Webhook old = p.createQuery(Webhook.class).singleResult(webhook.getWebhookId()).get();
            if (old == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21011,
                        String.format("The webhook (webhookId: %s) to be activate does not exists!", webhook.getWebhookId()));
            }
            if (WebhookStatus.PRODUCTION.equals(old.getWebhookStatus())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21011,
                        "The webhook already been activated, can't activate again!");
            }
            // save
            p.update(webhook);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21011, e);
        }
    }

    @Override
    protected void saveDeactivatedWebhook(IWebhook webhook) {
        if (!(webhook instanceof Webhook)) {
            String msg = String.format("Only the instance of %s could be persisted!", Webhook.class.getName());
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21012, msg);
        }
        webhook.setWebhookStatus(WebhookStatus.INACTIVE);
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            // check webhook is already deactivated or not
            Webhook old = p.createQuery(Webhook.class).singleResult(webhook.getWebhookId()).get();
            if (old == null) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21012,
                        String.format("The webhook (webhookId: %s) to be deactivate does not exists!", webhook.getWebhookId()));
            }
            if (WebhookStatus.INACTIVE.equals(old.getWebhookStatus())) {
                throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21012,
                        "The webhook already been deactivated, can't deactivate again!");
            }
            // save
            p.update(webhook);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21012, e);
        }
    }

    @Override
    protected void saveEventSubscribed(IWebhook webhook, List<IEvent> events) {
        IWebhook tmp = queryEntity(Webhook.class, webhook.getWebhookId());
        if (tmp == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21013,
                    String.format("The webhook (webhookId: %s) to subscribe events does not exists!", webhook.getWebhookId()));
        }
        Set<String> existingIds = null;
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            existingIds = existingUniqueValues(p, EventSubscribed.class, "webhookId", webhook.getWebhookId(), EventSubscribed.class.getMethod("getEventId"));
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21013, e);
        }
        List<EventSubscribed> subscribeEvents = new ArrayList<>();
        for (IEvent event : events) {
            if (!existingIds.contains(event.getEventId())) {
                EventSubscribed es = new EventSubscribed();
                es.setEventSubedId(EntityUtils.getInstance().createUniqueObjectId());
                es.setWebhookId(webhook.getWebhookId());
                es.setEventId(event.getEventId());
                subscribeEvents.add(es);
            }
        }
        // save
        try (IPersistence p = f.create()) {
            p.create(subscribeEvents);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21013, e);
        }
    }

    @Override
    protected void saveEventUnsubscribed(IWebhook webhook, List<IEvent> events) {
        IWebhook tmp = queryEntity(Webhook.class, webhook.getWebhookId());
        if (tmp == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21013,
                    String.format("The webhook (webhookId: %s) to unsubscribe events does not exists!", webhook.getWebhookId()));
        }
        List<EventSubscribed> existingEvents = queryEntities(EventSubscribed.class, "webhookId", webhook.getWebhookId());
        Map<String, EventSubscribed> existingIds = new HashMap<>();
        for (EventSubscribed event : existingEvents) {
            existingIds.put(event.getEventId(), event);
        }
        List<EventSubscribed> unsubscribeEvents = new ArrayList<>();
        for (IEvent event : events) {
            EventSubscribed es = existingIds.get(event.getEventId());
            if (es != null) {
                unsubscribeEvents.add(es);
            }
        }
        // save
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            p.remove(unsubscribeEvents);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21014, e);
        }
    }

    @Override
    protected void saveDataGroupSubscribed(IWebhook webhook, List<IDataGroup> dataGroups) {
        IWebhook tmp = queryEntity(Webhook.class, webhook.getWebhookId());
        if (tmp == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21013,
                    String.format("The webhook (webhookId: %s) to subscribe data groups does not exists!", webhook.getWebhookId()));
        }

        Set<String> existingIds = null;
        // save
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            existingIds = existingUniqueValues(p, DataGroupSubscribed.class, "webhookId", webhook.getWebhookId(),
                    DataGroupSubscribed.class.getMethod("getDataGroupId"));
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21015, e);
        }
        List<DataGroupSubscribed> subscribeDataGroups = new ArrayList<>();
        for (IDataGroup dataGroup : dataGroups) {
            if (!existingIds.contains(dataGroup.getDataGroupId())) {
                DataGroupSubscribed dgs = new DataGroupSubscribed();
                dgs.setDataGroupSubedId(EntityUtils.getInstance().createUniqueObjectId());
                dgs.setWebhookId(webhook.getWebhookId());
                dgs.setDataGroupId(dataGroup.getDataGroupId());
                dgs.setDataGroup(dataGroup.getDataGroup());
                subscribeDataGroups.add(dgs);
            }
        }
        // save
        try (IPersistence p = f.create()) {
            p.create(subscribeDataGroups);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21015, e);
        }
    }

    @Override
    protected void saveDataGroupUnsubscribed(IWebhook webhook, List<IDataGroup> dataGroups) {
        IWebhook tmp = queryEntity(Webhook.class, webhook.getWebhookId());
        if (tmp == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21013,
                    String.format("The webhook (webhookId: %s) to unsubscribe data groups does not exists!", webhook.getWebhookId()));
        }

        List<DataGroupSubscribed> existingDataGroups = queryEntities(DataGroupSubscribed.class, "webhookId", webhook.getWebhookId());
        Map<String, DataGroupSubscribed> existingIds = new HashMap<>();
        for (DataGroupSubscribed existingDataGroup : existingDataGroups) {
            existingIds.put(existingDataGroup.getDataGroupId(), existingDataGroup);
        }
        List<DataGroupSubscribed> unsubscribeDataGroups = new ArrayList<>();
        for (IDataGroup dataGroup : dataGroups) {
            DataGroupSubscribed dgs = existingIds.get(dataGroup.getDataGroupId());
            if (dgs != null) {
                unsubscribeDataGroups.add(dgs);
            }
        }
        // save
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            p.remove(unsubscribeDataGroups);
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21016, e);
        }
    }

    @Override
    public IOrganization findOrganization(String organizationId) {
        if (organizationId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017,
                    "Organization id should not be null when query organization!");
        }
        return queryEntity(Organization.class, organizationId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IOrganization> findAllOrganizations(int page, int pageSize) {
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            @SuppressWarnings("rawtypes")
            IEntityQuery q = p.createQuery(Organization.class).firstResult(page * pageSize).maxResults(pageSize);
            return (List<IOrganization>) q.resultList().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
    }

    @Override
    public IPublisher findPublisher(String publisherId) {
        if (publisherId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017,
                    "Publisher id should not be null when query publisher!");
        }
        return queryEntity(Publisher.class, publisherId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IPublisher> findAllPublishers(int page, int pageSize) {
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            @SuppressWarnings("rawtypes")
            IEntityQuery q = p.createQuery(Publisher.class).firstResult(page * pageSize).maxResults(pageSize);
            return (List<IPublisher>) q.resultList().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IEvent> findEvents(String publisherId, int page, int pageSize) {
        if (publisherId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017,
                    "Publisher id should not be null when query events!");
        }
        IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = factory.create()) {
            @SuppressWarnings("rawtypes")
            IEntityQuery q = p.createQuery(Event.class).firstResult(page * pageSize).maxResults(pageSize).filter("publisherId", FilterOperator.equals,
                    publisherId);
            return (List<IEvent>) q.resultList().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
    }

    @Override
    public IEvent findEvent(String eventId) {
        if (eventId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, "Event id should not be null when query event!");
        }
        return queryEntity(Event.class, eventId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IDataGroup> findDataGroups(String publisherId, int page, int pageSize) {
        if (publisherId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017,
                    "Publisher id should not be null when query data groups!");
        }
        IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = factory.create()) {
            @SuppressWarnings("rawtypes")
            IEntityQuery q = p.createQuery(DataGroup.class).firstResult(page * pageSize).maxResults(pageSize).filter("publisherId", FilterOperator.equals,
                    publisherId);
            return (List<IDataGroup>) q.resultList().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
    }

    @Override
    public IDataGroup findDataGroup(String dataGroupId) {
        if (dataGroupId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017,
                    "Data group id should not be null when query data group!");
        }
        return queryEntity(DataGroup.class, dataGroupId);
    }

    @Override
    public IDataGroup findDataGroup(String publisherId, String dataGroup) {
        if (publisherId == null || dataGroup == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017,
                    "The publisher id and data group should not be null when query data group!");
        }
        IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = factory.create()) {
            IEntityQuery<DataGroup> q = p.createQuery(DataGroup.class).filter("publisherId", FilterOperator.equals, publisherId).filter("dataGroup",
                    FilterOperator.equals, dataGroup.strip());
            return q.singleResult().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
    }

    @Override
    public ISubscriber findSubscriber(String subscriberId) {
        if (subscriberId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017,
                    "Subscriber id should not be null when query subscriber!");
        }
        return queryEntity(Subscriber.class, subscriberId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ISubscriber> findAllSubscribers(int page, int pageSize) {
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            @SuppressWarnings("rawtypes")
            IEntityQuery q = p.createQuery(Subscriber.class).firstResult(page * pageSize).maxResults(pageSize);
            return (List<ISubscriber>) q.resultList().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IWebhook> findWebhooks(String subscriberId, int page, int pageSize) {
        if (subscriberId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017,
                    "Subscriber id should not be null when query webhooks!");
        }
        IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = factory.create()) {
            @SuppressWarnings("rawtypes")
            IEntityQuery q = p.createQuery(Webhook.class).firstResult(page * pageSize).maxResults(pageSize).filter("subscriberId", FilterOperator.equals,
                    subscriberId);
            return (List<IWebhook>) q.resultList().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
    }

    @Override
    public IWebhook findWebhook(String webhookId) {
        if (webhookId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, "Webhook id should not be null when query webhook!");
        }
        return queryEntity(Webhook.class, webhookId);
    }

    @Override
    public List<IEvent> findSubscribedEvents(String webhookId, int page, int pageSize) {
        if (webhookId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017,
                    "Webhook id should not be null when query subscribed events!");
        }
        List<EventSubscribed> eventsSubscribed = null;
        IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = factory.create()) {
            IEntityQuery<EventSubscribed> q = p.createQuery(EventSubscribed.class).firstResult(page * pageSize).maxResults(pageSize).filter("webhookId",
                    FilterOperator.equals, webhookId);
            eventsSubscribed = q.resultList().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
        List<IEvent> list = new ArrayList<>();
        try (IPersistence p = factory.create()) {
            for (EventSubscribed es : eventsSubscribed) {
                Event e = p.createQuery(Event.class).singleResult(es.getEventId()).get();
                if (e != null) {
                    list.add(e);
                }
            }
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
        return list;
    }

    @Override
    public List<IDataGroup> findSubscribedDataGroups(String webhookId, int page, int pageSize) {
        if (webhookId == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017,
                    "Webhook id should not be null when query subscribed data groups!");
        }
        List<DataGroupSubscribed> dataGroupsSubscribed = null;
        IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = factory.create()) {
            IEntityQuery<DataGroupSubscribed> q = p.createQuery(DataGroupSubscribed.class).firstResult(page * pageSize).maxResults(pageSize).filter("webhookId",
                    FilterOperator.equals, webhookId);
            dataGroupsSubscribed = q.resultList().get();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
        List<IDataGroup> list = new ArrayList<>();
        try (IPersistence p = factory.create()) {
            for (DataGroupSubscribed dgs : dataGroupsSubscribed) {
                DataGroup dg = p.createQuery(DataGroup.class).singleResult(dgs.getDataGroupId()).get();
                if (dg != null) {
                    list.add(dg);
                }
            }
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
        return list;
    }

    @Override
    public IDataGroup findSubscribedDataGroup(String webhookId, String dataGroup) {
        if (webhookId == null || dataGroup == null) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017,
                    "The publisher id and data group should not be null when query data group subscribed!");
        }
        String dataGroupId = null;
        IPersistenceFactory factory = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = factory.create()) {
            IEntityQuery<DataGroupSubscribed> q = p.createQuery(DataGroupSubscribed.class).filter("webhookId", FilterOperator.equals, webhookId)
                    .filter("dataGroup", FilterOperator.equals, dataGroup.strip());
            DataGroupSubscribed dgs = q.singleResult().get();
            dataGroupId = dgs == null ? null : dgs.getDataGroupId();
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
        return dataGroupId == null ? null : queryEntity(DataGroup.class, dataGroupId);
    }

    private <T> Set<String> existingUniqueValues(IPersistence p, Class<T> clazz, String filterAttributeName, String filterAttributeValue, Method method)
            throws InterruptedException, ExecutionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Set<String> existingValues = new HashSet<>();
        int page = 0;
        List<T> existingEntities = p.createQuery(clazz).filter(filterAttributeName, FilterOperator.equals, filterAttributeValue).firstResult(page * PAGE_SIZE)
                .maxResults(PAGE_SIZE).resultList().get();
        while (existingEntities.size() > 0) {
            for (T existingEntity : existingEntities) {
                Object obj = method.invoke(existingEntity);
                if (obj != null && obj instanceof String) {
                    existingValues.add((String) obj);
                }
            }
            if (existingEntities.size() < PAGE_SIZE) {
                break;
            }
            page++;
            existingEntities = p.createQuery(clazz).filter(filterAttributeName, FilterOperator.equals, filterAttributeValue).firstResult(page * PAGE_SIZE)
                    .maxResults(PAGE_SIZE).resultList().get();
        }
        return existingValues;
    }

    private <T> T queryEntity(Class<T> entityClass, String primaryKey) {
        IPersistenceFactory f = IPersistenceFactory.getFactoryInstance();
        try (IPersistence p = f.create()) {
            T bean = p.createQuery(entityClass).singleResult(primaryKey).get();
            return bean;
        } catch (Exception e) {
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
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
            throw new EnginePersistenceException(EnginePersistenceException.BUSINESS_EXCEPTION_CODE_21017, e);
        }
        return list;
    }

}
