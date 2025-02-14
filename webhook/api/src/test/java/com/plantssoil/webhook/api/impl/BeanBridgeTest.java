package com.plantssoil.webhook.api.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.rdbms.JPAPersistenceFactory;
import com.plantssoil.common.test.TempDirectoryUtility;
import com.plantssoil.webhook.core.IEvent.EventStatus;
import com.plantssoil.webhook.core.IOrganization.OrganizationStatus;
import com.plantssoil.webhook.core.IWebhook.SecurityStrategy;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;
import com.plantssoil.webhook.core.IWebhookLogLine;
import com.plantssoil.webhook.core.impl.SingleMessageQueueEngineFactory;
import com.plantssoil.webhook.core.logging.InMemoryWebhookLog;
import com.plantssoil.webhook.core.logging.InMemoryWebhookLogLine;
import com.plantssoil.webhook.core.registry.InMemoryDataGroup;
import com.plantssoil.webhook.core.registry.InMemoryEvent;
import com.plantssoil.webhook.core.registry.InMemoryOrganization;
import com.plantssoil.webhook.core.registry.InMemoryPublisher;
import com.plantssoil.webhook.core.registry.InMemoryRegistry;
import com.plantssoil.webhook.core.registry.InMemorySubscriber;
import com.plantssoil.webhook.core.registry.InMemoryWebhook;
import com.plantssoil.webhook.persists.beans.DataGroup;
import com.plantssoil.webhook.persists.beans.Event;
import com.plantssoil.webhook.persists.beans.Organization;
import com.plantssoil.webhook.persists.beans.Publisher;
import com.plantssoil.webhook.persists.beans.Subscriber;
import com.plantssoil.webhook.persists.beans.Webhook;
import com.plantssoil.webhook.persists.beans.WebhookLog;
import com.plantssoil.webhook.persists.beans.WebhookLogLine;
import com.plantssoil.webhook.resteasy.BeanBridge;

public class BeanBridgeTest {
    public static void main(String[] args) {
        BeanBridgeTest test = new BeanBridgeTest();
        test.test1BridgeBaseOnConfiguration();
        test.test2BridgeBaseOnConfiguration();
    }

