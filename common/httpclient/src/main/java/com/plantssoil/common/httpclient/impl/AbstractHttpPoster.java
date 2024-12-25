package com.plantssoil.common.httpclient.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.httpclient.IHttpCallback;
import com.plantssoil.common.httpclient.IHttpClient;
import com.plantssoil.common.httpclient.IHttpPoster;
import com.plantssoil.common.httpclient.IHttpResponse;

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
    private static volatile IHttpClient HTTP_CLIENT_INSTANCE;
    private String accessToken;
    private String mediaType;
    private String charset;
    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;
    private int maxIdleConnections = 5;

    private IHttpClient createHttpClientInstance() {
        String httpclientImpl = ConfigFactory.getInstance().getConfiguration().getString(LettuceConfiguration.HTTPCLIENT_CONFIGURABLE);
        if (httpclientImpl == null || httpclientImpl.strip().equals(JdkHttpClientImpl.class.getName())) {
            return new JdkHttpClientImpl();
        }
        return new OkHttpClientImpl();
    }

    private IHttpClient getHttpClient() {
        if (HTTP_CLIENT_INSTANCE == null) {
            synchronized (AbstractHttpPoster.class) {
                if (HTTP_CLIENT_INSTANCE == null) {
                    HTTP_CLIENT_INSTANCE = createHttpClientInstance().maxIdleConnections(this.maxIdleConnections).maxRequests(this.maxRequests)
                            .maxRequestsPerHost(this.maxRequestsPerHost);
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
        // call the preparation
        beforePost(url, headers, requestId, payload);
        if (LOGGER.isInfoEnabled()) {
            String info = String.format("beforePost(url[%s], headers, requestId[%s], payload[%s]) completed.", url, requestId, payload);
            LOGGER.info(info);
        }
        IHttpResponse r = getHttpClient().post(url, headers, requestId, payload, getMediaType(), getCharset());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Sent request requestId[%s] to url[%s].", requestId, url);
        }
        return r;
    }

    @Override
    public void post(String url, Map<String, String> headers, String requestId, String payload, IHttpCallback callback) {
        // call the preparation
        beforePost(url, headers, requestId, payload);
        if (LOGGER.isInfoEnabled()) {
            String info = String.format("beforePost(url[%s], headers, requestId[%s], payload[%s]) completed.", url, requestId, payload);
            LOGGER.info(info);
        }
        getHttpClient().post(url, headers, requestId, payload, getMediaType(), getCharset(), callback);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Sent request requestId[%s] to url[%s].", requestId, url);
        }
    }

}
