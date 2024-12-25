package com.plantssoil.common.httpclient.impl;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.plantssoil.common.httpclient.IHttpCallback;
import com.plantssoil.common.httpclient.IHttpResponse;
import com.plantssoil.common.httpclient.exception.HttpClientException;

/**
 * The adaptor of JDK httpclient
 * 
 * @author danialdy
 * @Date 25 Dec 2024 4:28:19 pm
 */
class JdkHttpClientAdaptor implements AutoCloseable {
    private HttpClient.Builder builder = HttpClient.newBuilder().followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
            .proxy(java.net.ProxySelector.getDefault());
    private HttpClient httpClient;
    private volatile boolean closed = false;
    private int maxIdleConnections = 5;
    private Duration connectionTimeout = Duration.ofSeconds(10);
    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;
    private ExecutorService threadPool;
    private volatile AtomicInteger requestCount = new AtomicInteger(0);
    private Map<String, LinkedBlockingQueue<RequestWrapper>> requestQueuesByHost = new ConcurrentHashMap<>();
    private Map<String, Runnable> runnablesByHost = new ConcurrentHashMap<>();

    JdkHttpClientAdaptor() {
    }

    private class NamedThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final String namePrefix;
        private AtomicInteger threadSequence = new AtomicInteger(0);

        NamedThreadFactory(String factoryName) {
            SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = factoryName;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadSequence.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    private class RequestWrapper {
        private HttpRequest request;
        private IHttpCallback callback;

        RequestWrapper(HttpRequest request, IHttpCallback callback) {
            super();
            this.request = request;
            this.callback = callback;
        }

        HttpRequest getRequest() {
            return this.request;
        }

        IHttpCallback getCallback() {
            return this.callback;
        }
    }

    private class RequestRunner implements Runnable {
        private AtomicInteger requestCountCurrentHost = new AtomicInteger(0);
        private String host;

        RequestRunner(String host) {
            this.host = host;
        }

        private void callHttpClient(RequestWrapper request) {
            getHttpClient().sendAsync(request.getRequest(), java.net.http.HttpResponse.BodyHandlers.ofString()).thenAccept(r -> {
                SimpleHttpResponse response = new SimpleHttpResponse(r.statusCode(), r.body());
                try {
                    request.getCallback().onResponse(response);
                    requestCount.decrementAndGet();
                    this.requestCountCurrentHost.decrementAndGet();
                } catch (Exception e) {
                    throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14002, e);
                }
            }).exceptionally(ex -> {
                request.getCallback().onFailure(new Exception(ex));
                requestCount.decrementAndGet();
                this.requestCountCurrentHost.decrementAndGet();
                return null;
            });
        }

        @Override
        public void run() {
            while (true) {
                // read request from blocking queue
                RequestWrapper request = null;
                try {
                    request = requestQueuesByHost.get(this.host).poll(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    break;
                }
                if (request == null) {
                    break;
                }

                // wait if running requests exceed max requests
                while (requestCount.get() > maxRequests) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
                while (this.requestCountCurrentHost.get() > maxRequestsPerHost) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
                // if request is not null, will pass to http client
                requestCount.incrementAndGet();
                this.requestCountCurrentHost.incrementAndGet();
                callHttpClient(request);
            }
            // remove current thread from map if completed and no more requests within 30
            // seconds
            runnablesByHost.remove(host);
        }
    }

    private LinkedBlockingQueue<RequestWrapper> getRequestsQueueByHost(String host) {
        LinkedBlockingQueue<RequestWrapper> requestQueueByHost = this.requestQueuesByHost.get(host);
        if (requestQueueByHost == null) {
            synchronized (host.intern()) {
                requestQueueByHost = this.requestQueuesByHost.get(host);
                if (requestQueueByHost == null) {
                    requestQueueByHost = new LinkedBlockingQueue<>();
                    this.requestQueuesByHost.put(host, requestQueueByHost);
                }
            }
        }
        return requestQueueByHost;
    }

    private void startHttpClientRunnable(String host) {
        Runnable runnable = this.runnablesByHost.get(host);
        if (runnable == null) {
            synchronized (host.intern()) {
                runnable = this.runnablesByHost.get(host);
                if (runnable == null) {
                    runnable = new RequestRunner(host);
                    this.runnablesByHost.put(host, runnable);
                    Thread t = new Thread(runnable);
                    t.setName("JdkHttpClient-Dispatcher-" + host);
                    t.start();
                }
            }
        }
    }

    private HttpClient getHttpClient() {
        if (this.httpClient == null) {
            synchronized (JdkHttpClientAdaptor.class) {
                if (this.httpClient == null) {
                    // initialize thread pool
                    this.threadPool = Executors.newFixedThreadPool(this.maxIdleConnections, new NamedThreadFactory("JdkHttpClient-Runner-"));
                    // initialize the http client
                    this.httpClient = this.builder.connectTimeout(this.connectionTimeout).executor(this.threadPool).build();
                }
            }
        }
        return this.httpClient;
    }

    JdkHttpClientAdaptor connectTimeout(Duration timeoutDuration) {
        this.connectionTimeout = timeoutDuration;
        return this;
    }

    JdkHttpClientAdaptor maxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
        return this;
    }

    JdkHttpClientAdaptor maxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
        return this;
    }

    JdkHttpClientAdaptor maxRequestsPerHost(int maxRequestsPerHost) {
        this.maxRequestsPerHost = maxRequestsPerHost;
        return this;
    }

    void sendAsync(HttpRequest request, IHttpCallback callback) {
        if (this.closed) {
            throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14005, "JdkHttpClient service is closed, try again later!");
        }
        try {
            String host = request.uri().getHost();
            boolean added = getRequestsQueueByHost(host).offer(new RequestWrapper(request, callback), 10, TimeUnit.SECONDS);
            if (!added) {
                throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14004,
                        String.format("Failed to send the http request %s!", request.uri().getPath()));
            }
            startHttpClientRunnable(host);
        } catch (InterruptedException e) {
            throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14004, e);
        }
    }

    IHttpResponse send(HttpRequest request) {
        if (this.closed) {
            throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14005, "JdkHttpClient service is closed, try again later!");
        }
        // call remote url sync
        java.net.http.HttpResponse<String> response = null;
        try {
            response = getHttpClient().send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new HttpClientException(HttpClientException.BUSINESS_EXCEPTION_CODE_14004, e);
        }
        return new SimpleHttpResponse(response.statusCode(), response.body());
    }

    @Override
    public void close() throws Exception {
        this.closed = true;
        while (this.runnablesByHost.size() > 0) {
            Thread.sleep(100);
        }
        if (this.threadPool != null) {
            this.threadPool.shutdown();
        }
    }
}
