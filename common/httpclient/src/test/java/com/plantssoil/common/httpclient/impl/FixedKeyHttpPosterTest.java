package com.plantssoil.common.httpclient.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.plantssoil.common.httpclient.IHttpCallback;
import com.plantssoil.common.httpclient.IHttpResponse;

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
        String url = "https://webhook.site/6d51e002-03e5-423a-87e6-97ef83bc6f91";

        Map<String, String> headers = createTestHeaders();
        String messageId = "msg_p5jXN8AQM9LWM0D4loKWxJek";
        String payload = "{\"test\": 2432232314}";

        FixedKeyHttpPoster notifier = new FixedKeyHttpPoster();
        notifier.setAccessToken("whsec_MfKQ9r8GKYqrTwjUPD8ILPZIo2LaLaSw");
        notifier.setCharset("UTF-8");
        notifier.setMediaType("application/json");

        IHttpResponse response = notifier.post(url, headers, messageId, payload);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
        try {
            // should uncomment this line after setup the correct url
//            assertTrue(future.get().statusCode() == 200);
            assertTrue(response.getStatusCode() == 404);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
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
