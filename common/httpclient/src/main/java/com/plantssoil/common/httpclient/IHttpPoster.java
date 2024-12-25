package com.plantssoil.common.httpclient;

import java.util.Map;

import com.plantssoil.common.httpclient.exception.HttpClientException;
import com.plantssoil.common.httpclient.impl.BlankHttpPoster;
import com.plantssoil.common.httpclient.impl.FixedKeyHttpPoster;
import com.plantssoil.common.httpclient.impl.SignaturedHttpPoster;

/**
 * Post payload via java HttpClient<br/>
 * 
 * @author danialdy
 * @Date 25 Oct 2024 7:28:23 pm
 */
public interface IHttpPoster {
    /**
     * The Security Strategy<br/>
     * <ul>
     * <li>{@link SecurityStrategy#SIGNATURE} - Will generate signature in header
     * when callback subscriber ({@link IOrganization#getWebhookUrl()})</li>
     * <li>{@link SecurityStrategy#TOKEN} - Will add the secret key as signature in
     * header when callback subscriber ({@link IOrganization#getWebhookUrl()})</li>
     * <li>{@link SecurityStrategy#NONE} - Won't add signature in header when
     * callback subscriber ({@link IOrganization#getWebhookUrl()})</li>
     * </ul>
     * 
     * @author danialdy
     * @Date 13 Nov 2024 2:13:00 pm
     */
    public enum SecurityStrategy {
        SIGNATURE, TOKEN, NONE
    }

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
    public IHttpResponse post(String url, Map<String, String> headers, String requestId, String payload);

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
    public void post(String url, Map<String, String> headers, String requestId, String payload, IHttpCallback callback);

    /**
     * set the access token which is used to create signature
     * 
     * @param accessToken
     */
    public void setAccessToken(String accessToken);

    /**
     * set the media type of the http poster (application/json, application/xml)
     * 
     * @param mediaType the media type (application/json, application/xml)
     */
    public void setMediaType(String mediaType);

    /**
     * set the charset of the http poster body, used to encode/decode http body
     * 
     * @param charset charset of the http poster body
     */
    public void setCharset(String charset);

    /**
     * set max requests concurrently requesting, defaults to 64
     * 
     * @param maxRequests the max requests
     */
    public void setMaxRequests(int maxRequests);

    /**
     * set max requests concurrently requesting per host, defaults to 5
     * 
     * @param maxRequestsPerHost the max requests per host
     */
    public void setMaxRequestsPerHost(int maxRequestsPerHost);

    /**
     * set max idle connections in the connection pool (which will be released 5
     * minutes later if inactive), defaults to 5
     * 
     * @param maxIdleConnections the max idle connections
     */
    public void setMaxIdleConnections(int maxIdleConnections);

    /**
     * create IHttpPoster instance base security strategy
     * 
     * @param securityStrategy the security strategy chosen
     * @return IHttpPoster instance
     */
    public static IHttpPoster createInstance(SecurityStrategy securityStrategy) {
        if (SecurityStrategy.NONE.equals(securityStrategy)) {
            return new BlankHttpPoster();
        } else if (SecurityStrategy.TOKEN.equals(securityStrategy)) {
            return new FixedKeyHttpPoster();
        } else if (SecurityStrategy.SIGNATURE.equals(securityStrategy)) {
            return new SignaturedHttpPoster();
        } else {
            throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14003, "No security strategy specified!");
        }
    }
}
