package com.plantssoil.webhook;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The API Response bean
 * 
 * @author danialdy
 * @Date 8 Feb 2025 10:51:16 pm
 */
@XmlRootElement
public class ApiResponse implements java.io.Serializable {
    private static final long serialVersionUID = 2838001177387634370L;

    private Integer code = null;
    private String message = null;
    private Object data = null;

    /**
     * The response code
     **/
    @JsonProperty("code")
    @NotNull
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * The response message
     **/
    @JsonProperty("message")
    @NotNull
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * The data attached to the response
     **/
    @XmlElement(name = "data")
    @JsonProperty("data")
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
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
        ApiResponse _apiResponse = (ApiResponse) o;
        return Objects.equals(code, _apiResponse.code) && Objects.equals(message, _apiResponse.message) && Objects.equals(data, _apiResponse.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ApiResponse {\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
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
