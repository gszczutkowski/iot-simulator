package com.testcraftsmanship.iotsimulator.receiver;

public interface ListenerSetter {
    ListenerSetter topicIs(String topic);

    ListenerSetter messageIs(String message);

    ResponderTopicSetter then();
}
