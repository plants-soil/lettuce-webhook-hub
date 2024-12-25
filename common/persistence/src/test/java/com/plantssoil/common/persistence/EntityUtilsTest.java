package com.plantssoil.common.persistence;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.plantssoil.common.persistence.beans.Course;
import com.plantssoil.common.persistence.beans.Student;
import com.plantssoil.common.persistence.beans.Teacher;

public class EntityUtilsTest {
    private Map<String, Set<String>> ids = new ConcurrentHashMap<>();

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
    public void testGetEntityIdField() {
        assertEquals("courseId", EntityUtils.getInstance().getEntityIdField(Course.class));
        assertEquals("studentId", EntityUtils.getInstance().getEntityIdField(Student.class));
        assertEquals("teacherId", EntityUtils.getInstance().getEntityIdField(Teacher.class));
    }

    Set<String> getGeneratedIds(String threadName) {
        Set<String> set = this.ids.get(threadName);
        if (set == null) {
            synchronized (threadName) {
                set = this.ids.get(threadName);
                if (set == null) {
                    set = new java.util.HashSet<String>();
                    this.ids.put(threadName, set);
                }
            }
        }
        return set;
    }

    class ObjectIdCreator implements Runnable {
        private String threadName;
        private CountDownLatch latch;
        private int count;

        ObjectIdCreator(String threadName, CountDownLatch latch, int count) {
            this.threadName = threadName;
            this.latch = latch;
            this.count = count;
        }

        @Override
        public void run() {
            try {
                Thread.currentThread().setName(this.threadName);
                for (int i = 0; i < this.count; i++) {
                    getGeneratedIds(this.threadName).add(EntityUtils.getInstance().createUniqueObjectId());
                }
            } finally {
                this.latch.countDown();
            }
        }
    }

    @Test
    public void testCreateUniqueObjectId() {
        int threads = 100000;
        int count = 1000;
        CountDownLatch latch = new CountDownLatch(threads);
        ExecutorService es = Executors.newFixedThreadPool(1000);
        for (int i = 0; i < threads; i++) {
            es.submit(new ObjectIdCreator("Object-ID-Creator-" + i, latch, count));
        }

        try {
            latch.await(); // Wait for all threads to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        int idCount = 0;
        for (Map.Entry<String, Set<String>> entry : this.ids.entrySet()) {
            idCount += entry.getValue().size();
        }
        System.out.println(String.format("All threads (%d) have completed, (%d) ids created.", this.ids.size(), idCount));
        assertEquals(threads * count, idCount);
    }

}
