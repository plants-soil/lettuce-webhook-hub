package com.plantssoil.common.httpclient.impl;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.plantssoil.common.httpclient.IHttpCallback;
import com.plantssoil.common.httpclient.IHttpResponse;

public class JdkHttpClientTest {
    private JdkHttpClientAdaptor httpClient = new JdkHttpClientAdaptor().maxIdleConnections(7).maxRequestsPerHost(15);

    public static void main(String[] args) throws InterruptedException {
        JdkHttpClientTest test = new JdkHttpClientTest();
        test.testSend();
        Thread.sleep(100000);
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSendAsync() {
        fail("Not yet implemented");
    }

    @Test
    private void testSend() {
        // use https://webhook.site as the testing client, could go to
        // https://webhook.site to check and replace it below
        postRequests("http://dev.e-yunyi.com:8080/webhook/test", 1000);
//        postRequests("https://webhook.site/6d51e002-03e5-423a-87e6-97ef83bc6f91", 5);
        try {
            Thread.sleep(50 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        postRequests("http://dev.e-yunyi.com:8080/webhook/test", 30);
    }

    private void postRequests(String url, int count) {
        Map<String, String> headers = createTestHeaders();
//        String messageId = "msg_p5jXN8AQM9LWM0D4loKWxJek";
        long currentMillisecs = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            String payload = String.format("{\"test\": \"%d-%d\"}", currentMillisecs, i);

            // build http request with POST method
            java.net.http.HttpRequest.Builder requestBuilder = java.net.http.HttpRequest.newBuilder(java.net.URI.create(url)).timeout(Duration.ofSeconds(10))
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(payload));
            for (Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.header(entry.getKey(), entry.getValue());
            }

            this.httpClient.sendAsync(requestBuilder.build(), new IHttpCallback() {
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
            try {
                Thread.sleep(10);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    private Map<String, String> createTestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-webhook-fixedkey01", "X-webhook-value01");
        headers.put("X-webhook-fixedkey02", "X-webhook-value02");
        return headers;
    }

}
