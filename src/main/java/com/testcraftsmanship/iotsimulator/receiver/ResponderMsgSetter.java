package com.testcraftsmanship.iotsimulator.receiver;

import java.util.List;

public interface ResponderMsgSetter {
    IotResponder publishingMessage(String message);

    IotResponder publishingMessages(List<String> messages);
}
