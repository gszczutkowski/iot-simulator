package com.testcraftsmanship.iotsimulator.iottype.creator.subscriber;

public interface SubStateSelector {
    SubPreconditionSetter given();
    SubSubscriberSetter when();
}
