package com.plantssoil.common.httpclient.impl;

import java.util.Map;

import com.plantssoil.common.httpclient.exception.HttpClientException;

/**
 * Use the accessToken (fixed key) as the signature
 * 
 * @author danialdy
 * @Date 25 Oct 2024 10:48:34 pm
 */
public class FixedKeyHttpPoster extends AbstractHttpPoster {
    private String accessToken;

    /**
     * set the access token, which is used as the signature to call remote url
     * 
     * @param accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    protected void beforePost(String url, Map<String, String> headers, String messageId, String payload) {
        super.beforePost(url, headers, messageId, payload);
        headers.put(HEADER_WEBHOOK_ID, messageId);
        headers.put(HEADER_WEBHOOK_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        if (this.accessToken != null) {
            headers.put(HEADER_WEBHOOK_SIGNATURE, this.accessToken);
        } else {
            throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14001, "The secret key should not be null!");
        }
    }

}
