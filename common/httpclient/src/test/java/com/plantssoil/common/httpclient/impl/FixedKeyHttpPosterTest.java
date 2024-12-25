package com.plantssoil.common.httpclient.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.plantssoil.common.httpclient.IHttpCallback;
import com.plantssoil.common.httpclient.IHttpResponse;
import com.plantssoil.common.httpclient.exception.HttpClientException;

public class FixedKeyHttpPosterTest {
    public static void main(String[] args) throws InterruptedException {
        FixedKeyHttpPosterTest test = new FixedKeyHttpPosterTest();
        for (int i = 0; i < 1000; i++) {
            test.test02AsyncPostNotification();
        }
//        for (int i = 0; i < 20; i++) {
//            test.test01SyncPostNotification();
//        }
        Thread.sleep(100000);
    }

    @Test
    public void test01SyncPostNotification() {
        // use https://webhook.site as the testing client, could go to
        // https://webhook.site to check and replace it below
        String url = "https://webhook.site/d491e316-8fb3-498c-b7bb-2b3409827530";

        Map<String, String> headers = createTestHeaders();
        String messageId = "msg_p5jXN8AQM9LWM0D4loKWxJek";
        String payload = "{\"test\": 2432232314}";

        FixedKeyHttpPoster notifier = new FixedKeyHttpPoster();
        notifier.setAccessToken("whsec_MfKQ9r8GKYqrTwjUPD8ILPZIo2LaLaSw");
        notifier.setCharset("UTF-8");
        notifier.setMediaType("application/json");

        try {
            IHttpResponse response = notifier.post(url, headers, messageId, payload);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            // should uncomment this line after setup the correct url
//            assertTrue(future.get().statusCode() == 200);
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

    @Test
    public void test02AsyncPostNotification() {
        // use https://webhook.site as the testing client, could go to
        // https://webhook.site to check and replace it below
        String url = "http://dev.e-yunyi.com:8080/webhook/test";

        Map<String, String> headers = createTestHeaders();
        String messageId = "msg_p5jXN8AQM9LWM0D4loKWxJek";
        String payload = "{\"test\": 2432232314}";

        FixedKeyHttpPoster notifier = new FixedKeyHttpPoster();
        notifier.setAccessToken("whsec_MfKQ9r8GKYqrTwjUPD8ILPZIo2LaLaSw");
        notifier.setCharset("UTF-8");
        notifier.setMediaType("application/json");
        notifier.setMaxRequestsPerHost(10);

        notifier.post(url, headers, messageId, payload, new IHttpCallback() {
            @Override
            public void onFailure(Exception e) {
                System.out.println("Exception happened: " + e.getMessage());
            }

            @Override
            public void onResponse(IHttpResponse response) throws IOException {
                System.out.println(response.getStatusCode());
                System.out.println(response.getBody());
            }

        });
    }

    private Map<String, String> createTestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-webhook-fixedkey01", "X-webhook-value01");
        headers.put("X-webhook-fixedkey02", "X-webhook-value02");
        return headers;
    }

}
