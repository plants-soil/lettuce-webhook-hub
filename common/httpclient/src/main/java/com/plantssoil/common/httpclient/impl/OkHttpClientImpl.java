package com.plantssoil.common.httpclient.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.plantssoil.common.httpclient.IHttpCallback;
import com.plantssoil.common.httpclient.IHttpResponse;
import com.plantssoil.common.httpclient.exception.HttpClientException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The IHttpClient implementation via OkHttpClient
 * 
 * @author danialdy
 * @Date 25 Dec 2024 4:26:49 pm
 */
class OkHttpClientImpl extends AbstractHttpClientImpl {
    private volatile OkHttpClient httpclient;

    OkHttpClientImpl() {
    }

    private OkHttpClient getHttpClient() {
        if (this.httpclient == null) {
            synchronized (OkHttpClientImpl.class) {
                if (this.httpclient == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(10)).retryOnConnectionFailure(false);
                    // set max requests & max requests per host if needed
                    if ((getMaxRequests() != 64 || getMaxRequestsPerHost() != 5) && getMaxRequests() > 0 && getMaxRequestsPerHost() > 0) {
                        okhttp3.Dispatcher dispatcher = new okhttp3.Dispatcher();
                        dispatcher.setMaxRequests(getMaxRequests());
                        dispatcher.setMaxRequestsPerHost(getMaxRequestsPerHost());
                        builder.dispatcher(dispatcher);
                    }
                    // set max idle connections if needed
                    if (getMaxIdleConnections() != 5 && getMaxIdleConnections() > 0) {
                        ConnectionPool pool = new ConnectionPool(getMaxIdleConnections(), 5, TimeUnit.MINUTES);
                        builder.connectionPool(pool);
                    }
                    this.httpclient = builder.build();
                }
            }
        }
        return this.httpclient;
    }

    private Call createOkHttpClientCall(String url, Map<String, String> headers, String requestId, String payload, String mediaType, String charset) {
        // build http request
        MediaType mt = mediaType == null ? MediaType.get("application/json") : MediaType.get(mediaType);
        Charset cs = charset == null ? Charset.forName("UTF-8") : Charset.forName(charset);
        RequestBody body = RequestBody.create(mt, payload.getBytes(cs));
        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
        for (Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }

        // call remote url sync
        return getHttpClient().newCall(requestBuilder.build());
    }

    @Override
    public IHttpResponse post(String url, Map<String, String> headers, String requestId, String payload, String mediaType, String charset) {
        // create call
        Call call = createOkHttpClientCall(url, headers, requestId, payload, mediaType, charset);

        // call remote url sync
        try (Response response = call.execute()) {
            return new SimpleHttpResponse(response.code(), response.body().string());
        } catch (Exception e) {
            throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14002, e);
        }
    }

    @Override
    public void post(String url, Map<String, String> headers, String requestId, String payload, String mediaType, String charset, IHttpCallback callback) {
        // create call
        Call call = createOkHttpClientCall(url, headers, requestId, payload, mediaType, charset);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(new SimpleHttpResponse(response.code(), response.body().string()));
            }
        });
    }

}
