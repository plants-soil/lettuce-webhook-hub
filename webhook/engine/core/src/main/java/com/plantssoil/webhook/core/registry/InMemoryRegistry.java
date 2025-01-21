package com.plantssoil.webhook.core.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.plantssoil.webhook.core.ClonableBean;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IOrganization;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;
import com.plantssoil.webhook.core.exception.EngineException;
import com.plantssoil.webhook.core.impl.AbstractRegistry;

/**
 * The in-memory registry implementation<br/>
 * <p>
 * In-memory registry will keep publishers / subscribers in memory, and all data
 * will be lost when JVM shutdown<br/>
 * It's only for demonstration purpose, SHOULD AVOID be used in production
 * environment<br/>
 * </p>
 * 
 * @author danialdy
 * @Date 3 Dec 2024 4:49:06 pm
 */
public class InMemoryRegistry extends AbstractRegistry {
    /**
     * key - The IOganization.getOrganizationId(), value - IOrganization
     */
    private Map<String, InMemoryOrganization> organizations = new ConcurrentHashMap<>();
    /**
     * key - The IOganization.getOrganizationId(), value - publisher version set
     */
    private Map<String, Set<String>> publisherVersions = new ConcurrentHashMap<>();
    /**
     * key - The IPublisher.getPublisherId(), value - IPublisher
     */
    private Map<String, InMemoryPublisher> publishers = new ConcurrentHashMap<>();
    /**
     * key - The ISubscriber.getSubscriberId(), value - ISubscriber
     */
    private Map<String, InMemorySubscriber> subscribers = new ConcurrentHashMap<>();

    /**
     * key - The organizationId, value - The subscriber id
     */
    private Map<String, String> organizationsBySubscriber = new ConcurrentHashMap<>();

    /**
     * key - The IEvent.getEventId(), value - IEvent
     */
    private Map<String, InMemoryEvent> events = new ConcurrentHashMap<>();

    /**
     * key - The IDataGroup.getDataGroupId(), value - IDataGroup
     */
    private Map<String, InMemoryDataGroup> dataGroups = new ConcurrentHashMap<>();

    /**
     * key - The IWebhook.getWebhookId(), value - IWebhook
     */
    private Map<String, InMemoryWebhook> webhooks = new ConcurrentHashMap<>();

    /**
     * key - The IPublisher.getPublisherId(), value - IEvent list
     */
    private Map<String, List<InMemoryEvent>> publisherEvents = new ConcurrentHashMap<>();

    /**
     * key - The IPublisher.getPublisherId(), value - IDataGroup list
     */
    private Map<String, List<InMemoryDataGroup>> publisherDataGroups = new ConcurrentHashMap<>();

    /**
     * key - The ISubscriber.getSubscriberId(), value - IWebhook list
     */
    private Map<String, List<InMemoryWebhook>> subscriberWebhooks = new ConcurrentHashMap<>();

    /**
     * key - The IWebhook.getWebhookId(), value - IEvent list
     */
    private Map<String, List<InMemoryEvent>> eventsSubscribed = new ConcurrentHashMap<>();

    /**
     * key - The IWebhook.getWebhookId(), value - IDataGroup list
     */
    private Map<String, List<InMemoryDataGroup>> dataGroupsSubscribed = new ConcurrentHashMap<>();

