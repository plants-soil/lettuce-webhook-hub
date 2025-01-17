package com.plantssoil.webhook.core.impl;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.httpclient.IHttpCallback;
import com.plantssoil.common.httpclient.IHttpPoster;
import com.plantssoil.common.httpclient.IHttpResponse;
import com.plantssoil.webhook.core.IDataGroup;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IRegistry;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.IWebhookPoster;
import com.plantssoil.webhook.core.Message;
import com.plantssoil.webhook.core.exception.EngineException;

/**
 * The webhook url poster, which with retry logics:
 * <ul>
 * <li>The first call webhook url: success(HttpResponse status code is 20x) -
 * end the process, fail - go to the retry process in next step</li>
 * <li>The first retry webhook url 5 seconds later: success(HttpResponse status
 * code is 20x) - end the process, fail - go to the next retry process in next
 * step</li>
 * <li>The second retry webhook url 30 seconds later: end the process whatever
 * success(HttpResponse status code is 20x) or fail</li>
 * </ul>
 * 
 * @author danialdy
 * @Date 27 Nov 2024 11:24:20 am
 */
public class WebhookPoster implements IWebhookPoster {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebhookPoster.class.getName());
    private static volatile IWebhookPoster instance;
    private int connectionPoolSize, maxRequests, maxRequestsPerHost, retryQueueCapacity5, retryQueueCapacity30;
    private ScheduledExecutorService retryScheduler; // scheduler every 5 seconds
    private RetryWebhookQueue retryWebhooks5; // retry webhook queue 5 seconds delay
    private RetryWebhookQueue retryWebhooks30; // retry webhook queue 30 seconds delay
    private ThreadFactory retrySchedulerThreadFactory = new NamedThreadFactory("Webhook-Retry-Scheduler"); // create the ThreadFactory to name threads for Retry

    class NamedThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final String namePrefix;

        NamedThreadFactory(String factoryName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = factoryName;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix, 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    class HttpCallback implements IHttpCallback {
        private Message message;
        private IWebhook webhook;
        private RetryWebhookQueue retryQueue;
        private long executeMilliseconds;

        public HttpCallback(Message message, IWebhook webhook, RetryWebhookQueue retryQueue, long executeMilliseconds) {
            this.message = message;
            this.webhook = webhook;
            this.retryQueue = retryQueue;
            this.executeMilliseconds = executeMilliseconds;
        }

        @Override
        public void onFailure(Exception e) {
            if (this.retryQueue == null) {
                // discard the exception and the failed task, no need retry any more
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(String.format("Call webhook %s with message: %s failed 3 times, exception: %s", this.webhook.getWebhookUrl(),
                            this.message.getPayload(), e.getMessage()));
                }
                return;
            }
            this.retryQueue.add(this.message, this.webhook, this.executeMilliseconds);
        }

        @Override
        public void onResponse(IHttpResponse response) throws IOException {
            if (this.retryQueue == null) {
                // discard the exception and the failed task, no need retry any more
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(String.format("Call webhook %s with message: %s failed 3 times, statusCode: %d, responseBody: %s", this.webhook.getWebhookUrl(),
                            this.message.getPayload(), response.getStatusCode(), response.getBody()));
                }
                return;
            }
            // the response code should be 20x, indicates call webhook url successfully
            // otherwise, put the message into retry queue
            if (!(response.getStatusCode() >= 200 && response.getStatusCode() < 210)) {
                this.retryQueue.add(this.message, this.webhook, this.executeMilliseconds);
            }
        }

    }

    private WebhookPoster() {
        // initial configuration
        initialConfiguration();
        // initial retry queues (5 secs & 30 secs)
        initialRetryQueues();
        // initial retry scheduler 5 seconds delay, don't use ScheduledExecutorService
        // because ScheduledExecutorService will start threads when the schedule start.
        // 5 seconds & 30 seconds retry queue will be checked every 5 seconds
        initialRetryScheduler();
    }

    private void initialConfiguration() {
        IConfiguration configuration = ConfigFactory.getInstance().getConfiguration();
        this.connectionPoolSize = configuration.getInt(LettuceConfiguration.WEBHOOK_ENGINE_CONNECTION_POOL_SIZE, 5);
        this.maxRequests = configuration.getInt(LettuceConfiguration.WEBHOOK_ENGINE_MAX_REQUESTS, 64);
        this.maxRequestsPerHost = configuration.getInt(LettuceConfiguration.WEBHOOK_ENGINE_MAX_REQUESTS_PER_HOST, 5);
        this.retryQueueCapacity5 = configuration.getInt(LettuceConfiguration.WEBHOOK_ENGINE_RETRY_QUEUE_CAPACITY5, 10000);
        this.retryQueueCapacity30 = configuration.getInt(LettuceConfiguration.WEBHOOK_ENGINE_RETRY_QUEUE_CAPACITY30, 10000);
    }

    private void initialRetryQueues() {
        this.retryWebhooks5 = new RetryWebhookQueue(this.retryQueueCapacity5);
        this.retryWebhooks30 = new RetryWebhookQueue(this.retryQueueCapacity30);
    }

    private void initialRetryScheduler() {
        this.retryScheduler = Executors.newSingleThreadScheduledExecutor(this.retrySchedulerThreadFactory);
        this.retryScheduler.scheduleAtFixedRate(() -> {
            retryWebhook5();
            retryWebhook30();
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void retryWebhook5() {
        List<RetryWebhookTask> list = this.retryWebhooks5.webhookTasksTimeUp();
        for (RetryWebhookTask task : list) {
            try {
                post(task.getMessage(), task.getWebhook(),
                        new HttpCallback(task.getMessage(), task.getWebhook(), this.retryWebhooks30, System.currentTimeMillis() + 30 * 1000));
            } catch (Exception e) {
                // retry after 30 seconds if exception happens
                this.retryWebhooks30.add(task.getMessage(), task.getWebhook(), System.currentTimeMillis() + 30 * 1000);
            }
        }
    }

    private void retryWebhook30() {
        List<RetryWebhookTask> list = this.retryWebhooks30.webhookTasksTimeUp();
        for (RetryWebhookTask task : list) {
            try {
                // this is the last try, just post webhook url
                // no more further process, no matter success or not
                post(task.getMessage(), task.getWebhook(), new HttpCallback(null, null, null, 0));
            } catch (Exception e) {
                // discard the exception and the failed task, no need retry any more
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(String.format("Call webhook %s with message: %s failed 3 times, exception: %s", task.getWebhook().getWebhookUrl(),
                            task.getMessage().getPayload(), e.getMessage()));
                }
            }
        }
    }

    /**
     * Get the webhook poster singleton instance
     * 
     * @return webhook poster singleton instance
     */
    public static IWebhookPoster getInstance() {
        if (instance == null) {
            synchronized (WebhookPoster.class) {
                if (instance == null) {
                    // create poster instance (use proxy to AOP logging)
                    WebhookPoster poster = new WebhookPoster();
                    instance = poster;
//                    instance = (IWebhookPoster) WebhookLoggingHandler.createProxy(poster);
                }
            }
        }
        return instance;
    }

    @Override
    public void postWebhook(final Message message, final IWebhook webhook) {
        try {
            post(message, webhook, new HttpCallback(message, webhook, this.retryWebhooks5, System.currentTimeMillis() + 5 * 1000));
        } catch (Exception e) {
            this.retryWebhooks5.add(message, webhook, System.currentTimeMillis() + 5 * 1000);
        }
    }

    private void post(Message message, IWebhook webhook, IHttpCallback callback) {
        IHttpPoster poster = IHttpPoster
                .createInstance(com.plantssoil.common.httpclient.IHttpPoster.SecurityStrategy.valueOf(webhook.getSecurityStrategy().name()));
        if (message.getDataGroup() != null) {
            // get access token from data group (if supported)
            IRegistry r = IEngineFactory.getFactoryInstance().getEngine().getRegistry();
            IDataGroup dataGroup = r.findSubscribedDataGroup(webhook.getWebhookId(), message.getDataGroup());
            if (dataGroup != null) {
                poster.setAccessToken(dataGroup.getAccessToken());
            } else {
                throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20003, String.format(
                        "Webhook (webhookId: %s, url: %s) does not subscribe event (%s) from publisher (publisherId: %s, version: %s, dataGroup: %s), or data group (%s) does not exist!",
                        webhook.getWebhookId(), webhook.getWebhookUrl(), message.getEventType(), webhook.getPublisherId(), webhook.getPublisherVersion(),
                        message.getDataGroup(), message.getDataGroup()));
            }
        } else {
            // get access token from webhook
            poster.setAccessToken(webhook.getAccessToken());
        }
        poster.setCharset(message.getCharset());
        poster.setMediaType(message.getContentType());
        poster.setMaxIdleConnections(this.connectionPoolSize);
        poster.setMaxRequests(this.maxRequests);
        poster.setMaxRequestsPerHost(this.maxRequestsPerHost);

        poster.post(webhook.getWebhookUrl(), webhook.getCustomizedHeaders(), message.getRequestId(), message.getPayload(), callback);
    }
}
