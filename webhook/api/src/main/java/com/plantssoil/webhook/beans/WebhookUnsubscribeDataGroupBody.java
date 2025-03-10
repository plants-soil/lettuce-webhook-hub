package com.plantssoil.webhook.beans;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-10T20:51:53.215925500+08:00[Asia/Shanghai]")
public class WebhookUnsubscribeDataGroupBody {
    private String webhookId = null;
    private String dataGroupId = null;

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
     * ID of publisher data group to unsubscribe
     **/

    @Schema(required = true, description = "ID of publisher data group to unsubscribe")
    @JsonProperty("dataGroupId")
    @NotNull
    public String getDataGroupId() {
        return dataGroupId;
    }

    public void setDataGroupId(String dataGroupId) {
        this.dataGroupId = dataGroupId;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebhookUnsubscribeDataGroupBody webhookUnsubscribeDataGroupBody = (WebhookUnsubscribeDataGroupBody) o;
        return Objects.equals(webhookId, webhookUnsubscribeDataGroupBody.webhookId) && Objects.equals(dataGroupId, webhookUnsubscribeDataGroupBody.dataGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(webhookId, dataGroupId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class WebhookUnsubscribeDataGroupBody {\n");

        sb.append("    webhookId: ").append(toIndentedString(webhookId)).append("\n");
        sb.append("    dataGroupId: ").append(toIndentedString(dataGroupId)).append("\n");
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