    @Test
    public void testBridgeDataGroup() {
        try {
            BeanBridge.reload();
            ConfigFactory.reload();
            ConfigurableLoader.reload();
            InMemoryDataGroup idg = new InMemoryDataGroup();
            idg.setAccessToken("setAccessToken");
            idg.setDataGroup("setDataGroup");
            idg.setDataGroupId("setDataGroupId");
            idg.setRefreshToken("setRefreshToken");
            DataGroup dg = BeanBridge.getInstance().bridge(idg, DataGroup.class);
            assertEquals(dg.getAccessToken(), "setAccessToken");
            assertEquals(dg.getDataGroup(), "setDataGroup");
            assertEquals(dg.getDataGroupId(), "setDataGroupId");
            assertEquals(dg.getPublisherId(), null);
            assertEquals(dg.getRefreshToken(), "setRefreshToken");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBridgeEvent() {
        try {
            BeanBridge.reload();
            ConfigFactory.reload();
            ConfigurableLoader.reload();
            Date d = new Date();
            InMemoryEvent ie = new InMemoryEvent();
            ie.setCharset("setCharset");
            ie.setContentType("setContentType");
            ie.setCreatedBy("setCreatedBy");
            ie.setCreationTime(d);
            ie.setEventId("setEventId");
            ie.setEventStatus(EventStatus.SUBMITTED);
            ie.setEventTag("setEventTag");
            ie.setEventType("setEventType");
            Event e = BeanBridge.getInstance().bridge(ie, Event.class);
            assertEquals(e.getCharset(), "setCharset");
            assertEquals(e.getContentType(), "setContentType");
            assertEquals(e.getCreatedBy(), "setCreatedBy");
            assertEquals(e.getCreationTime(), d);
            assertEquals(e.getEventId(), "setEventId");
            assertEquals(e.getEventStatus(), EventStatus.SUBMITTED);
            assertEquals(e.getEventTag(), "setEventTag");
            assertEquals(e.getEventType(), "setEventType");
            assertEquals(e.getPublisherId(), null);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBridgeOrganization() {
        try {
            BeanBridge.reload();
            ConfigFactory.reload();
            ConfigurableLoader.reload();
            Date d = new Date();
            InMemoryOrganization io = new InMemoryOrganization();
            io.setCreatedBy("setCreatedBy");
            io.setCreationTime(d);
            io.setEmail("setEmail");
            io.setLogoLink("setLogoLink");
            io.setOrganizationId("setOrganizationId");
            io.setOrganizationName("setOrganizationName");
            io.setOrganizationStatus(OrganizationStatus.ACTIVE);
            io.setSecretKey("setSecretKey");
            io.setWebsite("setWebsite");
            Organization o = BeanBridge.getInstance().bridge(io, Organization.class);
            assertEquals(o.getCreatedBy(), "setCreatedBy");
            assertEquals(o.getCreationTime(), d);
            assertEquals(o.getEmail(), "setEmail");
            assertEquals(o.getLogoLink(), "setLogoLink");
            assertEquals(o.getOrganizationId(), "setOrganizationId");
            assertEquals(o.getOrganizationName(), "setOrganizationName");
            assertEquals(o.getOrganizationStatus(), OrganizationStatus.ACTIVE);
            assertEquals(o.getSecretKey(), "setSecretKey");
            assertEquals(o.getWebsite(), "setWebsite");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBridgePublisher() {
        try {
            BeanBridge.reload();
            ConfigFactory.reload();
            ConfigurableLoader.reload();
            Date d = new Date();
            InMemoryPublisher ip = new InMemoryPublisher();
            ip.setCreatedBy("setCreatedBy");
            ip.setCreationTime(d);
            ip.setOrganizationId("setOrganizationId");
            ip.setPublisherId("setPublisherId");
            ip.setSupportDataGroup(Boolean.FALSE);
            ip.setVersion("setVersion");
            Publisher p = BeanBridge.getInstance().bridge(ip, Publisher.class);
            assertEquals(p.getCreatedBy(), "setCreatedBy");
            assertEquals(p.getCreationTime(), d);
            assertEquals(p.getOrganizationId(), "setOrganizationId");
            assertEquals(p.getPublisherId(), "setPublisherId");
            assertEquals(p.getSupportDataGroup(), Boolean.FALSE);
            assertEquals(p.getVersion(), "setVersion");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBridgeSubscriber() {
        try {
            BeanBridge.reload();
            ConfigFactory.reload();
            ConfigurableLoader.reload();
            InMemorySubscriber is = new InMemorySubscriber();
            is.setOrganizationId("setOrganizationId");
            is.setSubscriberId("setSubscriberId");
            Subscriber s = BeanBridge.getInstance().bridge(is, Subscriber.class);
            assertEquals(s.getOrganizationId(), "setOrganizationId");
            assertEquals(s.getSubscriberId(), "setSubscriberId");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBridgeWebhook() {
        try {
            BeanBridge.reload();
            ConfigFactory.reload();
            ConfigurableLoader.reload();
            Date d = new Date();
            Map<String, String> h = new HashMap<>();
            h.put("a", "a1");
            h.put("b", "b1");
            String[] ips = new String[] { "192.168.0.1", "192.168.0.2" };
            InMemoryWebhook iw = new InMemoryWebhook();
            iw.setAccessToken("setAccessToken");
            iw.setAppName("setAppName");
            iw.setAppTag("setAppTag");
            iw.setCreatedBy("setCreatedBy");
            iw.setCreationTime(d);
            iw.setCustomizedHeaders(h);
            iw.setDescription("setDescription");
            iw.setPublisherId("setPublisherId");
            iw.setPublisherVersion("setPublisherVersion");
            iw.setRefreshToken("setRefreshToken");
            iw.setSecurityStrategy(SecurityStrategy.SIGNATURE);
            iw.setSubscriberId("setSubscriberId");
            iw.setTrustedIps(ips);
            iw.setWebhookId("setWebhookId");
            iw.setWebhookSecret("setWebhookSecret");
            iw.setWebhookStatus(WebhookStatus.PRODUCTION);
            iw.setWebhookUrl("setWebhookUrl");
            Webhook w = BeanBridge.getInstance().bridge(iw, Webhook.class);
            assertEquals(w.getAccessToken(), "setAccessToken");
            assertEquals(w.getAppName(), "setAppName");
            assertEquals(w.getAppTag(), "setAppTag");
            assertEquals(w.getCreatedBy(), "setCreatedBy");
            assertEquals(w.getCreationTime(), d);
            assertEquals(w.getCustomizedHeaders(), h);
            assertEquals(w.getDescription(), "setDescription");
            assertEquals(w.getPublisherId(), "setPublisherId");
            assertEquals(w.getPublisherVersion(), "setPublisherVersion");
            assertEquals(w.getRefreshToken(), "setRefreshToken");
            assertEquals(w.getSecurityStrategy(), SecurityStrategy.SIGNATURE);
            assertEquals(w.getSubscriberId(), "setSubscriberId");
            assertArrayEquals(w.getTrustedIps(), ips);
            assertEquals(w.getWebhookId(), "setWebhookId");
            assertEquals(w.getWebhookSecret(), "setWebhookSecret");
            assertEquals(w.getWebhookStatus(), WebhookStatus.PRODUCTION);
            assertEquals(w.getWebhookUrl(), "setWebhookUrl");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBridgeWebhookLog() {
        try {
            BeanBridge.reload();
            ConfigFactory.reload();
            ConfigurableLoader.reload();
            Date d = new Date();
            InMemoryWebhookLog il = new InMemoryWebhookLog();
            il.setCharset("setCharset");
            il.setContentType("setContentType");
            il.setDataGroup("setDataGroup");
            il.setEventTag("setEventTag");
            il.setEventType("setEventType");
            il.setPayload("setPayload");
            il.setPublisherId("setPublisherId");
            il.setRequestId("setRequestId");
            il.setTriggerTime(d);
            il.setVersion("setVersion");
            il.setWebhookStatus("setWebhookStatus");
            WebhookLog l = BeanBridge.getInstance().bridge(il, WebhookLog.class);
            assertEquals(l.getCharset(), "setCharset");
            assertEquals(l.getContentType(), "setContentType");
            assertEquals(l.getDataGroup(), "setDataGroup");
            assertEquals(l.getEventTag(), "setEventTag");
            assertEquals(l.getEventType(), "setEventType");
            assertEquals(l.getPayload(), "setPayload");
            assertEquals(l.getPublisherId(), "setPublisherId");
            assertEquals(l.getRequestId(), "setRequestId");
            assertEquals(l.getTriggerTime(), d);
            assertEquals(l.getVersion(), "setVersion");
            assertEquals(l.getWebhookStatus(), "setWebhookStatus");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBridgeWebhookLogLine() {
        try {
            BeanBridge.reload();
            ConfigFactory.reload();
            ConfigurableLoader.reload();
            Date d = new Date();
            InMemoryWebhookLogLine il = new InMemoryWebhookLogLine();
            il.setInformation("setInformation");
            il.setLogLineId("setLogLineId");
            il.setLogTime(d);
            il.setLogType("setLogType");
            il.setRequestId("setRequestId");
            il.setSubscriberId("setSubscriberId");
            il.setTryTime(3);
            il.setWebhookId("setWebhookId");
            WebhookLogLine l = BeanBridge.getInstance().bridge(il, WebhookLogLine.class);
            assertEquals(l.getInformation(), "setInformation");
            assertEquals(l.getLogLineId(), "setLogLineId");
            assertEquals(l.getLogTime(), d);
            assertEquals(l.getLogType(), "setLogType");
            assertEquals(l.getRequestId(), "setRequestId");
            assertEquals(l.getSubscriberId(), "setSubscriberId");
            assertEquals(l.getTryTime(), 3);
            assertEquals(l.getWebhookId(), "setWebhookId");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test1BridgeBaseOnConfiguration() {
        try {
            BeanBridge.reload();
            ConfigFactory.reload();
            ConfigurableLoader.reload();
            TempDirectoryUtility util = new TempDirectoryUtility();
            Properties p = new Properties();
            p.setProperty(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE, JPAPersistenceFactory.class.getName());
            p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_URL, "jdbc:h2:mem:testJdbc;DB_CLOSE_DELAY=-1");
            p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_USERNAME, "sa");
            p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_PASSWORD, "sa");
            p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_POOLSIZE, "22");
            p.setProperty(LettuceConfiguration.RDBMS_DATABASE_DRIVER, org.h2.Driver.class.getName());
            p.setProperty(LettuceConfiguration.RDBMS_DATABASE_SHOWSQL, "false");

            p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_FACTORY_CONFIGURABLE, com.plantssoil.common.mq.rabbit.MessageServiceFactory.class.getName());
            p.setProperty(LettuceConfiguration.MESSAGE_SERVICE_URI, "amqp://lettuce:lettuce20241101@192.168.0.67:5672/lettuce");
            p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_VERSION, "1.0.0");
            p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_FACTORY_CONFIGURABLE,
                    com.plantssoil.webhook.core.impl.SingleMessageQueueEngineFactory.class.getName());
            p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_REGISTRY_CONFIGURABLE,
                    com.plantssoil.webhook.persists.registry.PersistedRegistry.class.getName());
            try (FileOutputStream out = new FileOutputStream(util.getTempDir() + "/" + LettuceConfiguration.CONFIGURATION_FILE_NAME)) {
                p.store(out, "## No comments");
            }
            System.setProperty(LettuceConfiguration.CONF_DIRECTORY_PROPERTY_NAME, util.getTempDir());
            ConfigFactory.reload();
            ConfigurableLoader.reload();

            Date d = new Date();
            InMemoryWebhookLogLine il = new InMemoryWebhookLogLine();
            il.setInformation("setInformation");
            il.setLogLineId("setLogLineId");
            il.setLogTime(d);
            il.setLogType("setLogType");
            il.setRequestId("setRequestId");
            il.setSubscriberId("setSubscriberId");
            il.setTryTime(3);
            il.setWebhookId("setWebhookId");
            IWebhookLogLine l = (IWebhookLogLine) BeanBridge.getInstance().bridge(il);
            assertEquals(l.getClass(), WebhookLogLine.class);
            assertEquals(l.getLogTime(), d);
            assertEquals(l.getLogType(), "setLogType");
            assertEquals(l.getTryTime(), 3);
            util.removeTempDirectory();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test2BridgeBaseOnConfiguration() {
        try {
            BeanBridge.reload();
            TempDirectoryUtility util = new TempDirectoryUtility();
            Properties p = new Properties();
            p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_VERSION, "1.0.0");
            p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_FACTORY_CONFIGURABLE, SingleMessageQueueEngineFactory.class.getName());
            p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_REGISTRY_CONFIGURABLE, InMemoryRegistry.class.getName());
            try (FileOutputStream out = new FileOutputStream(util.getTempDir() + "/" + LettuceConfiguration.CONFIGURATION_FILE_NAME)) {
                p.store(out, "## No comments");
            }
            System.setProperty(LettuceConfiguration.CONF_DIRECTORY_PROPERTY_NAME, util.getTempDir());
            ConfigFactory.reload();
            ConfigurableLoader.reload();

            Date d = new Date();
            InMemoryWebhookLogLine il = new InMemoryWebhookLogLine();
            il.setInformation("setInformation");
            il.setLogLineId("setLogLineId");
            il.setLogTime(d);
            il.setLogType("setLogType");
            il.setRequestId("setRequestId");
            il.setSubscriberId("setSubscriberId");
            il.setTryTime(3);
            il.setWebhookId("setWebhookId");
            IWebhookLogLine l = (IWebhookLogLine) BeanBridge.getInstance().bridge(il);
            assertEquals(l.getClass(), InMemoryWebhookLogLine.class);
            assertEquals(l.getLogTime(), d);
            assertEquals(l.getLogType(), "setLogType");
            assertEquals(l.getTryTime(), 3);
            util.removeTempDirectory();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
