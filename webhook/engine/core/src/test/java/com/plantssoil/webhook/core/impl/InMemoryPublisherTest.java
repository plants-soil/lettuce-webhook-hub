package com.plantssoil.webhook.core.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class InMemoryPublisherTest {
    private static int corePoolSize = 500;
    private static int maximumPoolSize = 600;
    private static int workQueueCapacity = 1000;
    private static java.util.concurrent.Executor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(workQueueCapacity));
    private static long totalMemory;
    private static long maxMemory;
    private static long freeMemory;

    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        totalMemory = runtime.totalMemory();
        maxMemory = runtime.maxMemory();
        freeMemory = runtime.freeMemory();
        System.out.println(String.format("%d bytes total memory, %d bytes max memory, %d bytes free memory.", runtime.totalMemory(), runtime.maxMemory(),
                runtime.freeMemory()));
        InMemoryPublisherTest test = new InMemoryPublisherTest();
        test.testSubscribe();
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
    public void testSubscribe() {
        class TempSubscriber implements java.util.concurrent.Flow.Subscriber<Integer> {
            private Subscription subscription;
            private String subscriberName;

            TempSubscriber(String subscriberName) {
                this.subscriberName = subscriberName;
            }

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                System.out.println(this.subscriberName + " subscribed.");
                subscription.request(1);
                System.out.println(this.subscriberName + " request 1 item in onSubscribe()");
            }

            @Override
            public void onNext(Integer item) {
                System.out.println(String.format("%s received data %d in onNext()", this.subscriberName, item));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(String.format("Exception happened %s in %s.onError()", throwable.getMessage(), this.subscriberName));
            }

            @Override
            public void onComplete() {
                System.out.println(String.format("All data received successfully in %s.onComplete()", this.subscriberName));
                Runtime runtime = Runtime.getRuntime();
                System.out.println(String.format("%d bytes total memory (initial: %d), %d bytes max memory (initial: %d), %d bytes free memory (initial: %d).",
                        runtime.totalMemory(), totalMemory, runtime.maxMemory(), maxMemory, runtime.freeMemory(), freeMemory));
            }

        }

        try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>(executor, Flow.defaultBufferSize())) {
            TempSubscriber subscriber1 = new TempSubscriber("Subscriber111");
            TempSubscriber subscriber2 = new TempSubscriber("Subscriber222");
            publisher.subscribe(subscriber1);
            publisher.subscribe(subscriber2);

            for (int i = 0; i < 260; i++) {
                int submitted = publisher.submit(i);
                System.out.println(String.format("Produced data %d (returned: %d)", i, submitted));
            }
        }
    }

}
