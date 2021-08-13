package com.testcraftsmanship.iotsimulator.iottype.responder;

public interface PreconditionSetter {
    PreconditionSetter subscribedTo(String topicWildcard);
    PreconditionSetter responseDelay(int delayInSec);
    PreconditionSetter strictMatchingDisabled();
    ListenerSetter when();
}
