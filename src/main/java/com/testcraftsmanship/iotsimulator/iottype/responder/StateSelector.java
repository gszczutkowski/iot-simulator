package com.testcraftsmanship.iotsimulator.iottype.responder;

public interface StateSelector {
    PreconditionSetter given();
    ListenerSetter when();
}
