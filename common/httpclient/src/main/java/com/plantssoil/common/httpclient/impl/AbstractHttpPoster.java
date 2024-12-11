package com.plantssoil.common.httpclient.impl;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.httpclient.IHttpPoster;
import com.plantssoil.common.httpclient.IHttpResponse;
import com.plantssoil.common.httpclient.exception.HttpClientException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private static volatile OkHttpClient HTTP_CLIENT_INSTANCE;
    private String accessToken;
    private String mediaType;
    private String charset;

    private OkHttpClient getHttpClient() {
        if (HTTP_CLIENT_INSTANCE == null) {
            synchronized (this) {
                if (HTTP_CLIENT_INSTANCE == null) {
                    HTTP_CLIENT_INSTANCE = new OkHttpClient();
                }
            }
        }
        return HTTP_CLIENT_INSTANCE;
    }

    protected String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    protected String getMediaType() {
        return this.mediaType;
    }

    @Override
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    protected String getCharset() {
        return this.charset;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
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
    public IHttpResponse post(String url, Map<String, String> headers, String requestId, String payload) {
        return postOkHttpClinet(url, headers, requestId, payload);
    }

    // High CPU load if use JDK http client, so change to OkHttpClient
//    private IHttpResponse postJdkHttpClinet(String url, Map<String, String> headers, String requestId, String payload) {
//        // call the preparation
//        beforePost(url, headers, requestId, payload);
//        if (LOGGER.isInfoEnabled()) {
//            String info = String.format("beforePost(url[%s], headers, requestId[%s], payload[%s]) completed.", url, requestId, payload);
//            LOGGER.info(info);
//        }
//
//        // build http client
//        HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).connectTimeout(Duration.ofSeconds(30)).proxy(ProxySelector.getDefault())
//                .build();
//
//        // build http request with POST method
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(url)).POST(BodyPublishers.ofString(payload));
//        for (Entry<String, String> entry : headers.entrySet()) {
//            requestBuilder.header(entry.getKey(), entry.getValue());
//        }
//
//        // call remote url sync
//        HttpResponse<String> response = null;
//        try {
//            response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
//        } catch (IOException | InterruptedException e) {
//            throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14002, e);
//        }
//        if (LOGGER.isInfoEnabled()) {
//            LOGGER.info("Sent request requestId[%s] to url[%s].", requestId, url);
//        }
//
//        return new SimpleHttpResponse(response.statusCode(), response.body());
//    }

    private IHttpResponse postOkHttpClinet(String url, Map<String, String> headers, String requestId, String payload) {
        // call the preparation
        beforePost(url, headers, requestId, payload);
        if (LOGGER.isInfoEnabled()) {
            String info = String.format("beforePost(url[%s], headers, requestId[%s], payload[%s]) completed.", url, requestId, payload);
            LOGGER.info(info);
        }

        // build http request
        MediaType mt = getMediaType() == null ? null : MediaType.get(getMediaType());
        Charset cs = getCharset() == null ? null : Charset.forName(getCharset());
        RequestBody body = null;
        if (cs == null) {
            body = mt == null ? RequestBody.create(payload.getBytes()) : RequestBody.create(payload, mt);
        } else {
            body = mt == null ? RequestBody.create(payload.getBytes()) : RequestBody.create(payload.getBytes(cs), mt);
        }
        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
        for (Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }

        // call remote url sync
        try (Response response = getHttpClient().newCall(requestBuilder.build()).execute()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Sent request requestId[%s] to url[%s].", requestId, url);
            }

            return new SimpleHttpResponse(response.code(), response.body().string());
        } catch (Exception e) {
            throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14002, e);
        }
    }
}
