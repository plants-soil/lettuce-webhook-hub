package com.plantssoil.webhook.core.impl;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.IConfiguration;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.httpclient.IHttpPoster;
import com.plantssoil.webhook.core.IEngineFactory;
import com.plantssoil.webhook.core.IEvent;
import com.plantssoil.webhook.core.IWebhook;
import com.plantssoil.webhook.core.IWebhookPoster;
import com.plantssoil.webhook.core.Message;
import com.plantssoil.webhook.core.logging.WebhookLoggingHandler;

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
    private final static int PAGE_SIZE = 50;
    private static volatile IWebhookPoster instance;
    private ThreadPoolExecutor executor;
    private ScheduledExecutorService retryScheduler5; // scheduler 5 seconds delay
    private RetryWebhookQueue retryWebhooks5; // retry webhook queue 5 seconds delay
    private ScheduledExecutorService retryScheduler30; // scheduler 30 seconds delay
    private RetryWebhookQueue retryWebhooks30; // retry webhook queue 30 seconds delay

    private WebhookPoster() {
        // initial thread pool for webhook poster
        initialExecutor();
        // initial retry scheduler 5 seconds delay, don't use ScheduledExecutorService
        // because ScheduledExecutorService will start threads when the schedule start
        initialRetryScheduler5();
        // initial retry scheduler 30 seconds delay, don't use ScheduledExecutorService
        // because ScheduledExecutorService will start threads when the schedule start
        initialRetryScheduler30();
    }

    private void initialExecutor() {
        IConfiguration configuration = ConfigFactory.getInstance().getConfiguration();
        int corePoolSize = configuration.getInt(LettuceConfiguration.WEBHOOK_ENGINE_CORE_POOL_SIZE, 100);
        int maximumPoolSize = configuration.getInt(LettuceConfiguration.WEBHOOK_ENGINE_MAXIMUM_POOL_SIZE, 200);
        int workQueueCapacity = configuration.getInt(LettuceConfiguration.WEBHOOK_ENGINE_WORK_QUEUE_CAPACITY, 1000);
        this.executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(workQueueCapacity));
    }

    private void initialRetryScheduler5() {
        IConfiguration configuration = ConfigFactory.getInstance().getConfiguration();
        int retryQueueCapacity5 = configuration.getInt(LettuceConfiguration.WEBHOOK_ENGINE_RETRY_QUEUE_CAPACITY5, 1000);
        this.retryWebhooks5 = new RetryWebhookQueue(retryQueueCapacity5);
        this.retryScheduler5 = Executors.newScheduledThreadPool(1);
        this.retryScheduler5.scheduleAtFixedRate(() -> {
            List<RetryWebhookTask> list = retryWebhooks5.webhookTasksTimeUp();
            for (RetryWebhookTask task : list) {
                CompletableFuture<HttpResponse<String>> f = post(task.getMessage(), task.getWebhook());
                f.thenAccept(response -> {
                    // the response code should be 20x, indicates call webhook url successfully
                    // otherwise, put the message into retry queue, which will retry 30 seconds
                    // later
                    if (!(response.statusCode() >= 200 && response.statusCode() < 210)) {
                        this.retryWebhooks30.add(task.getMessage(), task.getWebhook(), System.currentTimeMillis() + 30 * 1000);
                    }
                });
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void initialRetryScheduler30() {
        IConfiguration configuration = ConfigFactory.getInstance().getConfiguration();
        int retryQueueCapacity30 = configuration.getInt(LettuceConfiguration.WEBHOOK_ENGINE_RETRY_QUEUE_CAPACITY30, 1000);
        this.retryWebhooks30 = new RetryWebhookQueue(retryQueueCapacity30);
        this.retryScheduler30 = Executors.newScheduledThreadPool(1);
        this.retryScheduler30.scheduleAtFixedRate(() -> {
            List<RetryWebhookTask> list = retryWebhooks30.webhookTasksTimeUp();
            for (RetryWebhookTask task : list) {
                // this is the last try, just post webhook url
                // no more further process, no matter success or not
                post(task.getMessage(), task.getWebhook());
            }
        }, 1, 1, TimeUnit.SECONDS);
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
                    instance = (IWebhookPoster) WebhookLoggingHandler.createProxy(poster);
                }
            }
        }
        return instance;
    }

    @Override
    public void postWebhook(final Message message, final IWebhook webhook) {
        // if event is not subscribed, not need post webhook
        if (!eventSubscried(message, webhook)) {
            return;
        }

        try {
            executor.submit(() -> {
                postWebhook1(message, webhook);
            });
        } catch (Exception ex) {
            // return the message back to queue, if exception happens when thread pool full
            // and queue full
            ex.printStackTrace();
            IEngineFactory.getFactoryInstance().getEngine().trigger(message);
        }
    }

    private boolean eventSubscried(Message message, IWebhook webhook) {
        boolean subscribed = false;
        int page = 0;

        List<IEvent> events = webhook.findSubscribedEvents(page, PAGE_SIZE);
        while (events != null && events.size() > 0) {
            for (IEvent event : events) {
                if (event.getEventType().equals(message.getEventType())) {
                    subscribed = true;
                    break;
                }
            }
            if (events.size() < PAGE_SIZE) {
                break;
            }
            page++;
            events = webhook.findSubscribedEvents(page, PAGE_SIZE);
        }
        return subscribed;
    }

    private void postWebhook1(Message message, IWebhook webhook) {
        CompletableFuture<HttpResponse<String>> f = post(message, webhook);
        f.thenAccept(response -> {
            // the response code should be 20x, indicates call webhook url successfully
            // otherwise, put the message into retry queue, which will retry 5 seconds later
            if (!(response.statusCode() >= 200 && response.statusCode() < 210)) {
                this.retryWebhooks5.add(message, webhook, System.currentTimeMillis() + 5 * 1000);
            }
        });
    }

    @Override
    public CompletableFuture<HttpResponse<String>> post(Message message, IWebhook webhook) {
        IHttpPoster poster = IHttpPoster
                .createInstance(com.plantssoil.common.httpclient.IHttpPoster.SecurityStrategy.valueOf(webhook.getSecurityStrategy().name()));
        poster.setAccessToken(webhook.getAccessToken());
        CompletableFuture<HttpResponse<String>> f = poster.post(webhook.getWebhookUrl(), webhook.getCustomizedHeaders(), message.getRequestId(),
                message.getPayload());
        return f;
    }
}
