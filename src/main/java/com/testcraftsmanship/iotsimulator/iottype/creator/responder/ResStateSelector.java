package com.testcraftsmanship.iotsimulator.iottype.creator.responder;

public interface ResStateSelector {
    ResPreconditionSetter given();
    ResSubscriberSetter when();
}
