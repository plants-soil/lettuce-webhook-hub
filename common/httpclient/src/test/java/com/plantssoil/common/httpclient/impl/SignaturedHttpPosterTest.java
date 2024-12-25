package com.plantssoil.common.httpclient.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.plantssoil.common.httpclient.IHttpResponse;
import com.plantssoil.common.httpclient.exception.HttpClientException;

public class SignaturedHttpPosterTest {

    @Test
    public void testPostNotification() {
        // use https://webhook.site as the testing client, could go to
        // https://webhook.site to check and replace it below
        String url = "https://webhook.site/d491e316-8fb3-498c-b7bb-2b3409827530";
        Map<String, String> headers = createTestHeaders();
        String messageId = "msg_p5jXN8AQM9LWM0D4loKWxJek";
        String payload = "{\"test\": 2432232314}";

        SignaturedHttpPoster notifier = new SignaturedHttpPoster();
        notifier.setAccessToken("MfKQ9r8GKYqrTwjUPD8ILPZIo2LaLaSw");
        notifier.setCharset("UTF-8");
        notifier.setMediaType("application/json");

        try {
            IHttpResponse response = notifier.post(url, headers, messageId, payload);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            // should uncomment this line after setup the correct url
//          assertTrue(future.get().statusCode() == 200);
            assertTrue(response.getStatusCode() == 404 || response.getStatusCode() == 429 || response.getStatusCode() == 200);
        } catch (Exception e) {
            if (e instanceof HttpClientException) {
                if (((HttpClientException) e).getCode() != 14004) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }
            } else {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }

    private Map<String, String> createTestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-webhook-signatured01", "X-webhook-value01");
        headers.put("X-webhook-signatured02", "X-webhook-value02");
        return headers;
    }

}
