package com.plantssoil.webhook.core.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.test.TempDirectoryUtility;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEngine;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IOrganization;
import com.plantssoil.webhook.core.IPublisher;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.ISubscriber;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.IWebhook.SecurityStrategy;
import com.plantssoil.webhook.core.IWebhook.WebhookStatus;
import com.plantssoil.webhook.core.Message;
import com.plantssoil.webhook.core.exception.EngineException;

import io.netty.util.internal.ThreadLocalRandom;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InMemoryRegistryTest {
    private static TempDirectoryUtility util = new TempDirectoryUtility();
    private final static long CURRENT_TIME_MILLIS = System.currentTimeMillis();
    private final static AtomicInteger SEQUENCE_VERSION = new AtomicInteger();
    private final static String WEBHOOK_URL = "http://dev.e-yunyi.com:8080/webhook/test";
    private final static String ORGANIZATION_PREFIX = "ORGANIZATION";
    private final static String PUBLISHER_PREFIX = "PUBLISHER";
    private final static String EVENT_PREFIX = "EVENT";
    private final static String DATAGROUP_PREFIX = "DATAGROUP";
    private final static String SUBSCRIBER_PREFIX = "SUBSCRIBER";
    private final static String WEBHOOK_PREFIX = "WEBHOOK";

    private Map<String, AtomicInteger> entitySequences = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        InMemoryRegistryTest test = new InMemoryRegistryTest();
        InMemoryRegistryTest.setUpBeforeClass();
        test.test01AddOrganization();
        test.test02UpdateOrganizationId();
        test.test03AddPublisher();
        test.test04UpdatePublisher();
        test.test05AddEventStringIEvent();
        test.test06AddDataGroupStringIDataGroup();
        test.test07AddSubscriber();
        test.test08UpdateSubscriber();
        test.test09RemoveSubscriber();
        test.test10AddWebhook();
        test.test11UpdateWebhook();
        test.test12ActivateWebhook();
        test.test13DeactivateWebhook();
        test.test14SubscribeEventIWebhookIEvent();
        test.test15UnsubscribeEventIWebhookIEvent();
        test.test16SubscribeDataGroupIWebhookIDataGroup();
        test.test17UnsubscribeDataGroupIWebhookIDataGroup();
        test.test18TriggerMessageWithoutDataGroup();
        System.out.println("completed.");
        InMemoryRegistryTest.tearDownAfterClass();
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Thread.sleep(1000);
        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_VERSION, "1.0.0");
        p.setProperty(LettuceConfiguration.WEBHOOK_ENGINE_FACTORY_CONFIGURABLE, com.plantssoil.webhook.core.impl.InMemoryEngineFactory.class.getName());

        try (FileOutputStream out = new FileOutputStream(util.getTempDir() + "/" + LettuceConfiguration.CONFIGURATION_FILE_NAME)) {
            p.store(out, "## No comments");
        }
        System.setProperty(LettuceConfiguration.CONF_DIRECTORY_PROPERTY_NAME, util.getTempDir());
        ConfigFactory.reload();
        ConfigurableLoader.reload();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        util.removeTempDirectory();
    }

    private String getEntityId(String prefix) {
        AtomicInteger ai = this.entitySequences.get(prefix);
        if (ai == null) {
            synchronized (prefix.intern()) {
                ai = new AtomicInteger();
                this.entitySequences.put(prefix, ai);
            }
        }
        return String.format("%s-%d-%d", prefix, CURRENT_TIME_MILLIS, ai.getAndIncrement());
    }

    @Test
    public void test01AddOrganization() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        // add 15 organizations, should success
        for (int i = 0; i < 15; i++) {
            InMemoryOrganization o = new InMemoryOrganization();
            String id = getEntityId(ORGANIZATION_PREFIX);
            o.setOrganizationId(id);
            o.setEmail(id + "@e-yunyi.com");
            r.addOrganization(o);
        }
        List<IOrganization> os = r.findAllOrganizations(0, 100);
        assertEquals(15, os.size());
        // should throw exception if missed required attributes
        assertThrows(EngineException.class, () -> {
            r.addOrganization(new InMemoryOrganization());
        });
        // should throw exception if add organization with existing organization id
        assertThrows(EngineException.class, () -> {
            InMemoryOrganization o = new InMemoryOrganization();
            o.setOrganizationId(os.get(ThreadLocalRandom.current().nextInt(14)).getOrganizationId());
            r.addOrganization(o);
        });
    }

    @Test
    public void test02UpdateOrganizationId() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        List<IOrganization> os = r.findAllOrganizations(0, 100);
        // update one organization without any change, should success
        r.updateOrganization(os.get(ThreadLocalRandom.current().nextInt(14)));
        // update one organization and change the original organization id, should throw
        // exception
        assertThrows(EngineException.class, () -> {
            IOrganization o1 = os.get(ThreadLocalRandom.current().nextInt(14));
            o1.setOrganizationId("changeorganizationid");
            r.updateOrganization(o1);
        });
    }

    private IOrganization getTestOrganization(IRegistry r) {
        return r.findOrganization(ORGANIZATION_PREFIX + "-" + CURRENT_TIME_MILLIS + "-" + "13");
    }

    @Test
    public void test03AddPublisher() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        IOrganization o = getTestOrganization(r);
        // add 10 publishers, should success
        for (int i = 0; i < 10; i++) {
            InMemoryPublisher p = new InMemoryPublisher();
            p.setOrganizationId(o.getOrganizationId());
            p.setPublisherId(getEntityId(PUBLISHER_PREFIX));
            p.setSupportDataGroup(i % 7 == 0 ? true : false);
            p.setVersion(SEQUENCE_VERSION.incrementAndGet() + ".0.0");
            r.addPublisher(p);
        }
        List<IPublisher> ps = r.findAllPublishers(0, 100);
        assertEquals(10, ps.size());
        // add publisher without required attributes, should throw exception
        assertThrows(EngineException.class, () -> {
            r.addPublisher(new InMemoryPublisher());
        });
        // add publisher with unknown organization id, should throw exception
        assertThrows(EngineException.class, () -> {
            InMemoryPublisher p = new InMemoryPublisher();
            p.setOrganizationId("the-organization-id-unknown");
            p.setPublisherId(getEntityId(PUBLISHER_PREFIX));
            p.setSupportDataGroup(true);
            p.setVersion(SEQUENCE_VERSION.incrementAndGet() + ".0.0");
            r.addPublisher(p);
        });
        // add publisher with existing publisher id, should throw exception
        assertThrows(EngineException.class, () -> {
            InMemoryPublisher p = new InMemoryPublisher();
            p.setOrganizationId(o.getOrganizationId());
            p.setPublisherId(ps.get(ThreadLocalRandom.current().nextInt(9)).getPublisherId());
            p.setSupportDataGroup(true);
            p.setVersion(SEQUENCE_VERSION.incrementAndGet() + ".0.0");
            r.addPublisher(p);
        });
        // add publisher with existing version, should throw exception
        assertThrows(EngineException.class, () -> {
            InMemoryPublisher p = new InMemoryPublisher();
            p.setOrganizationId(o.getOrganizationId());
            p.setPublisherId(getEntityId(PUBLISHER_PREFIX));
            p.setSupportDataGroup(true);
            p.setVersion(ps.get(ThreadLocalRandom.current().nextInt(9)).getVersion());
            r.addPublisher(p);
        });
    }

    @Test
    public void test04UpdatePublisher() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        List<IPublisher> ps = r.findAllPublishers(0, 100);
        // update publisher to unknown publisher id, should throw exception
        assertThrows(EngineException.class, () -> {
            IPublisher p = ps.get(5);
            p.setPublisherId("unknow-publisher-id");
            r.updatePublisher(p);
        });
        // update publisher attribute "support data group", should throw exception
        assertThrows(EngineException.class, () -> {
            IPublisher p = ps.get(4);
            p.setSupportDataGroup(p.getSupportDataGroup() ? false : true);
            r.updatePublisher(p);
        });
        // update publisher attribute "version", should throw exception
        assertThrows(EngineException.class, () -> {
            IPublisher p = ps.get(3);
            p.setVersion("-1.0.0");
            r.updatePublisher(p);
        });
        // update publisher attribute "organizationId", should throw exception
        assertThrows(EngineException.class, () -> {
            IPublisher p = ps.get(2);
            p.setOrganizationId("ORGANIZATION-" + CURRENT_TIME_MILLIS + "-0");
            r.updatePublisher(p);
        });
        // should success
        IPublisher p = ps.get(1);
        r.updatePublisher(p);
    }

    private IPublisher getTestPublisher(IRegistry r) {
        return r.findPublisher(PUBLISHER_PREFIX + "-" + CURRENT_TIME_MILLIS + "-" + "7");
    }

    @Test
    public void test05AddEventStringIEvent() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        IPublisher p = getTestPublisher(r);
        // add 9 events, should success
        String eventPrefix = "test.event.type.";
        List<IEvent> es = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            InMemoryEvent e = new InMemoryEvent();
            e.setEventId(getEntityId(EVENT_PREFIX));
            e.setCharset("UTF-8");
            e.setContentType("application/json");
            e.setEventTag("test");
            e.setEventType(eventPrefix + i);
            es.add(e);
        }
        r.addEvent(p.getPublisherId(), es);
        es = r.findEvents(p.getPublisherId(), 0, 100);
        assertEquals(9, es.size());
        // add event without required attributes, should throw exception
        assertThrows(EngineException.class, () -> {
            r.addEvent(p.getPublisherId(), new InMemoryEvent());
        });
        // add event with unknown publisher id, should throw exception
        assertThrows(EngineException.class, () -> {
            InMemoryEvent e = new InMemoryEvent();
            e.setEventId(getEntityId(EVENT_PREFIX));
            e.setCharset("UTF-8");
            e.setContentType("application/json");
            e.setEventTag("test");
            e.setEventType(eventPrefix + -100);
            r.addEvent("unknown-publisher-id", e);
        });
        // add event with existing event id, should throw exception
        String eid = es.get(ThreadLocalRandom.current().nextInt(8)).getEventId();
        assertThrows(EngineException.class, () -> {
            InMemoryEvent e = new InMemoryEvent();
            e.setEventId(eid);
            e.setCharset("UTF-8");
            e.setContentType("application/json");
            e.setEventTag("test");
            e.setEventType(eventPrefix + -100);
            r.addEvent(p.getPublisherId(), e);
        });
        // add event with existing event type, should throw exception
        String etype = es.get(ThreadLocalRandom.current().nextInt(8)).getEventType();
        assertThrows(EngineException.class, () -> {
            InMemoryEvent e = new InMemoryEvent();
            e.setEventId(getEntityId(EVENT_PREFIX));
            e.setCharset("UTF-8");
            e.setContentType("application/json");
            e.setEventTag("test");
            e.setEventType(etype);
            r.addEvent(p.getPublisherId(), e);
        });
        // add event with duplicated event type, should throw exception
        assertThrows(EngineException.class, () -> {
            List<IEvent> tmps = new ArrayList<>();
            InMemoryEvent e = new InMemoryEvent();
            e.setEventId(getEntityId(EVENT_PREFIX));
            e.setCharset("UTF-8");
            e.setContentType("application/json");
            e.setEventTag("test");
            e.setEventType(eventPrefix + "duplicated");
            tmps.add(e);
            e = new InMemoryEvent();
            e.setEventId(getEntityId(EVENT_PREFIX));
            e.setCharset("UTF-8");
            e.setContentType("application/json");
            e.setEventTag("test");
            e.setEventType(eventPrefix + "duplicated");
            tmps.add(e);
            r.addEvent(p.getPublisherId(), tmps);
        });
    }

    private IEvent getTestEvent(IRegistry r) {
        return r.findEvent(EVENT_PREFIX + "-" + CURRENT_TIME_MILLIS + "-" + "5");
    }

    @Test
    public void test06AddDataGroupStringIDataGroup() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        IPublisher p = getTestPublisher(r);
        // should choose a publisher support data group
        assertTrue(p.getSupportDataGroup());
        // add 20 data group, should success
        String dataGroupPrefix = "data.group.";
        List<IDataGroup> dgs = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            InMemoryDataGroup dg = new InMemoryDataGroup();
            dg.setDataGroupId(getEntityId(DATAGROUP_PREFIX));
            dg.setDataGroup(dataGroupPrefix + i);
            dg.setAccessToken("ACCESS_TOKEN" + i);
            dg.setRefreshToken("REFRESH_TOKEN" + i);
            dgs.add(dg);
        }
        r.addDataGroup(p.getPublisherId(), dgs);
        dgs = r.findDataGroups(p.getPublisherId(), 0, 100);
        assertEquals(20, dgs.size());
        // add data group without required attributes, should throw exception
        assertThrows(EngineException.class, () -> {
            r.addDataGroup(p.getPublisherId(), new InMemoryDataGroup());
        });
        // add data group with unknown publisher id, should throw exception
        assertThrows(EngineException.class, () -> {
            InMemoryDataGroup dg = new InMemoryDataGroup();
            dg.setDataGroupId(getEntityId(DATAGROUP_PREFIX));
            dg.setDataGroup(dataGroupPrefix + -1);
            dg.setAccessToken("ACCESS_TOKEN" + -1);
            dg.setRefreshToken("REFRESH_TOKEN" + -1);
            r.addDataGroup("unknown-publisher-id", dg);
        });
        // add data group with existing data group id, should throw exception
        String dgid = dgs.get(ThreadLocalRandom.current().nextInt(19)).getDataGroupId();
        assertThrows(EngineException.class, () -> {
            InMemoryDataGroup dg = new InMemoryDataGroup();
            dg.setDataGroupId(dgid);
            dg.setDataGroup(dataGroupPrefix + -1);
            dg.setAccessToken("ACCESS_TOKEN" + -1);
            dg.setRefreshToken("REFRESH_TOKEN" + -1);
            r.addDataGroup(p.getPublisherId(), dg);
        });
        // add data group with existing data group name, should throw exception
        String dgname = dgs.get(ThreadLocalRandom.current().nextInt(19)).getDataGroup();
        assertThrows(EngineException.class, () -> {
            InMemoryDataGroup dg = new InMemoryDataGroup();
            dg.setDataGroupId(getEntityId(DATAGROUP_PREFIX));
            dg.setDataGroup(dgname);
            dg.setAccessToken("ACCESS_TOKEN" + -1);
            dg.setRefreshToken("REFRESH_TOKEN" + -1);
            r.addDataGroup(p.getPublisherId(), dg);
        });
        assertThrows(EngineException.class, () -> {
            List<IDataGroup> tmps = new ArrayList<>();
            InMemoryDataGroup dg = new InMemoryDataGroup();
            dg.setDataGroupId(getEntityId(DATAGROUP_PREFIX));
            dg.setDataGroup(dataGroupPrefix + "duplicated");
            dg.setAccessToken("ACCESS_TOKEN" + -1);
            dg.setRefreshToken("REFRESH_TOKEN" + -1);
            tmps.add(dg);
            dg = new InMemoryDataGroup();
            dg.setDataGroupId(getEntityId(DATAGROUP_PREFIX));
            dg.setDataGroup(dataGroupPrefix + "duplicated");
            dg.setAccessToken("ACCESS_TOKEN" + -1);
            dg.setRefreshToken("REFRESH_TOKEN" + -1);
            tmps.add(dg);
            r.addDataGroup(p.getPublisherId(), tmps);
        });
    }

    private IDataGroup getTestDataGroup(IRegistry r) {
        return r.findDataGroup(DATAGROUP_PREFIX + "-" + CURRENT_TIME_MILLIS + "-" + "11");
    }

    @Test
    public void test07AddSubscriber() {
//        StackTraceElement[] es = Thread.currentThread().getStackTrace();
//        for (StackTraceElement e : es) {
//            System.out.println(String.format("%s.%s(Line Number:%d)", e.getClassName(), e.getMethodName(), e.getLineNumber()));
//        }
        // add 7 subscribers, should success
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        List<IOrganization> os = r.findAllOrganizations(0, 100);
        for (int i = 0; i < 7; i++) {
            InMemorySubscriber s = new InMemorySubscriber();
            s.setSubscriberId(getEntityId(SUBSCRIBER_PREFIX));
            s.setOrganizationId(os.get(i).getOrganizationId());
            r.addSubscriber(s);
        }
        List<ISubscriber> ss = r.findAllSubscribers(0, 100);
        assertEquals(7, ss.size());
        // add subscriber without required attributes, should throw exception
        assertThrows(EngineException.class, () -> {
            r.addSubscriber(new InMemorySubscriber());
        });
        // add subscriber with unknown organization id, should throw exception
        assertThrows(EngineException.class, () -> {
            InMemorySubscriber s = new InMemorySubscriber();
            s.setSubscriberId(getEntityId(SUBSCRIBER_PREFIX));
            s.setOrganizationId("the-organization-id-unknown");
            r.addSubscriber(s);
        });
        // add subscriber with existing subscriber id, should throw exception
        String sid = ss.get(ThreadLocalRandom.current().nextInt(6)).getSubscriberId();
        assertThrows(EngineException.class, () -> {
            InMemorySubscriber s = new InMemorySubscriber();
            s.setSubscriberId(sid);
            s.setOrganizationId(os.get(7).getOrganizationId());
            r.addSubscriber(s);
        });
    }

    @Test
    public void test08UpdateSubscriber() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        List<ISubscriber> ps = r.findAllSubscribers(0, 100);
        // update subscriber to unknown subscriber id, should throw exception
        assertThrows(EngineException.class, () -> {
            ISubscriber s = ps.get(5);
            s.setSubscriberId("unknow-publisher-id");
            r.updateSubscriber(s);
        });
        // update subscriber attribute "organizationId", should throw exception
        assertThrows(EngineException.class, () -> {
            ISubscriber s = ps.get(3);
            s.setOrganizationId("ORGANIZATION-" + CURRENT_TIME_MILLIS + "-A");
            r.updateSubscriber(s);
        });
        // should success
        ISubscriber s = ps.get(1);
        r.updateSubscriber(s);
    }

    @Test
    public void test09RemoveSubscriber() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        List<ISubscriber> ps = r.findAllSubscribers(0, 100);
        ISubscriber s = getTestSubscriber(r);
        ArrayList<Integer> deletedIndex = new ArrayList<>();
        for (int i = 0; i < ps.size(); i++) {
            if (!Objects.equals(ps.get(i).getSubscriberId(), s.getSubscriberId())) {
                deletedIndex.add(i);
                r.removeSubscriber(ps.get(i));
            }
        }
        ISubscriber tmpSubscriber = ps.get(deletedIndex.get(0));
        ps = r.findAllSubscribers(0, 100);
        assertEquals(1, ps.size());
        assertThrows(EngineException.class, () -> {
            r.removeSubscriber(tmpSubscriber);
        });
    }

    private ISubscriber getTestSubscriber(IRegistry r) {
        return r.findSubscriber(SUBSCRIBER_PREFIX + "-" + CURRENT_TIME_MILLIS + "-" + "3");
    }

    @Test
    public void test10AddWebhook() {
        // add webhook, should be success
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        IPublisher p = getTestPublisher(r);
        ISubscriber s = getTestSubscriber(r);
        InMemoryWebhook w = new InMemoryWebhook();
        w.setWebhookId(getEntityId(WEBHOOK_PREFIX));
        w.setPublisherId(p.getPublisherId());
        w.setPublisherVersion(p.getVersion());
        w.setSubscriberId(s.getSubscriberId());
        w.setSecurityStrategy(SecurityStrategy.TOKEN);
        w.setWebhookUrl(WEBHOOK_URL);
        w.setWebhookSecret("WEBHOOK_SECRET_TEST");
        w.setAccessToken("ACCESS_TOKEN_TEST");
        w.setRefreshToken("REFRESH_TOKEN_TEST");
        w.setWebhookStatus(WebhookStatus.TEST);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YXBpdXNlcjE6NjY2NjY2NjY2Ng==");
        headers.put("test-header-01", "test-value-01");
        headers.put("test-header-02", "test-value-02");
        w.setCustomizedHeaders(headers);
        r.addWebhook(w);
        List<IWebhook> ws = r.findWebhooks(s.getSubscriberId(), 0, 100);
        assertEquals(1, ws.size());
        // add webhook without required attributes, should throw exception
        assertThrows(EngineException.class, () -> {
            r.addWebhook(new InMemoryWebhook());
        });
        // add webhook with webhook id already exists, should throw exception
        assertThrows(EngineException.class, () -> {
            InMemoryWebhook tmp = new InMemoryWebhook();
            tmp.setWebhookId(ws.get(0).getWebhookId());
            tmp.setPublisherId(p.getPublisherId());
            tmp.setPublisherVersion(p.getVersion());
            tmp.setSubscriberId(s.getSubscriberId());
            tmp.setSecurityStrategy(SecurityStrategy.TOKEN);
            tmp.setWebhookUrl(WEBHOOK_URL);
            tmp.setWebhookSecret("WEBHOOK_SECRET_TEST");
            tmp.setAccessToken("ACCESS_TOKEN_TEST");
            tmp.setRefreshToken("REFRESH_TOKEN_TEST");
            tmp.setWebhookStatus(WebhookStatus.TEST);
            tmp.setCustomizedHeaders(headers);
            r.addWebhook(tmp);
        });
        // add webhook with unknown publisher id, should throw exception
        assertThrows(EngineException.class, () -> {
            InMemoryWebhook tmp = new InMemoryWebhook();
            tmp.setWebhookId(getEntityId(WEBHOOK_PREFIX));
            tmp.setPublisherId("unknown-publisher-id");
            tmp.setPublisherVersion(p.getVersion());
            tmp.setSubscriberId(s.getSubscriberId());
            tmp.setSecurityStrategy(SecurityStrategy.TOKEN);
            tmp.setWebhookUrl(WEBHOOK_URL);
            tmp.setWebhookSecret("WEBHOOK_SECRET_TEST");
            tmp.setAccessToken("ACCESS_TOKEN_TEST");
            tmp.setRefreshToken("REFRESH_TOKEN_TEST");
            tmp.setWebhookStatus(WebhookStatus.TEST);
            tmp.setCustomizedHeaders(headers);
            r.addWebhook(tmp);
        });
        // add webhook with unknown subscriber id, should throw exception
        assertThrows(EngineException.class, () -> {
            InMemoryWebhook tmp = new InMemoryWebhook();
            tmp.setWebhookId(getEntityId(WEBHOOK_PREFIX));
            tmp.setPublisherId(p.getPublisherId());
            tmp.setPublisherVersion(p.getVersion());
            tmp.setSubscriberId("unknown-subscriber-id");
            tmp.setSecurityStrategy(SecurityStrategy.TOKEN);
            tmp.setWebhookUrl(WEBHOOK_URL);
            tmp.setWebhookSecret("WEBHOOK_SECRET_TEST");
            tmp.setAccessToken("ACCESS_TOKEN_TEST");
            tmp.setRefreshToken("REFRESH_TOKEN_TEST");
            tmp.setWebhookStatus(WebhookStatus.TEST);
            tmp.setCustomizedHeaders(headers);
            r.addWebhook(tmp);
        });
    }

    @Test
    public void test11UpdateWebhook() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        InMemoryWebhook w = (InMemoryWebhook) getTestWebhook(r);
        // update webhook without required attributes, should fail
        assertThrows(EngineException.class, () -> {
            r.updateWebhook(new InMemoryWebhook());
        });
        // update webhook with un-existing webhook id, should fail
        assertThrows(EngineException.class, () -> {
            try {
                InMemoryWebhook tmp = (InMemoryWebhook) w.clone();
                tmp.setWebhookId("unknown-webhook-id");
                r.updateWebhook(tmp);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
        // change webhook publisher id, should fail
        assertThrows(EngineException.class, () -> {
            try {
                InMemoryWebhook tmp = (InMemoryWebhook) w.clone();
                tmp.setPublisherId("new-publisher-id");
                r.updateWebhook(tmp);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
        // change webhook subscriber id, should fail
        assertThrows(EngineException.class, () -> {
            try {
                InMemoryWebhook tmp = (InMemoryWebhook) w.clone();
                tmp.setSubscriberId("new-subscriber-id");
                r.updateWebhook(tmp);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
        // should success
        try {
            InMemoryWebhook tmp = (InMemoryWebhook) w.clone();
            tmp.setTrustedIps(new String[] { "192.168.0.111", "192.168.0.222" });
            r.updateWebhook(tmp);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private IWebhook getTestWebhook(IRegistry r) {
        return r.findWebhook(WEBHOOK_PREFIX + "-" + CURRENT_TIME_MILLIS + "-0");
    }

    @Test
    public void test12ActivateWebhook() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        InMemoryWebhook w = (InMemoryWebhook) getTestWebhook(r);
        r.activateWebhook(w);
        // activate webhook without required attributes, should fail
        assertThrows(EngineException.class, () -> {
            r.activateWebhook(new InMemoryWebhook());
        });
        // activate webhook with unknown webhook id, should fail
        assertThrows(EngineException.class, () -> {
            try {
                InMemoryWebhook tmp = (InMemoryWebhook) w.clone();
                tmp.setWebhookId("unknown-webhook-id");
                r.activateWebhook(tmp);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
        // activate webhook already activated, should fail
        assertThrows(EngineException.class, () -> {
            r.activateWebhook(w);
        });
    }

    @Test
    public void test13DeactivateWebhook() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        InMemoryWebhook w = (InMemoryWebhook) getTestWebhook(r);
        r.deactivateWebhook(w);
        // deactivate webhook without required attributes, should fail
        assertThrows(EngineException.class, () -> {
            r.deactivateWebhook(new InMemoryWebhook());
        });
        // deactivate webhook with unknown webhook id, should fail
        assertThrows(EngineException.class, () -> {
            try {
                InMemoryWebhook tmp = (InMemoryWebhook) w.clone();
                tmp.setWebhookId("unknown-webhook-id");
                r.deactivateWebhook(tmp);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
        // deactivate webhook already deactivated, should fail
        assertThrows(EngineException.class, () -> {
            r.deactivateWebhook(w);
        });
        // activate it again for the following test
        r.activateWebhook(w);
    }

    @Test
    public void test14SubscribeEventIWebhookIEvent() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        InMemoryWebhook w = (InMemoryWebhook) getTestWebhook(r);
        IEvent e = getTestEvent(r);
        r.subscribeEvent(w, e);
        // subscribe will fail if webhook id does not exist
        assertThrows(EngineException.class, () -> {
            try {
                InMemoryWebhook tmp = (InMemoryWebhook) w.clone();
                tmp.setWebhookId("unknown-webhook-id");
                r.subscribeEvent(tmp, e);
            } catch (CloneNotSupportedException e1) {
                e1.printStackTrace();
            }
        });
        // test to trigger message, should not be triggered, because data group is not
        // subscribed yet
        triggerMessage("SubscribeEvent");
    }

    @Test
    public void test15UnsubscribeEventIWebhookIEvent() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        InMemoryWebhook w = (InMemoryWebhook) getTestWebhook(r);
        IEvent e = getTestEvent(r);
        r.unsubscribeEvent(w, e);
        // subscribe will fail if webhook id does not exist
        assertThrows(EngineException.class, () -> {
            try {
                InMemoryWebhook tmp = (InMemoryWebhook) w.clone();
                tmp.setWebhookId("unknown-webhook-id");
                r.unsubscribeEvent(tmp, e);
            } catch (CloneNotSupportedException e1) {
                e1.printStackTrace();
            }
        });
        // subscribe again for the following test
        r.subscribeEvent(w, e);
    }

    @Test
    public void test16SubscribeDataGroupIWebhookIDataGroup() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        InMemoryWebhook w = (InMemoryWebhook) getTestWebhook(r);
        IDataGroup dg = getTestDataGroup(r);
        r.subscribeDataGroup(w, dg);
        // subscribe will fail if webhook id does not exist
        assertThrows(EngineException.class, () -> {
            try {
                InMemoryWebhook tmp = (InMemoryWebhook) w.clone();
                tmp.setWebhookId("unknown-webhook-id");
                r.subscribeDataGroup(tmp, dg);
            } catch (CloneNotSupportedException e1) {
                e1.printStackTrace();
            }
        });
        // test to trigger message
        triggerMessage("SubscribeDataGroup");
    }

    @Test
    public void test17UnsubscribeDataGroupIWebhookIDataGroup() {
        IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
        InMemoryWebhook w = (InMemoryWebhook) getTestWebhook(r);
        IDataGroup dg = getTestDataGroup(r);
        r.unsubscribeDataGroup(w, dg);
        // subscribe will fail if webhook id does not exist
        assertThrows(EngineException.class, () -> {
            try {
                InMemoryWebhook tmp = (InMemoryWebhook) w.clone();
                tmp.setWebhookId("unknown-webhook-id");
                r.unsubscribeDataGroup(tmp, dg);
            } catch (CloneNotSupportedException e1) {
                e1.printStackTrace();
            }
        });
        // test to trigger message, should not be triggered, because already been
        // unsubscribed
        triggerMessage("UnsubscribeDataGroup");
        // subscribe again for the following test
        r.subscribeDataGroup(w, dg);
    }

    private void triggerMessage(String triggerCaller) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        final IRegistry registry = engine.getRegistry();
        ExecutorService es = Executors.newFixedThreadPool(1);
        es.submit(() -> {
            IPublisher publisher = getTestPublisher(registry);
            IDataGroup dataGroup = getTestDataGroup(registry);
            IEvent event = getTestEvent(registry);
            for (int j = 0; j < 3; j++) {
                Message message = new Message(publisher.getPublisherId(), publisher.getVersion(), event.getEventType(), "test", "application/json", "UTF-8",
                        dataGroup.getDataGroup(), "MESSAGE-" + CURRENT_TIME_MILLIS + "-" + j,
                        "{\"data\": \"(" + triggerCaller + ")This is the test payload-" + CURRENT_TIME_MILLIS + "-" + j + "\"}");
                engine.trigger(message);
            }
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test18TriggerMessageWithoutDataGroup() {
        IEngine engine = IEngineFactory.getFactoryInstance().getEngine();
        IRegistry r = engine.getRegistry();
        List<IOrganization> os = r.findAllOrganizations(0, 100);
        IPublisher p = r.findPublisher(PUBLISHER_PREFIX + "-" + CURRENT_TIME_MILLIS + "-1");
        // add 1 events, should success
        InMemoryEvent e = new InMemoryEvent();
        e.setEventId(EVENT_PREFIX + "-" + CURRENT_TIME_MILLIS + "-WIHTOUT-DATAGROUP");
        e.setCharset("UTF-8");
        e.setContentType("application/json");
        e.setEventTag("test");
        e.setEventType("test.event.type.0");
        r.addEvent(p.getPublisherId(), e);

        InMemorySubscriber s = new InMemorySubscriber();
        s.setSubscriberId(SUBSCRIBER_PREFIX + "-" + CURRENT_TIME_MILLIS + "-WIHTOUT-DATAGROUP");
        s.setOrganizationId(os.get(9).getOrganizationId());
        r.addSubscriber(s);

        InMemoryWebhook w = new InMemoryWebhook();
        w.setWebhookId(WEBHOOK_PREFIX + "-" + CURRENT_TIME_MILLIS + "-WIHTOUT-DATAGROUP");
        w.setPublisherId(p.getPublisherId());
        w.setPublisherVersion(p.getVersion());
        w.setSubscriberId(s.getSubscriberId());
        w.setSecurityStrategy(SecurityStrategy.TOKEN);
        w.setWebhookUrl(WEBHOOK_URL);
        w.setWebhookSecret("WEBHOOK_SECRET_TEST");
        w.setAccessToken("ACCESS_TOKEN_TEST");
        w.setRefreshToken("REFRESH_TOKEN_TEST");
        w.setWebhookStatus(WebhookStatus.TEST);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YXBpdXNlcjE6NjY2NjY2NjY2Ng==");
        headers.put("test-header-01", "test-value-01");
        headers.put("test-header-02", "test-value-02");
        w.setCustomizedHeaders(headers);
        r.addWebhook(w);

        r.subscribeEvent(w, e);

        Message message = new Message(p.getPublisherId(), p.getVersion(), e.getEventType(), "test", "application/json", "UTF-8", null,
                "MESSAGE-" + CURRENT_TIME_MILLIS + "-withoutdatagroup",
                "{\"data\": \"(WithoutDataGroup)This is the test payload-" + CURRENT_TIME_MILLIS + "-withoutdatagroup\"}");
        engine.trigger(message);
    }
}
