package com.plantssoil.common.io;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ObjectJsonSerializerTest {
    public static void main(String[] args) {
        ObjectJsonSerializerTest test = new ObjectJsonSerializerTest();
        test.testSerialize();
    }

    @Test
    public void testSerialize() {
        for (int i = 0; i < 10; i++) {
            TestEventMessage om = new TestEventMessage("eventType" + i, "requestId" + i, "payload" + i);
            String string = ObjectJsonSerializer.getInstance().serialize(om);
            TestEventMessage un = ObjectJsonSerializer.getInstance().unserialize(string, TestEventMessage.class);
            assertEquals(om, un);
        }
    }
}
