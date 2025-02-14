package com.plantssoil.webhook.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-10T20:51:53.215925500+08:00[Asia/Shanghai]")
public class WebhookUnsubscribeDataGroupsBody {
    private String webhookId = null;
    private List<String> dataGroupIds = new ArrayList<String>();

    /**
     * ID of webhook
     **/

    @Schema(required = true, description = "ID of webhook")
    @JsonProperty("webhookId")
    @NotNull
    public String getWebhookId() {
        return webhookId;
    }

    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    /**
     * ID array of publisher data groups to unsubscribe
     **/

    @Schema(required = true, description = "ID array of publisher data groups to unsubscribe")
    @JsonProperty("dataGroupIds")
    @NotNull
    public List<String> getDataGroupIds() {
        return dataGroupIds;
    }

    public void setDataGroupIds(List<String> dataGroupIds) {
        this.dataGroupIds = dataGroupIds;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebhookUnsubscribeDataGroupsBody webhookUnsubscribeDataGroupsBody = (WebhookUnsubscribeDataGroupsBody) o;
        return Objects.equals(webhookId, webhookUnsubscribeDataGroupsBody.webhookId)
                && Objects.equals(dataGroupIds, webhookUnsubscribeDataGroupsBody.dataGroupIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(webhookId, dataGroupIds);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class WebhookUnsubscribeDataGroupsBody {\n");

        sb.append("    webhookId: ").append(toIndentedString(webhookId)).append("\n");
        sb.append("    dataGroupIds: ").append(toIndentedString(dataGroupIds)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
