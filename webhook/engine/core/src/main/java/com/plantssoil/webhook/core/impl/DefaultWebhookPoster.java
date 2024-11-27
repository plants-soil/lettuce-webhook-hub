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
import com.plantssoil.webhook.core.IWebhookEngineFactory;
import com.plantssoil.webhook.core.IWebhookEvent;
import com.plantssoil.webhook.core.IWebhookPoster;
import com.plantssoil.webhook.core.IWebhookSubscriber;
import com.plantssoil.webhook.core.exception.EngineException;
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
public class DefaultWebhookPoster implements IWebhookPoster {
    private final static int PAGE_SIZE = 50;
    private static volatile IWebhookPoster instance;
    private ThreadPoolExecutor executor;
    private ScheduledExecutorService retryScheduler5; // scheduler 5 seconds delay
    private RetryWebhookQueue retryWebhooks5; // retry webhook queue 5 seconds delay
    private ScheduledExecutorService retryScheduler30; // scheduler 30 seconds delay
    private RetryWebhookQueue retryWebhooks30; // retry webhook queue 30 seconds delay

    private DefaultWebhookPoster() {
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
                CompletableFuture<HttpResponse<String>> f = post(task.getMessage(), task.getSubscriber());
                f.thenAccept(response -> {
                    // the response code should be 20x, indicates call webhook url successfully
                    // otherwise, put the message into retry queue, which will retry 30 seconds
                    // later
                    if (!(response.statusCode() >= 200 && response.statusCode() < 210)) {
                        this.retryWebhooks30.add(task.getMessage(), task.getSubscriber(), System.currentTimeMillis() + 30 * 1000);
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
                post(task.getMessage(), task.getSubscriber());
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
            synchronized (DefaultWebhookPoster.class) {
                if (instance == null) {
                    // create poster instance (use proxy to AOP logging)
                    DefaultWebhookPoster poster = new DefaultWebhookPoster();
                    instance = (IWebhookPoster) WebhookLoggingHandler.createProxy(poster);
                }
            }
        }
        return instance;
    }

    @Override
    public void postWebhook(final DefaultWebhookMessage message, final IWebhookEvent event) {
        try {
            executor.submit(() -> {
                try {
                    int page = 0;
                    CompletableFuture<List<IWebhookSubscriber>> f = IWebhookEngineFactory.getFactoryInstance().getWebhookEngine().getRegistry()
                            .findSubscribers(event, page, PAGE_SIZE);
                    List<IWebhookSubscriber> subscribers = f.get();
                    while (subscribers.size() > 0) {
                        for (IWebhookSubscriber subscriber : subscribers) {
                            postWebhook(message, subscriber);
                        }
                        if (subscribers.size() < PAGE_SIZE) {
                            break;
                        }
                        page++;
                        f = IWebhookEngineFactory.getFactoryInstance().getWebhookEngine().getRegistry().findSubscribers(event, page, PAGE_SIZE);
                        subscribers = f.get();
                    }
                } catch (Exception e) {
                    throw new EngineException(EngineException.BUSINESS_EXCEPTION_CODE_20006, e);
                }
            });
        } catch (Exception ex) {
            // TODO exception happens when thread pool full and queue full
            // TODO should return the message back to queue?
            ex.printStackTrace();
        }
    }

    private void postWebhook(DefaultWebhookMessage message, IWebhookSubscriber subscriber) {
        CompletableFuture<HttpResponse<String>> f = post(message, subscriber);
        f.thenAccept(response -> {
            // the response code should be 20x, indicates call webhook url successfully
            // otherwise, put the message into retry queue, which will retry 5 seconds later
            if (!(response.statusCode() >= 200 && response.statusCode() < 210)) {
                this.retryWebhooks5.add(message, subscriber, System.currentTimeMillis() + 5 * 1000);
            }
        });
    }

    @Override
    public CompletableFuture<HttpResponse<String>> post(DefaultWebhookMessage message, IWebhookSubscriber subscriber) {
        IHttpPoster poster = IHttpPoster.createInstance(subscriber.getSecurityStrategy());
        poster.setSecretKey(subscriber.getSecretKey());
        CompletableFuture<HttpResponse<String>> f = poster.post(subscriber.getWebhookUrl(), subscriber.getCustomizedHeaders(), message.getRequestId(),
                message.getPayload());
        return f;
    }
}
