package com.plantssoil.common.httpclient;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Post payload via java HttpClient<br/>
 * 
 * @author danialdy
 * @Date 25 Oct 2024 7:28:23 pm
 */
public interface IHttpPoster {
    /**
     * post request to url with the headers and payload string
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
     * @return HttpResponse client response completableFuture
     */
    public CompletableFuture<HttpResponse<String>> post(String url, Map<String, String> headers, String requestId, String payload);
}
