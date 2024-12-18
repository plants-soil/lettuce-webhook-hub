package com.plantssoil.common.httpclient.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.httpclient.IHttpCallback;
import com.plantssoil.common.httpclient.IHttpPoster;
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
    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;
    private int maxIdleConnections = 5;

    class NamedThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final String namePrefix;

        NamedThreadFactory(String factoryName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = factoryName;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix, 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    private OkHttpClient getHttpClient() {
        if (HTTP_CLIENT_INSTANCE == null) {
            synchronized (this) {
                if (HTTP_CLIENT_INSTANCE == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(10)).retryOnConnectionFailure(false);
                    // set max requests & max requests per host if needed
                    if ((this.maxRequests != 64 || this.maxRequestsPerHost != 5) && this.maxRequests > 0 && this.maxRequestsPerHost > 0) {
                        okhttp3.Dispatcher dispatcher = new okhttp3.Dispatcher();
                        dispatcher.setMaxRequests(this.maxRequests);
                        dispatcher.setMaxRequestsPerHost(this.maxRequestsPerHost);
                        builder.dispatcher(dispatcher);
                    }
                    // set max idle connections if needed
                    if (this.maxIdleConnections != 5 && this.maxIdleConnections > 0) {
                        ConnectionPool pool = new ConnectionPool(this.maxIdleConnections, 5, TimeUnit.MINUTES);
                        builder.connectionPool(pool);
                    }
                    HTTP_CLIENT_INSTANCE = builder.build();
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

    @Override
    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    @Override
    public void setMaxRequestsPerHost(int maxRequestsPerHost) {
        this.maxRequestsPerHost = maxRequestsPerHost;
    }

    @Override
    public void setMaxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
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
        // create call
        Call call = createOkHttpClientCall(url, headers, requestId, payload);

        // call remote url sync
        try (Response response = call.execute()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Sent request requestId[%s] to url[%s].", requestId, url);
            }

            return new SimpleHttpResponse(response.code(), response.body().string());
        } catch (Exception e) {
            throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14002, e);
        }
    }

    @Override
    public void post(String url, Map<String, String> headers, String requestId, String payload, IHttpCallback callback) {
        // create call
        Call call = createOkHttpClientCall(url, headers, requestId, payload);
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

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Sent request requestId[%s] to url[%s].", requestId, url);
        }

    }

    private Call createOkHttpClientCall(String url, Map<String, String> headers, String requestId, String payload) {
        // call the preparation
        beforePost(url, headers, requestId, payload);
        if (LOGGER.isInfoEnabled()) {
            String info = String.format("beforePost(url[%s], headers, requestId[%s], payload[%s]) completed.", url, requestId, payload);
            LOGGER.info(info);
        }

        // build http request
        MediaType mt = getMediaType() == null ? MediaType.get("application/json") : MediaType.get(getMediaType());
        Charset cs = getCharset() == null ? Charset.forName("UTF-8") : Charset.forName(getCharset());
        RequestBody body = RequestBody.create(mt, payload.getBytes(cs));
        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
        for (Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }

        // call remote url sync
        return getHttpClient().newCall(requestBuilder.build());
    }
}
