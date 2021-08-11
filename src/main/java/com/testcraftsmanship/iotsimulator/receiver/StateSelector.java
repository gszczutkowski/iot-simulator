package com.testcraftsmanship.iotsimulator.receiver;

public interface StateSelector {
    PreconditionSetter given();
    ListenerSetter when();
}
