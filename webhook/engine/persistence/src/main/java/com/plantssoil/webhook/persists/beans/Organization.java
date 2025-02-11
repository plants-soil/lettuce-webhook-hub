package com.plantssoil.webhook.persists.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.plantssoil.webhook.core.ClonableBean;
import com.plantssoil.webhook.core.IOrganization;

/**
 * The organization bean, publisher and subscriber are all derived from this
 * object
 * 
 * @author danialdy
 * @Date 19 Nov 2024 10:11:44 am
 */
@Entity
@Table(name = "LETTUCE_ORGANIZATION", indexes = { @Index(name = "idx_organization_email", columnList = "email") })
public class Organization extends ClonableBean implements IOrganization {
    private static final long serialVersionUID = -1431122765874295449L;

    @Id
    private String organizationId;
    private String organizationName;
    @Column(nullable = false, unique = true)
    private String email;
    private String website;
    private String logoLink;
    private String secretKey;
    @Enumerated(EnumType.STRING)
    private OrganizationStatus organizationStatus;
    private String createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    public Organization() {
    }

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLogoLink() {
        return logoLink;
    }

    public void setLogoLink(String logoLink) {
        this.logoLink = logoLink;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public OrganizationStatus getOrganizationStatus() {
        return organizationStatus;
    }

    public void setOrganizationStatus(OrganizationStatus organizationStatus) {
        this.organizationStatus = organizationStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
