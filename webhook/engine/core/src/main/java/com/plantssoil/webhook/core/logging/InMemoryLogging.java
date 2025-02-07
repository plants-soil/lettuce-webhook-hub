package com.plantssoil.webhook.core.logging;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfigurable;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.httpclient.impl.NamedThreadFactory;
import com.plantssoil.webhook.core.ILogging;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.IWebhookLog;
import com.plantssoil.webhook.core.IWebhookLogLine;
import com.plantssoil.webhook.core.Message;

/**
 * The in-memory logging implementation which will hold logging information<br/>
 * All data will be lost when JVM shutdown<br/>
 * It's only for demonstration purpose, SHOULD AVOID be used in production
 * environment<br/>
 * 
 * @author danialdy
 * @Date 6 Feb 2025 11:57:18 am
 */
public class InMemoryLogging implements ILogging, IConfigurable {
    /**
     * key - request id, value - in-memory webhook log
     */
    private Map<String, InMemoryWebhookLog> logs = new ConcurrentHashMap<>();
    /**
     * key - request id, value - list of in-memory webhook log lines
     */
    private Map<String, List<InMemoryWebhookLogLine>> logLines = new ConcurrentHashMap<>();
    private AtomicLong logLineId = new AtomicLong(0);
    private ExecutorService loggingExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("Persisted-Logging-Executor"));
    private static String PERSISTENCE_FACTORY_CONFIGURABLE = ConfigFactory.getInstance().getConfiguration()
            .getString(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE);

    private List<InMemoryWebhookLogLine> getLogLines(String requestId) {
        List<InMemoryWebhookLogLine> lines = this.logLines.get(requestId);
        if (lines == null) {
            synchronized (requestId.intern()) {
                lines = this.logLines.get(requestId);
                if (lines == null) {
                    lines = new ArrayList<>();
                    this.logLines.put(requestId, lines);
                }
            }
        }
        return lines;
    }

    @Override
    public void triggerMessage(Message message) {
        if (PERSISTENCE_FACTORY_CONFIGURABLE == null) {
            return;
        }

        loggingExecutor.submit(() -> {
            InMemoryWebhookLog log = logs.get(message.getRequestId());
            if (log == null) {
                log = new InMemoryWebhookLog();
                log.setPublisherId(message.getPublisherId());
                log.setVersion(message.getVersion());
                log.setDataGroup(message.getDataGroup());
                log.setEventType(message.getEventType());
                log.setEventTag(message.getEventTag());
                log.setContentType(message.getContentType());
                log.setCharset(message.getCharset());
                log.setRequestId(message.getRequestId());
                log.setPayload(message.getPayload());
                log.setWebhookStatus("Published");
                log.setTriggerTime(new Date());
                logs.put(message.getRequestId(), log);
            }
        });
    }

    @Override
    public void dispatchMessage(Message message, IWebhook webhook, int tryTime) {
        if (PERSISTENCE_FACTORY_CONFIGURABLE == null) {
            return;
        }

        loggingExecutor.submit(() -> {
            List<InMemoryWebhookLogLine> lines = getLogLines(message.getRequestId());
            InMemoryWebhookLogLine line = new InMemoryWebhookLogLine();
            line.setLogLineId(Long.toString(logLineId.getAndIncrement()));
            line.setSubscriberId(webhook.getWebhookId());
            line.setWebhookId(webhook.getWebhookId());
            line.setRequestId(message.getRequestId());
            line.setLogType("Dispatched");
            line.setLogTime(new Date());
            line.setTryTime(tryTime);
            lines.add(line);
        });
    }

    @Override
    public void responseMessage(Message message, IWebhook webhook, String responseType, String information) {
        if (PERSISTENCE_FACTORY_CONFIGURABLE == null) {
            return;
        }

        loggingExecutor.submit(() -> {
            List<InMemoryWebhookLogLine> lines = getLogLines(message.getRequestId());
            InMemoryWebhookLogLine line = new InMemoryWebhookLogLine();
            line.setLogLineId(Long.toString(logLineId.getAndIncrement()));
            line.setSubscriberId(webhook.getWebhookId());
            line.setWebhookId(webhook.getWebhookId());
            line.setRequestId(message.getRequestId());
            line.setLogType(responseType);
            line.setInformation(information);
            line.setLogTime(new Date());
            lines.add(line);
        });
    }

    @Override
    public List<IWebhookLog> findAllWebhookLogs(String publisherId, String dataGroup, int page, int pageSize) {
        List<IWebhookLog> list = new ArrayList<>();
        if (publisherId == null) {
            return list;
        }

        int startIndex = page * pageSize;
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (startIndex >= this.logs.size()) {
            return list;
        }
        int index = 0, added = 0;
        for (InMemoryWebhookLog log : this.logs.values()) {
            if (index < startIndex) {
                index++;
                continue;
            }
            if (Objects.equals(publisherId, log.getPublisherId()) && (dataGroup == null || dataGroup.equals(log.getDataGroup()))) {
                list.add(log);
                added++;
            }
            if (added > pageSize) {
                break;
            }
        }
        return list;
    }

    @Override
    public List<IWebhookLog> findAllWebhookLogs(String webhookId, int page, int pageSize) {
        List<IWebhookLog> list = new ArrayList<>();
        if (webhookId == null) {
            return list;
        }
        int startIndex = page * pageSize;
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (startIndex >= this.logs.size()) {
            return list;
        }
        int index = 0, added = 0;
        Set<String> requestIds = new LinkedHashSet<>();
        for (Entry<String, List<InMemoryWebhookLogLine>> entry : this.logLines.entrySet()) {
            if (containsWebhook(entry.getValue(), webhookId) && index < startIndex) {
                index++;
                continue;
            }
            if (containsWebhook(entry.getValue(), webhookId)) {
                requestIds.add(entry.getKey());
                added++;
            }
            if (added > pageSize) {
                break;
            }
        }
        for (String requestId : requestIds) {
            list.add(this.logs.get(requestId));
        }
        return list;
    }

    private boolean containsWebhook(List<InMemoryWebhookLogLine> lines, String webhookId) {
        boolean contains = false;
        for (InMemoryWebhookLogLine line : lines) {
            if (Objects.equals(line.getWebhookId(), webhookId)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    @Override
    public List<IWebhookLogLine> findWebhookLogLines(String requestId, String webhookId) {
        List<IWebhookLogLine> list = new ArrayList<>();
        if (requestId == null || webhookId == null) {
            return list;
        }
        List<InMemoryWebhookLogLine> lines = this.logLines.get(requestId);
        if (lines == null) {
            return list;
        }
        for (InMemoryWebhookLogLine line : lines) {
            if (Objects.equals(line.getWebhookId(), webhookId)) {
                list.add(line);
            }
        }
        return list;
    }

}
