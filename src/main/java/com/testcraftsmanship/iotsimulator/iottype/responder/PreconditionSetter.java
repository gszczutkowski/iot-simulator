package com.testcraftsmanship.iotsimulator.iottype.responder;

public interface PreconditionSetter {
    PreconditionSetter subscribedTopic(String topicWildcard);

    PreconditionSetter responseDelay(int delayInSec);

    ListenerSetter when();
}
