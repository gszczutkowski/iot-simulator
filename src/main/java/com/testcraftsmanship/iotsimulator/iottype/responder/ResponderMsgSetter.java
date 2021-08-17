package com.testcraftsmanship.iotsimulator.iottype.responder;

import java.util.List;

public interface ResponderMsgSetter {
    IotResponder publishingMessage(String message);

    IotResponder publishingMessages(List<String> messages);
}
