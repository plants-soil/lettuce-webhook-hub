package com.plantssoil.webhook.persists.beans;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The data groups subscribed by webhook, one webhook could subscribe events
 * from multiple datagroups
 * 
 * @author danialdy
 * @Date 28 Dec 2024 12:21:02 pm
 */
@Entity
@Table(name = "LETTUCE_DATAGROUPSUB", uniqueConstraints = @UniqueConstraint(columnNames = { "webhookId", "dataGroupId" }), indexes = {
        @Index(name = "idx_dgsub_webhookId", columnList = "webhookId"), @Index(name = "idx_dgsub_dataGroupId", columnList = "dataGroupId") })
public class DataGroupSubscribed implements java.io.Serializable {
    private static final long serialVersionUID = -3373068800005798198L;
    @Id
    private String dataGroupSubedId;
    private String webhookId;
    private String dataGroupId;
    private String dataGroup;

    public DataGroupSubscribed() {
    }

    public String getDataGroupSubedId() {
        return dataGroupSubedId;
    }

    public void setDataGroupSubedId(String dataGroupSubedId) {
        this.dataGroupSubedId = dataGroupSubedId;
    }

    public String getWebhookId() {
        return webhookId;
    }

    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    public String getDataGroupId() {
        return dataGroupId;
    }

    public void setDataGroupId(String dataGroupId) {
        this.dataGroupId = dataGroupId;
    }

    public String getDataGroup() {
        return dataGroup;
    }

    public void setDataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
    }

}
