package com.plantssoil.webhook.beans;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-09T17:41:01.999402200+08:00[Asia/Shanghai]")
public class InlineResponse2006 extends com.plantssoil.webhook.ApiResponse {
    private static final long serialVersionUID = -4942682804357629451L;
    private com.plantssoil.webhook.core.registry.InMemoryDataGroup data = null;

    /**
     **/

    @Override
    @Schema(description = "")
    @JsonProperty("data")
    @NotNull
    public com.plantssoil.webhook.core.registry.InMemoryDataGroup getData() {
        return data;
    }

    public void setData(com.plantssoil.webhook.core.registry.InMemoryDataGroup data) {
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
        InlineResponse2006 inlineResponse2006 = (InlineResponse2006) o;
        return Objects.equals(data, inlineResponse2006.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class InlineResponse2006 {\n");
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
