package com.plantssoil.webhook.resteasy;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.webhook.api.NotFoundException;
import com.plantssoil.webhook.api.OrganizationApiService;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IOrganization;
import com.plantssoil.webhook.core.IOrganization.OrganizationStatus;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.registry.InMemoryOrganization;

@ApplicationScoped
@Default
public class OrganizationApiServiceImpl implements OrganizationApiService {
    public OrganizationApiServiceImpl() {
    }

    @Override
    public Response addOrganization(InMemoryOrganization body, SecurityContext securityContext) throws NotFoundException {
        try {
            if (body.getOrganizationId() == null) {
                body.setOrganizationId(EntityUtils.getInstance().createUniqueObjectId());
            }
            if (body.getOrganizationStatus() != null) {
                body.setOrganizationStatus(OrganizationStatus.ACTIVE);
            }
            if (body.getSecretKey() != null) {
                body.setSecretKey(null);
            }
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IOrganization org = (IOrganization) BeanBridge.getInstance().bridge(body);
            r.addOrganization(org);
            return ResponseBuilder.ok().data(body).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response addOrganization(String organizationId, String organizationName, String email, String website, String logoLink, String organizationStatus,
            String createdBy, Date creationTime, SecurityContext securityContext) throws NotFoundException {
        InMemoryOrganization organization = new InMemoryOrganization();
        organization.setOrganizationId(organizationId);
        organization.setOrganizationName(organizationName);
        organization.setEmail(email);
        organization.setWebsite(website);
        organization.setLogoLink(logoLink);
        organization.setOrganizationStatus(OrganizationStatus.ACTIVE);
        organization.setCreatedBy(createdBy);
        organization.setCreationTime(creationTime);
        return addOrganization(organization, securityContext);
    }

    @Override
    public Response findAllOrganizations(Integer page, Integer pageSize, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            List<IOrganization> orgs = r.findAllOrganizations(page, pageSize);
            return ResponseBuilder.ok().data(orgs).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response findOrganizationById(String organizationId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IOrganization org = r.findOrganization(organizationId);
            return ResponseBuilder.ok().data(org).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    @Override
    public Response updateOrganizationById(InMemoryOrganization body, String organizationId, SecurityContext securityContext) throws NotFoundException {
        try {
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IOrganization org = r.findOrganization(organizationId);
            InMemoryOrganization old = BeanBridge.getInstance().bridge(org, InMemoryOrganization.class);
            updateOrganizationValue(old, body);
            org = (IOrganization) BeanBridge.getInstance().bridge(old);
            r.updateOrganization(org);
            return ResponseBuilder.ok().data(old).build();
        } catch (Exception e) {
            return ResponseBuilder.exception(e).build();
        }
    }

    private void updateOrganizationValue(InMemoryOrganization old, InMemoryOrganization updated) {
        if (updated.getOrganizationName() != null && !Objects.equals(updated.getOrganizationName(), old.getOrganizationName())) {
            old.setOrganizationName(updated.getOrganizationName());
        }
        if (updated.getEmail() != null && !Objects.equals(updated.getEmail(), old.getEmail())) {
            old.setEmail(updated.getEmail());
        }
        if (updated.getWebsite() != null && !Objects.equals(updated.getWebsite(), old.getWebsite())) {
            old.setWebsite(updated.getWebsite());
        }
        if (updated.getLogoLink() != null && !Objects.equals(updated.getLogoLink(), old.getLogoLink())) {
            old.setLogoLink(updated.getLogoLink());
        }
        if (updated.getOrganizationStatus() != null && !Objects.equals(updated.getOrganizationStatus(), old.getOrganizationStatus())) {
            old.setOrganizationStatus(updated.getOrganizationStatus());
        }
        if (updated.getCreatedBy() != null && !Objects.equals(updated.getCreatedBy(), old.getCreatedBy())) {
            old.setCreatedBy(updated.getCreatedBy());
        }
        if (updated.getCreationTime() != null && !Objects.equals(updated.getCreationTime(), old.getCreationTime())) {
            old.setCreationTime(updated.getCreationTime());
        }
    }

    @Override
    public Response updateOrganizationById(String organizationId2, String organizationName, String email, String website, String logoLink,
            String organizationStatus, String createdBy, Date creationTime, String organizationId, SecurityContext securityContext) throws NotFoundException {
        InMemoryOrganization organization = new InMemoryOrganization();
        organization.setOrganizationId(organizationId);
        organization.setOrganizationName(organizationName);
        organization.setEmail(email);
        organization.setWebsite(website);
        organization.setLogoLink(logoLink);
        if (organizationStatus != null) {
            organization.setOrganizationStatus(OrganizationStatus.valueOf(organizationStatus));
        }
        organization.setCreatedBy(createdBy);
        organization.setCreationTime(creationTime);
        return updateOrganizationById(organization, organizationId, securityContext);
    }
}
