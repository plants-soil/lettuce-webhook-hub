package com.plantssoil.webhook.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The publisher which will publish events & messages to subscribers
 */
@Entity
@Table(name = "LETTUCE_PUBLISHER", indexes = { @Index(name = "idx_publisher_orgid", columnList = "organizationId") })
public class Publisher implements Serializable {
	private static final long serialVersionUID = -1820991401679378737L;
	@Id
	private String publisherId;
	@Column(unique = true)
	private String organizationId;
	private boolean supportDataGroup;
	private String version;
	private String createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime;

	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public boolean isSupportDataGroup() {
		return supportDataGroup;
	}

	public void setSupportDataGroup(boolean supportDataGroup) {
		this.supportDataGroup = supportDataGroup;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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
