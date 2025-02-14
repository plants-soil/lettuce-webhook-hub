package com.plantssoil.webhook.resteasy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.webhook.api.NotFoundException;
import com.plantssoil.webhook.api.PublisherApiService;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IEvent.EventStatus;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.registry.InMemoryDataGroup;
import com.plantssoil.webhook.core.registry.InMemoryEvent;
import com.plantssoil.webhook.core.registry.InMemoryPublisher;

@ApplicationScoped
@Default
public class PublisherApiServiceImpl implements PublisherApiService {
    public PublisherApiServiceImpl() {
    }

    @Override
    public Response addDataGroup(InMemoryDataGroup body, String publisherId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            if (body.getDataGroupId() == null) {
                body.setDataGroupId(EntityUtils.getInstance().createUniqueObjectId());
            }
            IDataGroup dg = (IDataGroup) BeanBridge.getInstance().bridge(body);
            r.addDataGroup(publisherId, dg);
            return ResponseBuilder.ok().data(body).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response addDataGroup(String dataGroupId, String publisherId2, String dataGroup, String publisherId, SecurityContext securityContext)
            throws NotFoundException {
        InMemoryDataGroup dg = new InMemoryDataGroup();
        dg.setDataGroup(dataGroup);
        dg.setDataGroupId(dataGroupId);
        return addDataGroup(dg, publisherId, securityContext);
    }

    @Override
    public Response addDataGroups(List<InMemoryDataGroup> body, String publisherId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            List<IDataGroup> list = new ArrayList<>();
            for (InMemoryDataGroup dg : body) {
                if (dg.getDataGroupId() == null) {
                    dg.setDataGroupId(EntityUtils.getInstance().createUniqueObjectId());
                }
                list.add((IDataGroup) BeanBridge.getInstance().bridge(dg));
            }
            r.addDataGroup(publisherId, list);
            return ResponseBuilder.ok().data(body).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response addEvent(InMemoryEvent body, String publisherId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            if (body.getEventId() == null) {
                body.setEventId(EntityUtils.getInstance().createUniqueObjectId());
            }
            body.setEventStatus(EventStatus.SUBMITTED);
            IEvent event = (IEvent) BeanBridge.getInstance().bridge(body);
            r.addEvent(publisherId, event);
            return ResponseBuilder.ok().data(body).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response addEvent(String eventId, String publisherId2, String eventType, String eventTag, String contentType, String charset, String eventStatus,
            String publisherId, SecurityContext securityContext) throws NotFoundException {
        InMemoryEvent event = new InMemoryEvent();
        event.setEventId(eventId);
        event.setEventType(eventType);
        event.setEventTag(eventTag);
        event.setContentType(contentType);
        event.setCharset(charset);
        return addEvent(event, publisherId, securityContext);
    }

    @Override
    public Response addEvents(List<InMemoryEvent> body, String publisherId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            List<IEvent> list = new ArrayList<>();
            for (InMemoryEvent event : body) {
                if (event.getEventId() == null) {
                    event.setEventId(EntityUtils.getInstance().createUniqueObjectId());
                }
                event.setEventStatus(EventStatus.SUBMITTED);
                list.add((IEvent) BeanBridge.getInstance().bridge(event));
            }
            r.addEvent(publisherId, list);
            return ResponseBuilder.ok().data(body).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response addPublisher(InMemoryPublisher body, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            if (body.getPublisherId() == null) {
                body.setPublisherId(EntityUtils.getInstance().createUniqueObjectId());
            }
            IPublisher p = (IPublisher) BeanBridge.getInstance().bridge(body);
            r.addPublisher(p);
            return ResponseBuilder.ok().data(body).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response addPublisher(String publisherId, String organizationId, Boolean supportDataGroup, String version, String createdBy, Date creationTime,
            SecurityContext securityContext) throws NotFoundException {
        InMemoryPublisher publisher = new InMemoryPublisher();
        publisher.setPublisherId(publisherId);
        publisher.setOrganizationId(organizationId);
        publisher.setSupportDataGroup(supportDataGroup);
        publisher.setVersion(version);
        publisher.setCreatedBy(createdBy);
        publisher.setCreationTime(creationTime);
        return addPublisher(publisher, securityContext);
    }

    @Override
    public Response findAllPublishers(Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            List<IPublisher> publishers = r.findAllPublishers(page, pageSize);
            return ResponseBuilder.ok().data(publishers).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findDataGroupById(String dataGroupId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IDataGroup dg = r.findDataGroup(dataGroupId);
            return ResponseBuilder.ok().data(dg).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findDataGroupByName(String publisherId, String dataGroup, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IDataGroup dg = r.findDataGroup(publisherId, dataGroup);
            return ResponseBuilder.ok().data(dg).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findDataGroups(String publisherId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            List<IDataGroup> dgs = r.findDataGroups(publisherId, page, pageSize);
            return ResponseBuilder.ok().data(dgs).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findEventById(String eventId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IEvent event = r.findEvent(eventId);
            return ResponseBuilder.ok().data(event).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findEvents(String publisherId, Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            List<IEvent> events = r.findEvents(publisherId, page, pageSize);
            return ResponseBuilder.ok().data(events).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findPublisherById(String publisherId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IPublisher publisher = r.findPublisher(publisherId);
            return ResponseBuilder.ok().data(publisher).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response updatePublisherById(InMemoryPublisher body, String publisherId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IPublisher p = r.findPublisher(publisherId);
            InMemoryPublisher old = BeanBridge.getInstance().bridge(p, InMemoryPublisher.class);
            updatePublisherValue(old, body);
            p = (IPublisher) BeanBridge.getInstance().bridge(old);
            r.updatePublisher(p);
            return ResponseBuilder.ok().data(old).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    private void updatePublisherValue(InMemoryPublisher old, InMemoryPublisher updated) {
        if (updated.getPublisherId() != null && !Objects.equals(updated.getPublisherId(), old.getPublisherId())) {
            old.setPublisherId(updated.getPublisherId());
        }
        if (updated.getOrganizationId() != null && !Objects.equals(updated.getOrganizationId(), old.getOrganizationId())) {
            old.setOrganizationId(updated.getOrganizationId());
        }
        if (updated.getSupportDataGroup() != null && !Objects.equals(updated.getSupportDataGroup(), old.getSupportDataGroup())) {
            old.setSupportDataGroup(updated.getSupportDataGroup());
        }
        if (updated.getVersion() != null && !Objects.equals(updated.getVersion(), old.getVersion())) {
            old.setVersion(updated.getVersion());
        }
        if (updated.getCreatedBy() != null && !Objects.equals(updated.getCreatedBy(), old.getCreatedBy())) {
            old.setCreatedBy(updated.getCreatedBy());
        }
        if (updated.getCreationTime() != null && !Objects.equals(updated.getCreationTime(), old.getCreationTime())) {
            old.setCreationTime(updated.getCreationTime());
        }
    }

    @Override
    public Response updatePublisherById(String publisherId2, String organizationId, Boolean supportDataGroup, String version, String createdBy,
            Date creationTime, String publisherId, SecurityContext securityContext) throws NotFoundException {
        InMemoryPublisher publisher = new InMemoryPublisher();
        publisher.setPublisherId(publisherId);
        publisher.setOrganizationId(organizationId);
        publisher.setSupportDataGroup(supportDataGroup);
        publisher.setVersion(version);
        publisher.setCreatedBy(createdBy);
        publisher.setCreationTime(creationTime);
        return updatePublisherById(publisher, publisherId, securityContext);
    }
}
