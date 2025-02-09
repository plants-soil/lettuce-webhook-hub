package com.plantssoil.webhook.api.impl;

import java.util.List;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.webhook.ApiResponse;

/**
 * The response builder to construct JAX-RS Restful Easy API response
 * 
 * @author danialdy
 * @Date 8 Feb 2025 11:22:15 pm
 */
public class ResponseBuilder {
    private final static Logger LOGGER = LoggerFactory.getLogger(ResponseBuilder.class);
    private Response.Status status = Response.Status.BAD_REQUEST;
    private String customizedMessage = null;
    private Object data = null;

    private ResponseBuilder() {
    }

    /**
     * Return the OK response
     * 
     * @return New Response Builder instance with OK response
     */
    public static ResponseBuilder ok() {
        ResponseBuilder builder = new ResponseBuilder();
        builder.status = Response.Status.OK;
        return builder;
    }

    /**
     * Return the Bad Request response, with Throwable message
     * 
     * @param t The exception object
     * @return New Response Builder instance with Bad Request response
     */
    public static ResponseBuilder exception(Throwable t) {
        if (LOGGER.isDebugEnabled()) {
            t.printStackTrace();
        }
        ResponseBuilder builder = new ResponseBuilder();
        builder.status = Response.Status.BAD_REQUEST;
        builder.customizedMessage = t.getMessage();
        return builder;
    }

    /**
     * Return the Unauthorized response
     * 
     * @return New Response Builder instance with Unauthorized response
     */
    public static ResponseBuilder unauthorized() {
        ResponseBuilder builder = new ResponseBuilder();
        builder.status = Response.Status.UNAUTHORIZED;
        return builder;
    }

    /**
     * Return the Not Found response
     * 
     * @return New Response Builder instance with Not Found response
     */
    public static ResponseBuilder notFound() {
        ResponseBuilder builder = new ResponseBuilder();
        builder.status = Response.Status.NOT_FOUND;
        return builder;
    }

    /**
     * Provide the response message, will provide default message if this method not
     * called
     * 
     * @param message The customized resonse message
     * @return Current builder instance
     */
    public ResponseBuilder message(String message) {
        this.customizedMessage = message;
        return this;
    }

    /**
     * Provide the response data<br/>
     * <ul>
     * <li>Will change the response status to {@link Response.Status#NOT_FOUND} if
     * parameter data is null.</li>
     * <li>Will change the response status to {@link Response.Status#NOT_FOUND} if
     * parameter data is {@link java.util.List} and no element in it.</li>
     * <li>When response status is not {@link Response.Status#OK}, it will be
     * changed to {@link Response.Status#OK} if parameter data is not empty.</li>
     * </ul>
     * 
     * @param data The data response, could be POJO or List
     * @return Current builder instance
     */
    public ResponseBuilder data(Object data) {
        if (data == null) {
            this.status = Response.Status.NOT_FOUND;
            this.data = null;
        } else if (data instanceof List) {
            List<?> list = (List<?>) data;
            if (list.size() > 0) {
                this.data = list;
            } else {
                this.status = Response.Status.NOT_FOUND;
                this.data = null;
            }
        } else {
            this.data = data;
        }

        if (this.data != null && this.status != Response.Status.OK) {
            this.status = Response.Status.OK;
        }

        return this;
    }

    /**
     * Create a Response instance from the current ResponseBuilder.
     * 
     * @return Response instance
     */
    public Response build() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(this.status.getStatusCode());
        apiResponse.setMessage(this.customizedMessage == null ? this.status.getReasonPhrase() : this.customizedMessage);
        if (this.data != null) {
            apiResponse.setData(this.data);
        }
        return Response.ok().entity(apiResponse).build();
    }
}
