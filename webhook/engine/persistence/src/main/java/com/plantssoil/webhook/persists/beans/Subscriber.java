package com.plantssoil.webhook.persists.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.plantssoil.webhook.core.ClonableBean;
import com.plantssoil.webhook.core.ISubscriber;

/**
 * The subscriber which will listen publisher's events & messages
 * 
 * @author danialdy
 * @Date 28 Dec 2024 12:26:24 pm
 */
@Entity
@Table(name = "LETTUCE_SUBSCRIBER", indexes = { @Index(name = "idx_subscriber_orgid", columnList = "organizationId") })
public class Subscriber extends ClonableBean implements ISubscriber, Serializable {
    private static final long serialVersionUID = 1848865673559702879L;
    @Id
    private String subscriberId;
    @Column(unique = true)
    private String organizationId;

    public Subscriber() {
    }

    @Override
    public String getSubscriberId() {
        return subscriberId;
    }

    @Override
    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

}
