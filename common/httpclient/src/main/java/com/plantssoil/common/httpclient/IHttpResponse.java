package com.plantssoil.common.httpclient;

/**
 * The http post response object, which with status code and string body
 * 
 * @author danialdy
 * @Date 11 Dec 2024 2:19:12 pm
 */
public interface IHttpResponse {
    /**
     * Get the response status code
     * 
     * @return status code
     */
    public int getStatusCode();

    /**
     * Get the response body (string), mostly should be in JSON/XML format
     * 
     * @return response body string
     */
    public String getBody();
}
