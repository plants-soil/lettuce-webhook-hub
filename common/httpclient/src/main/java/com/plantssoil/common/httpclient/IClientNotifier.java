package com.plantssoil.common.httpclient;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Notify clients via java HttpClient<br/>
 * 
 * @author danialdy
 * @Date 25 Oct 2024 7:28:23 pm
 */
public interface IClientNotifier {
    /**
     * send request to notify url with the headers and requestBody string
     * 
     * @param url       Client URL to receive messages (only need receive POST
     *                  method)
     * @param headers   headerName and value, additional headers for special
     *                  customer needs (not mandatory)
     * @param messageId Message ID (Webhook ID), this is the unique id for client
     *                  side to avoid duplicated consume the message
     * @param payload   request payload string (xml, json, etc)
     * @param action    Async consumer
     * 
     * @return HttpResponse client response completableFuture
     */
    public CompletableFuture<HttpResponse<String>> postNotification(String url, Map<String, String> headers, String messageId, String payload,
            Consumer<? super HttpResponse<String>> action);
}
