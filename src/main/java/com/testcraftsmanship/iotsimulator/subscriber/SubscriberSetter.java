package com.testcraftsmanship.iotsimulator.subscriber;



public interface SubscriberSetter {
    IotSubscriber topicWithMessage(String topic, String message);

    IotSubscriber topic(String topic);
}
