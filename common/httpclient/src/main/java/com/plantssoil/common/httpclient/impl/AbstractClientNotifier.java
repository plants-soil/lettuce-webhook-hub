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
import java.util.function.Consumer;

import com.plantssoil.common.httpclient.IClientNotifier;

/**
 * will prepare the request and do the remote call
 * 
 * @author danialdy
 * @Date 25 Oct 2024 10:16:10 pm
 */
public abstract class AbstractClientNotifier implements IClientNotifier {
    protected final static String HEADER_WEBHOOK_ID = "webhook-id";
    protected final static String HEADER_WEBHOOK_TIMESTAMP = "webhook-timestamp";
    protected final static String HEADER_WEBHOOK_SIGNATURE = "webhook-signature";

    /**
     * do preparation before call remote request to the URL
     * 
     * @param url       remote client url
     * @param headers   headers will post to client (could add additional headers if
     *                  needed)
     * @param messageId unique message id, which client side will use to avoid
     *                  duplication
     * @param payload   original request payload string (maybe JSON or XML), should
     *                  not be changed
     */
    protected void beforePost(final String url, Map<String, String> headers, final String messageId, final String payload) {
    }

    @Override
    public CompletableFuture<HttpResponse<String>> postNotification(String url, Map<String, String> headers, String messageId, String payload,
            Consumer<? super HttpResponse<String>> action) {
        // call the preparation
        beforePost(url, headers, messageId, payload);

        // build http client
        HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).connectTimeout(Duration.ofSeconds(30)).proxy(ProxySelector.getDefault())
                .build();

        // build http request with POST method
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(url)).POST(BodyPublishers.ofString(payload));
        for (Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }

        // call remote url async
        CompletableFuture<HttpResponse<String>> completableResponse = client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        completableResponse.thenAccept((HttpResponse<String> response) -> {
            action.accept(response);
        });
        return completableResponse;
    }

}
