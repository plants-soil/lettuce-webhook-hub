package com.plantssoil.common.httpclient;

import java.util.Map;

import com.plantssoil.common.config.IConfigurable;

/**
 * Http client, which used to post requests to specific url, in order to call
 * the API on remote URL
 * 
 * @author danialdy
 * @Date 25 Dec 2024 11:48:41 am
 */
public interface IHttpClient extends IConfigurable {
    /**
     * set max requests concurrently requesting, defaults to 64
     * 
     * @param maxRequests the max requests
     */
    public IHttpClient maxRequests(int maxRequests);

    /**
     * set max requests concurrently requesting per host, defaults to 5
     * 
     * @param maxRequestsPerHost the max requests per host
     */
    public IHttpClient maxRequestsPerHost(int maxRequestsPerHost);

    /**
     * set max idle connections in the connection pool (which will be released 5
     * minutes later if inactive), defaults to 5
     * 
     * @param maxIdleConnections the max idle connections
     */
    public IHttpClient maxIdleConnections(int maxIdleConnections);

    /**
     * Synchronized post request to url with the headers and payload string
     * 
     * @param url       HTTP(S) URL to receive payload (only need receive POST
     *                  method)
     * @param headers   headerName and value, additional headers for special
     *                  customer needs (not mandatory)
     * @param requestId Request ID (post as Webhook ID in header), this is the
     *                  unique id for client side to avoid duplicated consume the
     *                  message
     * @param payload   request payload string (xml, json, etc)
     * 
     * @return HttpResponse http response
     */
    public IHttpResponse post(String url, Map<String, String> headers, String requestId, String payload, String mediaType, String charset);

    /**
     * Asynchronized post request to url with the headers and payload string
     * 
     * @param url       HTTP(S) URL to receive payload (only need receive POST
     *                  method)
     * @param headers   headerName and value, additional headers for special
     *                  customer needs (not mandatory)
     * @param requestId Request ID (post as Webhook ID in header), this is the
     *                  unique id for client side to avoid duplicated consume the
     *                  message
     * @param payload   request payload string (xml, json, etc)
     * 
     * @param callback  the call back object which will receive failure or success
     */
    public void post(String url, Map<String, String> headers, String requestId, String payload, String mediaType, String charset, IHttpCallback callback);

}