    private <T> List<T> getList(Map<String, List<T>> map, String key) {
        List<T> list = map.get(key);
        if (list == null) {
            synchronized (key.intern()) {
                list = map.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(key, list);
                }
            }
        }
        return list;
    }

    private Set<String> getPublisherVersion(String organizationId) {
        Set<String> version = this.publisherVersions.get(organizationId);
        if (version == null) {
            synchronized (organizationId.intern()) {
                version = this.publisherVersions.get(organizationId);
                if (version == null) {
                    version = new HashSet<>();
                    this.publisherVersions.put(organizationId, version);
                }
            }
        }
        return version;
    }

    @Override
    public void addOrganization(IOrganization organization) {
        super.addOrganization(organization);
        if (this.organizations.containsKey(organization.getOrganizationId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The organization (organizationId: %s) arealdy exists!", organization.getOrganizationId()));
        }
        this.organizations.put(organization.getOrganizationId(), (InMemoryOrganization) organization);
    }

    @Override
    public void updateOrganization(IOrganization organization) {
        super.updateOrganization(organization);
        InMemoryOrganization old = this.organizations.get(organization.getOrganizationId());
        if (old == null) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The organization (organizationId: %s) to be update does not exists!", organization.getOrganizationId()));
        }
        this.organizations.put(organization.getOrganizationId(), (InMemoryOrganization) organization);
    }

    @Override
    protected void saveNewPublisher(IPublisher publisher) {
        if (this.publishers.containsKey(publisher.getPublisherId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The publisher (publisherId: %s) arealdy exists!", publisher.getPublisherId()));
        }
        if (!this.organizations.containsKey(publisher.getOrganizationId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The organization (organizationId: %s) does not exists!", publisher.getOrganizationId()));
        }
        Set<String> vs = getPublisherVersion(publisher.getOrganizationId());
        if (vs.contains(publisher.getVersion())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The publisher with version (%s) already exists!", publisher.getVersion()));
        } else {
            vs.add(publisher.getVersion());
        }

        // add publisher into map
        this.publishers.put(publisher.getPublisherId(), (InMemoryPublisher) publisher);
    }

    @Override
    protected void saveUpdatedPublisher(IPublisher publisher) {
        InMemoryPublisher old = this.publishers.get(publisher.getPublisherId());
        if (old == null) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The publisher (publisherId: %s) to be update does not exists!", publisher.getPublisherId()));
        }
        if ((old.isSupportDataGroup() && !publisher.isSupportDataGroup()) || (!old.isSupportDataGroup() && publisher.isSupportDataGroup())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008, String.format(
                    "The property (support data group, old: %s, updated: %s) could not be changed!", old.isSupportDataGroup(), publisher.isSupportDataGroup()));
        }
        if (!Objects.equals(old.getVersion(), publisher.getVersion())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The property (version, old: %s, updated: %s) could not be changed!", old.getVersion(), publisher.getVersion()));
        }
        if (!Objects.equals(old.getOrganizationId(), publisher.getOrganizationId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008, String.format(
                    "The property (organizationId, old: %s, updated: %s) could not be changed!", old.getOrganizationId(), publisher.getOrganizationId()));
        }
        this.publishers.put(publisher.getPublisherId(), (InMemoryPublisher) publisher);
    }

    @Override
    protected void saveNewEvent(String publisherId, List<IEvent> es) {
        if (!this.publishers.containsKey(publisherId)) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The publisher (publisherId: %s) does not exist!", publisherId));
        }
        List<InMemoryEvent> existingEvents = getList(this.publisherEvents, publisherId);
        Set<String> existingEventTypes = new HashSet<>();
        for (InMemoryEvent existingEvent : existingEvents) {
            existingEventTypes.add(existingEvent.getEventType());
        }
        Set<String> eventTypes = new HashSet<>();
        for (IEvent e : es) {
            if (this.events.containsKey(e.getEventId())) {
                throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                        String.format("The event (eventId: %s) already exists!", e.getEventId()));
            }
            if (eventTypes.contains(e.getEventType()) || existingEventTypes.contains(e.getEventType())) {
                throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                        String.format("The event type shoule be unique, however (%s) is duplicated!", e.getEventType()));
            }
            eventTypes.add(e.getEventType());
        }

        for (IEvent e : es) {
            InMemoryEvent ie = (InMemoryEvent) e;
            this.events.put(ie.getEventId(), ie);
            existingEvents.add(ie);
        }
    }

    @Override
    protected void saveNewDataGroup(String publisherId, List<IDataGroup> dgs) {
        if (!this.publishers.containsKey(publisherId)) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The publisher (publisherId: %s) does not exist!", publisherId));
        }
        List<InMemoryDataGroup> existingDataGroups = getList(this.publisherDataGroups, publisherId);
        Set<String> existingDataGroupNames = new HashSet<>();
        for (InMemoryDataGroup existingDataGroup : existingDataGroups) {
            existingDataGroupNames.add(existingDataGroup.getDataGroup());
        }
        Set<String> dataGroupNames = new HashSet<>();
        for (IDataGroup dg : dgs) {
            if (this.dataGroups.containsKey(dg.getDataGroupId())) {
                throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                        String.format("The data group (dataGroupId: %s) already exists!", dg.getDataGroupId()));
            }
            if (dataGroupNames.contains(dg.getDataGroup()) || existingDataGroupNames.contains(dg.getDataGroup())) {
                throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                        String.format("The data group shoule be unique, however (%s) is duplicated!", dg.getDataGroup()));
            }
            dataGroupNames.add(dg.getDataGroup());
        }
        for (IDataGroup dg : dgs) {
            InMemoryDataGroup idg = (InMemoryDataGroup) dg;
            this.dataGroups.put(idg.getDataGroupId(), idg);
            existingDataGroups.add(idg);
        }
    }

    @Override
    protected void saveNewSubscriber(ISubscriber subscriber) {
        if (this.subscribers.containsKey(subscriber.getSubscriberId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The subscriber (subscriberId: %s) arealdy exists!", subscriber.getSubscriberId()));
        }
        if (!this.organizations.containsKey(subscriber.getOrganizationId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The organization (organizationId: %s) does not exists!", subscriber.getOrganizationId()));
        }
        String organizationBoundSubscriber = this.organizationsBySubscriber.get(subscriber.getOrganizationId());
        if (organizationBoundSubscriber != null) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The organization (organizationId: %s) already bound to subscriber (subscriberId: %s)!", subscriber.getOrganizationId(),
                            organizationBoundSubscriber));
        }
        // add subscriber into map
        this.subscribers.put(subscriber.getSubscriberId(), (InMemorySubscriber) subscriber);
        this.organizationsBySubscriber.put(subscriber.getOrganizationId(), subscriber.getSubscriberId());
    }

    @Override
    protected void saveUpdatedSubscriber(ISubscriber subscriber) {
        InMemorySubscriber old = this.subscribers.get(subscriber.getSubscriberId());
        if (old == null) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The subscriber (subscriberId: %s) to be update does not exists!", subscriber.getSubscriberId()));
        }
        if (!Objects.equals(old.getOrganizationId(), subscriber.getOrganizationId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008, String.format(
                    "The property (organizationId, old: %s, updated: %s) could not be changed!", old.getOrganizationId(), subscriber.getOrganizationId()));
        }
        this.subscribers.put(subscriber.getSubscriberId(), (InMemorySubscriber) subscriber);
    }

    @Override
    protected void deleteSubscriber(ISubscriber subscriber) {
        String subscriberId = subscriber.getSubscriberId();
        if (!this.subscribers.containsKey(subscriberId)) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The subscriber (subscriberId: %s) to be delete does not exists!", subscriberId));
        }
        this.subscribers.remove(subscriberId);
        this.organizationsBySubscriber.remove(subscriber.getOrganizationId());
        List<InMemoryWebhook> whs = getList(this.subscriberWebhooks, subscriberId);
        for (InMemoryWebhook wh : whs) {
            this.webhooks.remove(wh.getWebhookId());
            this.eventsSubscribed.remove(wh.getWebhookId());
            this.dataGroupsSubscribed.remove(wh.getWebhookId());
        }
        whs.clear();
        this.subscriberWebhooks.remove(subscriberId);
    }

    @Override
    protected void saveNewWebhook(IWebhook webhook) {
        String webhookId = webhook.getWebhookId();
        if (this.webhooks.containsKey(webhookId)) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008, String.format("The webhook (webhookId: %s) arealdy exists!", webhookId));
        }
        if (!this.subscribers.containsKey(webhook.getSubscriberId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The subscriber (subscriberId: %s) does not exists!", webhook.getSubscriberId()));
        }
        if (!this.publishers.containsKey(webhook.getPublisherId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The publisher (publisherId: %s) does not exists!", webhook.getPublisherId()));
        }

        InMemoryWebhook wh = (InMemoryWebhook) webhook;
        this.webhooks.put(webhookId, wh);
        getList(this.subscriberWebhooks, wh.getSubscriberId()).add(wh);
    }

    @Override
    protected void saveUpdatedWebhook(IWebhook webhook) {
        String webhookId = webhook.getWebhookId();
        InMemoryWebhook old = this.webhooks.get(webhookId);
        if (old == null) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The webhook (webhookId: %s) to be update does not exists!", webhookId));
        }
        if (!Objects.equals(webhook.getPublisherId(), old.getPublisherId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The publisher id (old: %s, new: %s) can't be changed!", old.getPublisherId(), webhook.getPublisherId()));
        }
        if (!Objects.equals(webhook.getPublisherVersion(), old.getPublisherVersion())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The publisher version (old: %s, new: %s) can't be changed!", old.getPublisherVersion(), webhook.getPublisherVersion()));
        }
        if (!Objects.equals(webhook.getSubscriberId(), old.getSubscriberId())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The subscriber id (old: %s, new: %s) can't be changed!", old.getSubscriberId(), webhook.getSubscriberId()));
        }

        InMemoryWebhook wh = (InMemoryWebhook) webhook;
        if (!Objects.equals(wh.getAccessToken(), old.getAccessToken())) {
            old.setAccessToken(wh.getAccessToken());
        }
        if (!Objects.equals(wh.getRefreshToken(), old.getRefreshToken())) {
            old.setRefreshToken(wh.getRefreshToken());
        }
        if (!Objects.equals(wh.getTrustedIps(), old.getTrustedIps())) {
            old.setTrustedIps(wh.getTrustedIps());
        }
        if (!Objects.equals(wh.getWebhookSecret(), old.getWebhookSecret())) {
            old.setWebhookSecret(wh.getWebhookSecret());
        }
        if (!Objects.equals(wh.getWebhookUrl(), old.getWebhookUrl())) {
            old.setWebhookUrl(wh.getWebhookUrl());
        }
        if (!Objects.equals(wh.getCustomizedHeaders(), old.getCustomizedHeaders())) {
            old.setCustomizedHeaders(wh.getCustomizedHeaders());
        }
        if (!Objects.equals(wh.getSecurityStrategy(), old.getSecurityStrategy())) {
            old.setSecurityStrategy(wh.getSecurityStrategy());
        }
    }

    @Override
    protected void saveActivatedWebhook(IWebhook webhook) {
        String webhookId = webhook.getWebhookId();
        InMemoryWebhook old = this.webhooks.get(webhookId);
        if (old == null) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The webhook (webhookId: %s) to be activate does not exists!", webhookId));
        }
        if (WebhookStatus.PRODUCTION.equals(old.getWebhookStatus())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008, "The webhook already been activated, can't activate again!");
        }
        old.setWebhookStatus(WebhookStatus.PRODUCTION);
    }

    @Override
    protected void saveDeactivatedWebhook(IWebhook webhook) {
        String webhookId = webhook.getWebhookId();
        InMemoryWebhook old = this.webhooks.get(webhookId);
        if (old == null) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The webhook (webhookId: %s) to be deactivate does not exists!", webhookId));
        }
        if (WebhookStatus.INACTIVE.equals(old.getWebhookStatus())) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008, "The webhook already been deactivated, can't deactivate again!");
        }
        old.setWebhookStatus(WebhookStatus.INACTIVE);
    }

    @Override
    protected void saveEventSubscribed(IWebhook webhook, List<IEvent> es) {
        String webhookId = webhook.getWebhookId();
        if (!this.webhooks.containsKey(webhookId)) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The webhook (webhookId: %s) to subscribe events does not exists!", webhookId));
        }
        List<InMemoryEvent> existingEvents = getList(this.eventsSubscribed, webhookId);
        Set<String> existingIds = new HashSet<>();
        for (InMemoryEvent e : existingEvents) {
            existingIds.add(e.getEventId());
        }
        for (IEvent e : es) {
            if (!existingIds.contains(e.getEventId())) {
                existingEvents.add((InMemoryEvent) e);
            }
        }
    }

    @Override
    protected void saveEventUnsubscribed(IWebhook webhook, List<IEvent> es) {
        String webhookId = webhook.getWebhookId();
        if (!this.webhooks.containsKey(webhookId)) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The webhook (webhookId: %s) to unsubscribe events does not exists!", webhookId));
        }
        List<InMemoryEvent> existingEvents = getList(this.eventsSubscribed, webhookId);
        Map<String, Integer> existingIds = new HashMap<>();
        for (int i = 0; i < existingEvents.size(); i++) {
            existingIds.put(existingEvents.get(i).getEventId(), i);
        }
        List<Integer> indexesRemove = new ArrayList<>();
        for (IEvent e : es) {
            Integer index = existingIds.get(e.getEventId());
            if (index != null) {
                indexesRemove.add(index);
            }
        }
        for (Integer index : indexesRemove) {
            existingEvents.remove(index.intValue());
        }
    }

    @Override
    protected void saveDataGroupSubscribed(IWebhook webhook, List<IDataGroup> dgs) {
        String webhookId = webhook.getWebhookId();
        if (!this.webhooks.containsKey(webhookId)) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The webhook (webhookId: %s) to subscribe data groups does not exists!", webhookId));
        }
        List<InMemoryDataGroup> existingDataGroups = getList(this.dataGroupsSubscribed, webhookId);
        Set<String> existingIds = new HashSet<>();
        for (InMemoryDataGroup e : existingDataGroups) {
            existingIds.add(e.getDataGroupId());
        }
        for (IDataGroup dg : dgs) {
            if (!existingIds.contains(dg.getDataGroupId())) {
                existingDataGroups.add((InMemoryDataGroup) dg);
            }
        }
    }

    @Override
    protected void saveDataGroupUnsubscribed(IWebhook webhook, List<IDataGroup> dgs) {
        String webhookId = webhook.getWebhookId();
        if (!this.webhooks.containsKey(webhookId)) {
            throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20008,
                    String.format("The webhook (webhookId: %s) to unsubscribe data groups does not exists!", webhookId));
        }
        List<InMemoryDataGroup> existingDataGroups = getList(this.dataGroupsSubscribed, webhookId);
        Map<String, Integer> existingIds = new HashMap<>();
        for (int i = 0; i < existingDataGroups.size(); i++) {
            existingIds.put(existingDataGroups.get(i).getDataGroupId(), i);
        }
        List<Integer> indexesRemove = new ArrayList<>();
        for (IDataGroup dg : dgs) {
            Integer index = existingIds.get(dg.getDataGroupId());
            if (index != null) {
                indexesRemove.add(index);
            }
        }
        for (Integer index : indexesRemove) {
            existingDataGroups.remove(index.intValue());
        }
    }

    @Override
    public IOrganization findOrganization(String organizationId) {
        InMemoryOrganization o = this.organizations.get(organizationId);
        return (IOrganization) cloneObject(o);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IOrganization> findAllOrganizations(int page, int pageSize) {
        return getPagedList(this.organizations.values(), page, pageSize);
    }

    @Override
    public IPublisher findPublisher(String publisherId) {
        InMemoryPublisher o = this.publishers.get(publisherId);
        return (IPublisher) cloneObject(o);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IPublisher> findAllPublishers(int page, int pageSize) {
        return getPagedList(this.publishers.values(), page, pageSize);
    }

    @Override
    public IEvent findEvent(String eventId) {
        InMemoryEvent o = this.events.get(eventId);
        return (IEvent) cloneObject(o);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IEvent> findEvents(String publisherId, int page, int pageSize) {
        List<InMemoryEvent> pel = getList(this.publisherEvents, publisherId);
        return getPagedList(pel, page, pageSize);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IDataGroup> findDataGroups(String publisherId, int page, int pageSize) {
        List<InMemoryDataGroup> pdgl = getList(this.publisherDataGroups, publisherId);
        return getPagedList(pdgl, page, pageSize);
    }

    @Override
    public IDataGroup findDataGroup(String dataGroupId) {
        InMemoryDataGroup o = this.dataGroups.get(dataGroupId);
        return (IDataGroup) cloneObject(o);
    }

    @Override
    public IDataGroup findDataGroup(String publisherId, String dataGroup) {
        List<InMemoryDataGroup> pdgl = getList(this.publisherDataGroups, publisherId);
        for (InMemoryDataGroup pdg : pdgl) {
            if (pdg.getDataGroup().equals(dataGroup)) {
                return (IDataGroup) cloneObject(pdg);
            }
        }
        return null;
    }

    @Override
    public ISubscriber findSubscriber(String subscriberId) {
        InMemorySubscriber o = this.subscribers.get(subscriberId);
        return (ISubscriber) cloneObject(o);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ISubscriber> findAllSubscribers(int page, int pageSize) {
        return getPagedList(this.subscribers.values(), page, pageSize);
    }

    @Override
    public IWebhook findWebhook(String webhookId) {
        InMemoryWebhook o = this.webhooks.get(webhookId);
        return (IWebhook) cloneObject(o);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IWebhook> findWebhooks(String subscriberId, int page, int pageSize) {
        List<InMemoryWebhook> swhs = getList(this.subscriberWebhooks, subscriberId);
        return getPagedList(swhs, page, pageSize);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IEvent> findSubscribedEvents(String webhookId, int page, int pageSize) {
        List<InMemoryEvent> wes = getList(this.eventsSubscribed, webhookId);
        return getPagedList(wes, page, pageSize);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IDataGroup> findSubscribedDataGroups(String webhookId, int page, int pageSize) {
        List<InMemoryDataGroup> wdgs = getList(this.dataGroupsSubscribed, webhookId);
        return getPagedList(wdgs, page, pageSize);
    }

    @Override
    public IDataGroup findSubscribedDataGroup(String webhookId, String dataGroup) {
        List<InMemoryDataGroup> wdgs = getList(this.dataGroupsSubscribed, webhookId);
        for (InMemoryDataGroup wdg : wdgs) {
            if (wdg.getDataGroup().equals(dataGroup)) {
                return (IDataGroup) cloneObject(wdg);
            }
        }
        return null;
    }

    private ClonableBean cloneObject(ClonableBean o) {
        try {
            return o == null ? null : (ClonableBean) o.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getPagedList(List sourceList, int page, int pageSize) {
        List list = new ArrayList();
        int startIndex = page * pageSize;
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (startIndex >= sourceList.size()) {
            return list;
        }

        int endIndex = startIndex + pageSize;
        if (endIndex < 0) {
            return list;
        }
        if (endIndex > sourceList.size()) {
            endIndex = sourceList.size();
        }

        for (int i = startIndex; i < endIndex; i++) {
            ClonableBean o = (ClonableBean) sourceList.get(i);
            list.add(cloneObject(o));
        }
        return list;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List getPagedList(Collection collection, int page, int pageSize) {
        List list = new ArrayList();
        int startIndex = page * pageSize;
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (startIndex >= collection.size()) {
            return list;
        }

        int endIndex = startIndex + pageSize;
        if (endIndex < 0) {
            return list;
        }
        if (endIndex > collection.size()) {
            endIndex = collection.size();
        }

        int i = 0;
        for (Object obj : collection) {
            if (i >= startIndex && i < endIndex) {
                ClonableBean o = (ClonableBean) obj;
                list.add(cloneObject(o));
            }
            i++;
        }
        return list;
    }
}
