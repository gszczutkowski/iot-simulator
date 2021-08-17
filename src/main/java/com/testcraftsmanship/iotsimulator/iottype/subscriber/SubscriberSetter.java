package com.testcraftsmanship.iotsimulator.iottype.subscriber;



public interface SubscriberSetter {
    IotSubscriber topicWithMessage(String topic, String message);
    IotSubscriber topic(String topic);
}
