package com.plantssoil.common.httpclient.impl;

import java.util.Map;

import com.plantssoil.common.security.Sha512HmacEncrypter;

/**
 * Will create the signature from (requestId, timestamp, payload) and call
 * remote url along with the signature
 * 
 * @author danialdy
 * @Date 26 Oct 2024 10:01:18 am
 */
public class SignaturedHttpPoster extends AbstractHttpPoster {
    @Override
    protected void beforePost(String url, Map<String, String> headers, String requestId, String payload) {
        super.beforePost(url, headers, requestId, payload);
        String timestamp = String.valueOf(System.currentTimeMillis());
        headers.put(HEADER_WEBHOOK_ID, requestId);
        headers.put(HEADER_WEBHOOK_TIMESTAMP, timestamp);
        headers.put(HEADER_WEBHOOK_SIGNATURE, signature(requestId, timestamp, payload));
    }

    private String signature(String messageId, String timestamp, String payload) {
        String toSign = String.format("%s.%s.%s", messageId, timestamp, payload);
        String signature = String.format("v1,%s", Sha512HmacEncrypter.getInstance().encrypt(getAccessToken(), toSign));
        return signature;
    }
}
