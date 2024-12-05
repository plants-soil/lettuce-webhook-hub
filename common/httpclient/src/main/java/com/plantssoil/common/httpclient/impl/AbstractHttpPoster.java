package com.plantssoil.common.httpclient.impl;

import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.httpclient.IHttpPoster;

/**
 * will prepare the request and do the remote call
 * 
 * @author danialdy
 * @Date 25 Oct 2024 10:16:10 pm
 */
public abstract class AbstractHttpPoster implements IHttpPoster {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractHttpPoster.class.getName());
    protected final static String HEADER_WEBHOOK_ID = "webhook-id";
    protected final static String HEADER_WEBHOOK_TIMESTAMP = "webhook-timestamp";
    protected final static String HEADER_WEBHOOK_SIGNATURE = "webhook-signature";
    private String accessToken;

    /**
     * set the access token which is used to create signature
     * 
     * @param accessToken
     */
    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    protected String getAccessToken() {
        return this.accessToken;
    }

    /**
     * do preparation before call remote request to the URL
     * 
     * @param url       remote client url
     * @param headers   headers will post to client (could add additional headers if
     *                  needed)
     * @param requestId unique request id, which client side will use to avoid
     *                  duplication (post as webhook id in header)
     * @param payload   original request payload string (maybe JSON or XML), should
     *                  not be changed
     */
    protected void beforePost(final String url, Map<String, String> headers, final String requestId, final String payload) {
    }

    @Override
    public CompletableFuture<HttpResponse<String>> post(String url, Map<String, String> headers, String requestId, String payload) {
        // call the preparation
        beforePost(url, headers, requestId, payload);
        if (LOGGER.isInfoEnabled()) {
            String info = String.format("beforePost(url[%s], headers, requestId[%s], payload[%s]) completed.", url, requestId, payload);
            LOGGER.info(info);
        }

        // build http client
        HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).connectTimeout(Duration.ofSeconds(30)).proxy(ProxySelector.getDefault())
                .build();

        // build http request with POST method
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(url)).POST(BodyPublishers.ofString(payload));
        for (Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }

        // call remote url async
        CompletableFuture<HttpResponse<String>> future = client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Sent request requestId[%s] to url[%s].", requestId, url);
        }

        return future;
    }

}
