package com.plantssoil.common.httpclient.impl;

import java.util.Map;

import com.plantssoil.common.httpclient.exception.HttpClientException;

/**
 * Use the Secret Key (fixed key) as the signature
 * 
 * @author danialdy
 * @Date 25 Oct 2024 10:48:34 pm
 */
public class FixedKeyHttpPoster extends AbstractHttpPoster {
    @Override
    protected void beforePost(String url, Map<String, String> headers, String requestId, String payload) {
        super.beforePost(url, headers, requestId, payload);
        headers.put(HEADER_WEBHOOK_ID, requestId);
        headers.put(HEADER_WEBHOOK_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        if (getSecretKey() != null) {
            headers.put(HEADER_WEBHOOK_SIGNATURE, getSecretKey());
        } else {
            throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14001, "The secret key should not be null!");
        }
    }

}
