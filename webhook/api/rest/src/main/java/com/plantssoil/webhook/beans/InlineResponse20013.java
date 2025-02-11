package com.plantssoil.webhook.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-09T17:41:01.999402200+08:00[Asia/Shanghai]")
public class InlineResponse20013 extends com.plantssoil.webhook.ApiResponse {
    private static final long serialVersionUID = 7957930558987056368L;
    private List<com.plantssoil.webhook.core.logging.InMemoryWebhookLogLine> data = new ArrayList<com.plantssoil.webhook.core.logging.InMemoryWebhookLogLine>();

    /**
     **/

    @Override
    @Schema(description = "")
    @JsonProperty("data")
    @NotNull
    public List<com.plantssoil.webhook.core.logging.InMemoryWebhookLogLine> getData() {
        return data;
    }

    public void setData(List<com.plantssoil.webhook.core.logging.InMemoryWebhookLogLine> data) {
        this.data = data;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineResponse20013 inlineResponse20013 = (InlineResponse20013) o;
        return Objects.equals(data, inlineResponse20013.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class InlineResponse20013 {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    data: ").append(toIndentedString(data)).append("\n");
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
