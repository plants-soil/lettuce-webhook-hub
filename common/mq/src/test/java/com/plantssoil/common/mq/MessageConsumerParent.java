package com.plantssoil.common.mq;

import static org.junit.Assert.assertTrue;

public class MessageConsumerParent {
    public void consumeOrganization01() {
        for (int i = 0; i < 5; i++) {
            IMessageServiceFactory<TestEventMessage> f = IMessageServiceFactory.getFactoryInstance();
            IMessageConsumer<TestEventMessage> consumer = f.createMessageConsumer().consumerId("consumerId-" + i).channelName("PUBLISHER-ID-01-V1.0")
                    .addMessageListener(new MessageListener());
            consumer.consume(TestEventMessage.class);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // publish message
        IMessageServiceFactory<TestEventMessage> f = IMessageServiceFactory.getFactoryInstance();
        try (IMessagePublisher<TestEventMessage> publisher = f.createMessagePublisher().channelName("PUBLISHER-ID-01-V1.0")) {
            for (int i = 0; i < 20; i++) {
                TestEventMessage om = new TestEventMessage("order.created", String.valueOf(i),
                        "This is the " + i + " message comes from PUBLISHER-ID-01 (V1.0)");
                publisher.publish(om);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    public void consumeOrganization02() {
        // setup 2 consumers
        for (int i = 5; i < 8; i++) {
            IMessageServiceFactory<TestEventMessage> f = IMessageServiceFactory.getFactoryInstance();
            IMessageConsumer<TestEventMessage> consumer = f.createMessageConsumer().consumerId("consumerId-" + i).channelName("PUBLISHER-ID-02-V2.0")
                    .addMessageListener(new MessageListener());
            consumer.consume(TestEventMessage.class);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // publish message
        IMessageServiceFactory<TestEventMessage> f = IMessageServiceFactory.getFactoryInstance();
        try (IMessagePublisher<TestEventMessage> publisher = f.createMessagePublisher().channelName("PUBLISHER-ID-02-V2.0")) {
            for (int i = 0; i < 30; i++) {
                TestEventMessage om = new TestEventMessage("order.updated", String.valueOf(i),
                        "This is the " + i + " message comes from PUBLISHER-ID-02 (V2.0)");
                publisher.publish(om);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    public void consumeTopicMessages03() {
        // setup 2 consumers
        for (int i = 0; i < 2; i++) {
            IMessageServiceFactory<TestEventMessage> f = IMessageServiceFactory.getFactoryInstance();
            IMessageConsumer<TestEventMessage> consumer = f.createMessageConsumer().consumerId("topicConsumerId-" + i).channelName("TOPIC-ID-01-V1.0")
                    .channelType(ChannelType.TOPIC).addMessageListener(new MessageListener());
            consumer.consume(TestEventMessage.class);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // publish message
        IMessageServiceFactory<TestEventMessage> f = IMessageServiceFactory.getFactoryInstance();
        try (IMessagePublisher<TestEventMessage> publisher = f.createMessagePublisher().channelName("TOPIC-ID-01-V1.0").channelType(ChannelType.TOPIC)) {
            for (int i = 0; i < 10; i++) {
                TestEventMessage om = new TestEventMessage("order.updated", String.valueOf(i), "This is the " + i + " message comes from TOPIC-ID-01 (V1.0)");
                publisher.publish(om);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

}
