package com.testcraftsmanship.iotsimulator.receiver;

public interface PreconditionSetter {
    PreconditionSetter subscribedTopic(String topicWildcard);

    PreconditionSetter responseDelay(int delayInSec);

    ListenerSetter when();
}
