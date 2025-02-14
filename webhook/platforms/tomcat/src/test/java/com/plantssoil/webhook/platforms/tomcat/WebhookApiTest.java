package com.plantssoil.webhook.platforms.tomcat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WebhookApiTest {
    private OkHttpClient httpClient = new OkHttpClient();
    private String host = "http://localhost:8080/lettuce-webhook-platforms-tomcat/v1";
    private ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws JsonMappingException, JsonProcessingException, InterruptedException {
        WebhookApiTest test = new WebhookApiTest();
        test.test01OrganizationPost();
        test.test02OrganizationAll();
        test.test03OrganizationId();
        test.test04OrganizationUpdate();
        test.test05PublisherPost();
        test.test06PublisherAll();
        test.test07PublisherId();
        test.test08PublisherUpdate();
        test.test09PublisherEventPost();
        test.test10PublisherEventsPost();
        test.test11PublisherEventAll();
        test.test12PublisherEventId();
        test.test13PublisherDataGroupPost();
        test.test14PublisherDataGroupsPost();
        test.test15PublisherDataGroupName();
        test.test16PublisherDataGroupAll();
        test.test17PublisherDataGroupId();
        test.test18SubscriberPost();
        test.test19SubscriberAll();
        test.test20SubscriberId();
        test.test21SubscriberUpdate();
        test.test22SubscriberDelete();
        test.test23WebhookPost();
        test.test24WebhookAll();
        test.test25WebhookId();
        test.test26WebhookUpdate();
        test.test27WebhookDeactivate();
        test.test28WebhookActivate();
        test.test29WebhookSubscribeEvent();
        test.test30WebhookUnsubscribeEvent();
        test.test31WebhookSubscribeEvents();
        test.test32WebhookUnsubscribeEvents();
        test.test33WebhookSubscribeDataGroup();
        test.test34WebhookUnsubscribeDataGroup();
        test.test35WebhookSubscribeDataGroups();
        test.test36WebhookUnsubscribeDataGroups();
        test.test37WebhookAllSubscribedEvents();
        test.test38WebhookAllSubscribedDataGroups();
        test.test39WebhookSubscribedDataGroup();
        test.test40EngineVersion();
        test.test41EngineTrigger();
        test.test42EngineWebhookLogsPublisher();
        test.test43EngineWebhookLogsSubscriber();
        test.test44EngineWebhookLogLines();
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    private Object getResponseData(Request request) {
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String message = new String(response.body().bytes(), "utf-8");
                System.out.println(message);
                @SuppressWarnings("rawtypes")
                Map map = this.objectMapper.readValue(message, HashMap.class);
                Object obj = map.get("code");
                if (obj != null && Objects.equals("200", obj.toString())) {
                    return map.get("data");
                } else {
                    return map.get("message").toString();
                }
            } else {
                fail(response.message());
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test01OrganizationPost() throws JsonMappingException, JsonProcessingException {
        String url = this.host + "/organization";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "    \"organizationId\": \"67a9d57c58c2a00816841bd5\",\r\n" + "    \"organizationName\": \"Organiztion name\",\r\n"
                + "    \"email\": \"test@email.com\",\r\n" + "    \"website\": \"https://test.domain.com\",\r\n"
                + "    \"logoLink\": \"https://test.domain.com/images/logo.png\",\r\n" + "    \"organizationStatus\": \"ACTIVE\",\r\n"
                + "    \"createdBy\": \"danialdy\",\r\n" + "    \"creationTime\": \"2025-01-23T14:56:07.000Z\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map) data).get("organizationName"), "Organiztion name");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @Test
    public void test02OrganizationAll() {
        String url = host + "/organization/all?page=0&pageSize=20";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) ((List) data).get(0);
                assertEquals(m.get("organizationId"), "67a9d57c58c2a00816841bd5");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test03OrganizationId() {
        String url = host + "/organization/67a9d57c58c2a00816841bd5";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) data;
                assertEquals(m.get("organizationId"), "67a9d57c58c2a00816841bd5");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test04OrganizationUpdate() {
        String url = this.host + "/organization/67a9d57c58c2a00816841bd5";
        StringBuilder requestBody = new StringBuilder();
        requestBody
                .append("{\r\n" + "    \"organizationId\": \"67a9d57c58c2a00816841bd5\",\r\n" + "    \"organizationName\": \"Organiztion name (updated)\",\r\n"
                        + "    \"email\": \"test@email.com\",\r\n" + "    \"website\": \"https://test.domain.com\",\r\n"
                        + "    \"logoLink\": \"https://test.domain.com/images/logo.png\",\r\n" + "    \"organizationStatus\": \"ACTIVE\",\r\n"
                        + "    \"createdBy\": \"danialdy\",\r\n" + "    \"creationTime\": \"2025-01-23T14:56:07.000Z\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).put(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map) data).get("organizationName"), "Organiztion name (updated)");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @Test
    public void test05PublisherPost() {
        String url = this.host + "/publisher";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "  \"publisherId\": \"67a9d85558c2a00816841bd6\",\r\n" + "  \"organizationId\": \"67a9d57c58c2a00816841bd5\",\r\n"
                + "  \"supportDataGroup\": true,\r\n" + "  \"version\": \"1.0.0\",\r\n" + "  \"createdBy\": \"danialdy\",\r\n"
                + "  \"creationTime\": \"2025-01-23T14:56:07.000Z\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map<?, ?>) data).get("createdBy"), "danialdy");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @Test
    public void test06PublisherAll() {
        String url = host + "/publisher/all?page=0&pageSize=20";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) ((List) data).get(0);
                assertEquals(m.get("createdBy"), "danialdy");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test07PublisherId() {
        String url = host + "/publisher/67a9d85558c2a00816841bd6";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) data;
                assertEquals(m.get("createdBy"), "danialdy");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test08PublisherUpdate() {
        String url = this.host + "/publisher/67a9d85558c2a00816841bd6";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "  \"createdBy\": \"danialdy1(updated)\",\r\n" + "  \"creationTime\": \"2025-02-08T14:56:07.000Z\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).put(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map) data).get("createdBy"), "danialdy1(updated)");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void test09PublisherEventPost() {
        String url = this.host + "/publisher/67a9d85558c2a00816841bd6/event";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "    \"eventId\": \"67a9e08d58c2a00816841bd7\",\r\n" + "    \"eventType\": \"order.created\",\r\n"
                + "    \"eventTag\": \"order\",\r\n" + "    \"contentType\": \"application/json\",\r\n" + "    \"charset\": \"utf-8\",\r\n"
                + "    \"eventStatus\": \"PUBLISHED\",\r\n" + "    \"createdBy\": \"danialdy\",\r\n" + "    \"creationTime\": \"2025-02-10T10:10:10.000Z\"\r\n"
                + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map) data).get("eventType"), "order.created");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @Test
    public void test10PublisherEventsPost() {
        String url = this.host + "/publisher/67a9d85558c2a00816841bd6/events";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("[\r\n" + "    {\r\n" + "        \"eventId\": \"67a9e17758c2a00816841bd8\",\r\n" + "        \"eventType\": \"order.paid\",\r\n"
                + "        \"eventTag\": \"order\",\r\n" + "        \"contentType\": \"application/json\",\r\n" + "        \"charset\": \"utf-8\",\r\n"
                + "        \"eventStatus\": \"PUBLISHED\",\r\n" + "        \"createdBy\": \"danialdy\",\r\n"
                + "        \"creationTime\": \"2025-02-10T10:10:10.000Z\"\r\n" + "    },\r\n" + "    {\r\n"
                + "        \"eventId\": \"67a9e17758c2a00816841bd9\",\r\n" + "        \"eventType\": \"order.delivered\",\r\n"
                + "        \"eventTag\": \"order\",\r\n" + "        \"contentType\": \"application/json\",\r\n" + "        \"charset\": \"utf-8\",\r\n"
                + "        \"eventStatus\": \"PUBLISHED\",\r\n" + "        \"createdBy\": \"danialdy\",\r\n"
                + "        \"creationTime\": \"2025-02-10T10:10:10.000Z\"\r\n" + "    }\r\n" + "]");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) ((List) data).get(0);
                assertTrue(Objects.equals(m.get("eventType"), "order.paid") || Objects.equals(m.get("eventType"), "order.delivered"));
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test11PublisherEventAll() {
        String url = this.host + "/publisher/67a9d85558c2a00816841bd6/allEvents?page=0&pageSize=20";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) ((List) data).get(0);
                assertTrue(Objects.equals(m.get("eventType"), "order.created") || Objects.equals(m.get("eventType"), "order.paid")
                        || Objects.equals(m.get("eventType"), "order.delivered"));
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test12PublisherEventId() {
        String url = host + "/publisher/event/67a9e08d58c2a00816841bd7";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) data;
                assertEquals(m.get("eventType"), "order.created");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test13PublisherDataGroupPost() {
        String url = this.host + "/publisher/67a9d85558c2a00816841bd6/datagroup";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "  \"dataGroupId\": \"67a9e3bd58c2a00816841bdb\",\r\n" + "  \"dataGroup\": \"6791d42ed3d172552ed09459\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map<?, ?>) data).get("dataGroup"), "6791d42ed3d172552ed09459");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @Test
    public void test14PublisherDataGroupsPost() {
        String url = this.host + "/publisher/67a9d85558c2a00816841bd6/datagroups";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("[\r\n" + "    {\r\n" + "        \"dataGroupId\": \"67a9e4f458c2a00816841bdc\",\r\n"
                + "        \"dataGroup\": \"6791d42ed3d172552ed09466\"\r\n" + "    },\r\n" + "    {\r\n"
                + "        \"dataGroupId\": \"67a9e4f458c2a00816841bdd\",\r\n" + "        \"dataGroup\": \"6791d42ed3d172552ed09488\"\r\n" + "    }\r\n" + "]");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) ((List) data).get(0);
                assertTrue(Objects.equals(m.get("dataGroup"), "6791d42ed3d172552ed09466") || Objects.equals(m.get("dataGroup"), "6791d42ed3d172552ed09488"));
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test15PublisherDataGroupName() {
        String url = host + "/publisher/67a9d85558c2a00816841bd6/datagroup?dataGroup=6791d42ed3d172552ed09459";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) data;
                assertEquals(m.get("dataGroupId"), "67a9e3bd58c2a00816841bdb");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test16PublisherDataGroupAll() {
        String url = this.host + "/publisher/67a9d85558c2a00816841bd6/allDataGroups?page=0&pageSize=20";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) ((List) data).get(0);
                assertTrue(Objects.equals(m.get("dataGroup"), "6791d42ed3d172552ed09459") || Objects.equals(m.get("dataGroup"), "6791d42ed3d172552ed09466")
                        || Objects.equals(m.get("dataGroup"), "6791d42ed3d172552ed09488"));
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test17PublisherDataGroupId() {
        String url = host + "/publisher/datagroup/67a9e3bd58c2a00816841bdb";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) data;
                assertEquals(m.get("dataGroup"), "6791d42ed3d172552ed09459");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test18SubscriberPost() {
        String url = this.host + "/subscriber";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "  \"subscriberId\": \"67a9e63f58c2a00816841bde\",\r\n" + "  \"organizationId\": \"67a9d57c58c2a00816841bd5\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map<?, ?>) data).get("organizationId"), "67a9d57c58c2a00816841bd5");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @Test
    public void test19SubscriberAll() {
        String url = host + "/subscriber/all?page=0&pageSize=20";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) ((List) data).get(0);
                assertEquals(m.get("organizationId"), "67a9d57c58c2a00816841bd5");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test20SubscriberId() {
        String url = host + "/subscriber/67a9e63f58c2a00816841bde";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) data;
                assertEquals(m.get("organizationId"), "67a9d57c58c2a00816841bd5");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test21SubscriberUpdate() {
        String url = this.host + "/subscriber/67a9e63f58c2a00816841bde";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "  \"subscriberId\": \"67a9e63f58c2a00816841bde\",\r\n" + "  \"organizationId\": \"67a9d57c58c2a00816841bd5\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).put(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map<?, ?>) data).get("organizationId"), "67a9d57c58c2a00816841bd5");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @Test
    public void test22SubscriberDelete() {
        String url = this.host + "/subscriber/67a9e63f58c2a00816841bde";
        Request request = new Request.Builder().url(url).delete().build();
        Object data = getResponseData(request);
        if (data == null || Objects.equals("OK", data)) {
            assertTrue(true);
        }
        test18SubscriberPost();
    }

    @Test
    public void test23WebhookPost() {
        String url = this.host + "/webhook";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "    \"webhookId\": \"67a9e8e558c2a00816841bdf\",\r\n" + "    \"subscriberId\": \"67a9e63f58c2a00816841bde\",\r\n"
                + "    \"appName\": \"Order Management System\",\r\n" + "    \"appTag\": \"E-Commerce\",\r\n"
                + "    \"description\": \"The system for merchants to manage customer orders\",\r\n" + "    \"webhookSecret\": \"string\",\r\n"
                + "    \"publisherId\": \"67a9d85558c2a00816841bd6\",\r\n" + "    \"publisherVersion\": \"1.0.0\",\r\n"
                + "    \"securityStrategy\": \"NONE\",\r\n" + "    \"webhookUrl\": \"http://dev.e-yunyi.com:8080/webhook/test\",\r\n"
                + "    \"customizedHeaders\": {\r\n" + "        \"Authorization\": \"Basic YXBpdXNlcjE6NjY2NjY2NjY2Ng==\",\r\n"
                + "        \"key1\": \"value1\",\r\n" + "        \"key2\": \"value2\",\r\n" + "        \"key3\": \"value3\"\r\n" + "    },\r\n"
                + "    \"trustedIps\": [\r\n" + "        \"192.168.0.1\",\r\n" + "        \"192.168.0.2\",\r\n" + "        \"192.168.0.3\"\r\n" + "    ],\r\n"
                + "    \"webhookStatus\": \"TEST\",\r\n" + "    \"createdBy\": \"danialdy\",\r\n" + "    \"creationTime\": \"2025-01-23T14:56:07.000Z\"\r\n"
                + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map<?, ?>) data).get("appName"), "Order Management System");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @Test
    public void test24WebhookAll() {
        String url = host + "/webhook/all?subscriberId=67a9e63f58c2a00816841bde&page=0&pageSize=20";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) ((List) data).get(0);
                assertEquals(m.get("appName"), "Order Management System");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test25WebhookId() {
        String url = host + "/webhook/67a9e8e558c2a00816841bdf";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) data;
                assertEquals(m.get("appName"), "Order Management System");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test26WebhookUpdate() {
        String url = this.host + "/webhook/67a9e8e558c2a00816841bdf";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "    \"appName\": \"Order Management System (updated)\",\r\n" + "    \"appTag\": \"E-Commerce\",\r\n"
                + "    \"description\": \"The system for merchants to manage customer orders\",\r\n" + "    \"webhookSecret\": \"string\",\r\n"
                + "    \"securityStrategy\": \"NONE\",\r\n" + "    \"webhookUrl\": \"http://dev.e-yunyi.com:8080/webhook/test\",\r\n"
                + "    \"customizedHeaders\": {\r\n" + "        \"Authorization\": \"Basic YXBpdXNlcjE6NjY2NjY2NjY2Ng==\",\r\n"
                + "        \"key1\": \"value1\",\r\n" + "        \"key2\": \"value2\"\r\n" + "    },\r\n" + "    \"trustedIps\": [\r\n"
                + "        \"192.168.0.1\",\r\n" + "        \"192.168.0.2\"\r\n" + "    ],\r\n" + "    \"webhookStatus\": \"TEST\",\r\n"
                + "    \"createdBy\": \"danialdy\",\r\n" + "    \"creationTime\": \"2025-01-23T14:56:07.000Z\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).put(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map<?, ?>) data).get("appName"), "Order Management System (updated)");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @Test
    public void test27WebhookDeactivate() {
        String url = this.host + "/webhook/deactivate?webhookId=67a9e8e558c2a00816841bdf";
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), "");
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map<?, ?>) data).get("webhookStatus"), "INACTIVE");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @Test
    public void test28WebhookActivate() {
        String url = this.host + "/webhook/activate?webhookId=67a9e8e558c2a00816841bdf";
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), "");
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                assertEquals(((Map<?, ?>) data).get("webhookStatus"), "PRODUCTION");
            } else {
                fail("Response data is not Map type!");
            }
        }
    }

    @Test
    public void test29WebhookSubscribeEvent() {
        String url = this.host + "/webhook/subscribeEvent";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "    \"webhookId\": \"67a9e8e558c2a00816841bdf\",\r\n" + "    \"eventId\": \"67a9e08d58c2a00816841bd7\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data == null || Objects.equals("OK", data)) {
            assertTrue(true);
        } else {
            System.out.println("Response data is: " + data);
            fail("Response data is not correct!");
        }
    }

    @Test
    public void test30WebhookUnsubscribeEvent() {
        String url = this.host + "/webhook/unsubscribeEvent";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "    \"webhookId\": \"67a9e8e558c2a00816841bdf\",\r\n" + "    \"eventId\": \"67a9e08d58c2a00816841bd7\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data == null || Objects.equals("OK", data)) {
            assertTrue(true);
        } else {
            System.out.println("Response data is: " + data);
            fail("Response data is not correct!");
        }
    }

    @Test
    public void test31WebhookSubscribeEvents() {
        String url = this.host + "/webhook/subscribeEvents";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append(
                "{\r\n" + "    \"webhookId\": \"67a9e8e558c2a00816841bdf\",\r\n" + "    \"eventIds\": [\r\n" + "        \"67a9e08d58c2a00816841bd7\",\r\n"
                        + "        \"67a9e17758c2a00816841bd8\",\r\n" + "        \"67a9e17758c2a00816841bd9\"\r\n" + "    ]\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data == null || Objects.equals("OK", data)) {
            assertTrue(true);
        } else {
            System.out.println("Response data is: " + data);
            fail("Response data is not correct!");
        }
    }

    @Test
    public void test32WebhookUnsubscribeEvents() {
        String url = this.host + "/webhook/unsubscribeEvents";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "    \"webhookId\": \"67a9e8e558c2a00816841bdf\",\r\n" + "    \"eventIds\": [\r\n"
                + "        \"67a9e17758c2a00816841bd8\",\r\n" + "        \"67a9e17758c2a00816841bd9\"\r\n" + "    ]\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data == null || Objects.equals("OK", data)) {
            assertTrue(true);
        } else {
            System.out.println("Response data is: " + data);
            fail("Response data is not correct!");
        }
    }

    @Test
    public void test33WebhookSubscribeDataGroup() {
        String url = this.host + "/webhook/subscribeDataGroup";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "    \"webhookId\": \"67a9e8e558c2a00816841bdf\",\r\n" + "    \"dataGroupId\": \"67a9e3bd58c2a00816841bdb\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data == null || Objects.equals("OK", data)) {
            assertTrue(true);
        } else {
            System.out.println("Response data is: " + data);
            fail("Response data is not correct!");
        }
    }

    @Test
    public void test34WebhookUnsubscribeDataGroup() {
        String url = this.host + "/webhook/unsubscribeDataGroup";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "    \"webhookId\": \"67a9e8e558c2a00816841bdf\",\r\n" + "    \"dataGroupId\": \"67a9e3bd58c2a00816841bdb\"\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data == null || Objects.equals("OK", data)) {
            assertTrue(true);
        } else {
            System.out.println("Response data is: " + data);
            fail("Response data is not correct!");
        }
    }

    @Test
    public void test35WebhookSubscribeDataGroups() {
        String url = this.host + "/webhook/subscribeDataGroups";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append(
                "{\r\n" + "    \"webhookId\": \"67a9e8e558c2a00816841bdf\",\r\n" + "    \"dataGroupIds\": [\r\n" + "        \"67a9e3bd58c2a00816841bdb\",\r\n"
                        + "        \"67a9e4f458c2a00816841bdc\",\r\n" + "        \"67a9e4f458c2a00816841bdd\"\r\n" + "    ]\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data == null || Objects.equals("OK", data)) {
            assertTrue(true);
        } else {
            System.out.println("Response data is: " + data);
            fail("Response data is not correct!");
        }
    }

    @Test
    public void test36WebhookUnsubscribeDataGroups() {
        String url = this.host + "/webhook/unsubscribeDataGroups";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "    \"webhookId\": \"67a9e8e558c2a00816841bdf\",\r\n" + "    \"dataGroupIds\": [\r\n"
                + "        \"67a9e4f458c2a00816841bdc\",\r\n" + "        \"67a9e4f458c2a00816841bdd\"\r\n" + "    ]\r\n" + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data == null || Objects.equals("OK", data)) {
            assertTrue(true);
        } else {
            System.out.println("Response data is: " + data);
            fail("Response data is not correct!");
        }
    }

    @Test
    public void test37WebhookAllSubscribedEvents() {
        String url = this.host + "/webhook/allSubscribedEvents?webhookId=67a9e8e558c2a00816841bdf&page=0&pageSize=20";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) ((List) data).get(0);
                assertTrue(Objects.equals(m.get("eventType"), "order.created"));
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test38WebhookAllSubscribedDataGroups() {
        String url = this.host + "/webhook/allSubscribedDataGroups?webhookId=67a9e8e558c2a00816841bdf&page=0&pageSize=20";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) ((List) data).get(0);
                assertTrue(Objects.equals(m.get("dataGroup"), "6791d42ed3d172552ed09459"));
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test39WebhookSubscribedDataGroup() {
        String url = host + "/webhook/subscribedDataGroup?webhookId=67a9e8e558c2a00816841bdf&dataGroup=6791d42ed3d172552ed09459";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof Map) {
                @SuppressWarnings("rawtypes")
                Map m = (Map) data;
                assertEquals(m.get("dataGroupId"), "67a9e3bd58c2a00816841bdb");
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test40EngineVersion() {
        String url = host + "/engine/version";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data == null || Objects.equals("OK", data)) {
            assertTrue(true);
        } else {
            System.out.println("Response data is: " + data);
            fail("Response data is not correct!");
        }
    }

    @Test
    public void test41EngineTrigger() {
        String url = this.host + "/engine/trigger";
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{\r\n" + "    \"publisherId\": \"67a9d85558c2a00816841bd6\",\r\n" + "    \"version\": \"1.0.0\",\r\n"
                + "    \"eventType\": \"order.created\",\r\n" + "    \"eventTag\": \"order\",\r\n" + "    \"contentType\": \"application/json\",\r\n"
                + "    \"charset\": \"utf-8\",\r\n" + "    \"dataGroup\": \"6791d42ed3d172552ed09459\",\r\n"
                + "    \"requestId\": \"1001d42ed3d172552ed00003\",\r\n"
                + "    \"payload\": \"{\\\"orderId\\\": \\\"1\\\",\\\"orderTime\\\": \\\"2025-01-23T14:56:07.000Z\\\", \\\"customerId\\\": \\\"6791d42ed3d172552ed08888\\\", \\\"totalAmount\\\": 120, \\\"orderLines\\\": [{\\\"rowno\\\": 1, \\\"sku\\\": \\\"100001\\\", \\\"quantity\\\": 1, \\\"unitPrice\\\": 40}, {\\\"rowno\\\": 1, \\\"sku\\\": \\\"100002\\\", \\\"quantity\\\": 1, \\\"unitPrice\\\": 80}]}\"\r\n"
                + "}");
        RequestBody rb = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), requestBody.toString());
        Request request = new Request.Builder().url(url).post(rb).build();
        Object data = getResponseData(request);
        if (data == null || Objects.equals("OK", data)) {
            assertTrue(true);
        } else {
            System.out.println("Response data is: " + data);
            fail("Response data is not correct!");
        }
    }

    @Test
    public void test42EngineWebhookLogsPublisher() throws InterruptedException {
        Thread.sleep(1000);
        String url = this.host + "/engine/allPublisherWebhookLogs?publisherId=67a9d85558c2a00816841bd6&dataGroup=6791d42ed3d172552ed09459&page=0&pageSize=20";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                List l = (List) data;
                assertTrue(l.size() > 0);
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test43EngineWebhookLogsSubscriber() throws InterruptedException {
        Thread.sleep(1000);
        String url = this.host + "/engine/allSubscriberWebhookLogs?webhookId=67a9e8e558c2a00816841bdf&page=0&pageSize=20";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                List l = (List) data;
                assertTrue(l.size() > 0);
            } else {
                fail("Response data is not List type!");
            }
        }
    }

    @Test
    public void test44EngineWebhookLogLines() throws InterruptedException {
        Thread.sleep(1000);
        String url = this.host + "/engine/webhookLines?webhookId=67a9e8e558c2a00816841bdf&requestId=1001d42ed3d172552ed00003";
        Request request = new Request.Builder().url(url).get().build();
        Object data = getResponseData(request);
        if (data != null) {
            if (data instanceof List) {
                @SuppressWarnings("rawtypes")
                List l = (List) data;
                assertTrue(l.size() > 0);
            } else {
                fail("Response data is not List type!");
            }
        }
    }
}
