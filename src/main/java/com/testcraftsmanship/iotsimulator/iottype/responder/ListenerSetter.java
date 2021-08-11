package com.testcraftsmanship.iotsimulator.iottype.responder;

public interface ListenerSetter {
    ListenerSetter topicIs(String topic);

    ListenerSetter messageIs(String message);

    ResponderTopicSetter then();
}
