package com.plantssoil.webhook.resteasy;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.webhook.api.NotFoundException;
import com.plantssoil.webhook.api.SubscriberApiService;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.registry.InMemorySubscriber;

@ApplicationScoped
@Default
public class SubscriberApiServiceImpl implements SubscriberApiService {
    public SubscriberApiServiceImpl() {
    }

    @Override
    public Response addSubscriber(InMemorySubscriber body, SecurityContext securityContext) throws NotFoundException {
        try {
            if (body.getSubscriberId() == null) {
                body.setSubscriberId(EntityUtils.getInstance().createUniqueObjectId());
            }
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            ISubscriber subscriber = (ISubscriber) BeanBridge.getInstance().bridge(body);
            r.addSubscriber(subscriber);
            return ResponseBuilder.ok().data(body).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response addSubscriber(String subscriberId, String organizationId, SecurityContext securityContext) throws NotFoundException {
        InMemorySubscriber subscriber = new InMemorySubscriber();
        subscriber.setSubscriberId(subscriberId);
        subscriber.setOrganizationId(organizationId);
        return addSubscriber(subscriber, securityContext);
    }

    @Override
    public Response deleteSubscriber(String subscriberId, String apiKey, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            ISubscriber subscriber = r.findSubscriber(subscriberId);
            r.removeSubscriber(subscriber);
            return ResponseBuilder.ok().build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findAllSubscribers(Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            List<ISubscriber> subscribers = r.findAllSubscribers(page, pageSize);
            return ResponseBuilder.ok().data(subscribers).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findSubscriberById(String subscriberId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            ISubscriber subscriber = r.findSubscriber(subscriberId);
            return ResponseBuilder.ok().data(subscriber).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response updateSubscriberById(InMemorySubscriber body, String subscriberId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            ISubscriber old = r.findSubscriber(subscriberId);
            if (body.getOrganizationId() != null && !Objects.equals(old.getOrganizationId(), body.getOrganizationId())) {
                old.setOrganizationId(body.getOrganizationId());
            }
            r.updateSubscriber(old);
            return ResponseBuilder.ok().data(old).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response updateSubscriberById(String subscriberId2, String organizationId, String subscriberId, SecurityContext securityContext)
            throws NotFoundException {
        InMemorySubscriber updated = new InMemorySubscriber();
        updated.setOrganizationId(organizationId);
        return updateSubscriberById(updated, subscriberId, securityContext);
    }
}
