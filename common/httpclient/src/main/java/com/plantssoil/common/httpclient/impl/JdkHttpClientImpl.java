package com.plantssoil.common.httpclient.impl;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;

import com.plantssoil.common.httpclient.IHttpCallback;
import com.plantssoil.common.httpclient.IHttpResponse;

/**
 * The IHttpClient implementation via JDK HttpClient
 * 
 * @author danialdy
 * @Date 25 Dec 2024 4:27:21 pm
 */
public class JdkHttpClientImpl extends AbstractHttpClientImpl {
    private volatile JdkHttpClientAdaptor httpclient;

    JdkHttpClientImpl() {
    }

    private JdkHttpClientAdaptor getHttpClient() {
        if (this.httpclient == null) {
            synchronized (JdkHttpClientImpl.class) {
                if (this.httpclient == null) {
                    this.httpclient = new JdkHttpClientAdaptor();
                    // set max requests & max requests per host if needed
                    if ((getMaxRequests() != 64 || getMaxRequestsPerHost() != 5) && getMaxRequests() > 0 && getMaxRequestsPerHost() > 0) {
                        this.httpclient.maxRequests(getMaxRequests()).maxRequestsPerHost(getMaxRequestsPerHost());
                    }
                    // set max idle connections if needed
                    if (getMaxIdleConnections() != 5 && getMaxIdleConnections() > 0) {
                        this.httpclient.maxIdleConnections(getMaxIdleConnections());
                    }
                }
            }
        }
        return this.httpclient;
    }

    private java.net.http.HttpRequest.Builder createRequestBuilder(String url, Map<String, String> headers, String requestId, String payload, String mediaType,
            String charset) {
        // build http request with POST method
        String mt = mediaType == null ? "application/json" : mediaType;
        Charset cs = charset == null ? Charset.forName("UTF-8") : Charset.forName(charset);
        java.net.http.HttpRequest.Builder requestBuilder = java.net.http.HttpRequest.newBuilder(java.net.URI.create(url)).timeout(Duration.ofSeconds(10))
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(payload, cs));
        for (Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }
        requestBuilder.header("Content-Type", mt);
        return requestBuilder;
    }

    @Override
    public IHttpResponse post(String url, Map<String, String> headers, String requestId, String payload, String mediaType, String charset) {
        java.net.http.HttpRequest.Builder requestBuilder = createRequestBuilder(url, headers, requestId, payload, mediaType, charset);
        return getHttpClient().send(requestBuilder.build());
    }

    @Override
    public void post(String url, Map<String, String> headers, String requestId, String payload, String mediaType, String charset, IHttpCallback callback) {
        java.net.http.HttpRequest.Builder requestBuilder = createRequestBuilder(url, headers, requestId, payload, mediaType, charset);
        getHttpClient().sendAsync(requestBuilder.build(), callback);
    }

}
