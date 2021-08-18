package com.testcraftsmanship.iotsimulator.iottype.creator.subscriber;

public interface SubPreconditionSetter {
    SubPreconditionSetter subscribedTo(String topicWildcard);
    SubPreconditionSetter strictMatchingDisabled();
    SubSubscriberSetter when();
}
