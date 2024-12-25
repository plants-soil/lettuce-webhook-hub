package com.plantssoil.common.io;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ObjectByteArraySerializerTest {
    public static void main(String[] args) {
        ObjectByteArraySerializerTest test = new ObjectByteArraySerializerTest();
        test.testSerialize();
    }

    @Test
    public void testSerialize() {
        for (int i = 0; i < 10; i++) {
            TestEventMessage om = new TestEventMessage("eventType" + i, "requestId" + i, "payload" + i);
            byte[] bytes = ObjectByteArraySerializer.serialize(om);
            TestEventMessage un = ObjectByteArraySerializer.unserialize(bytes);
            assertEquals(om, un);
        }
    }
}
