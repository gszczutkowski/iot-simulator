package com.testcraftsmanship.iotsimulator.iottype.creator.responder;

public interface ResPreconditionSetter {
    ResPreconditionSetter responseDelay(int delayInSec);
    ResPreconditionSetter subscribedTo(String topicWildcard);
    ResPreconditionSetter strictMatchingDisabled();
    ResSubscriberSetter when();
}
