package com.testcraftsmanship.iotsimulator.iottype.creator.responder;

import com.testcraftsmanship.iotsimulator.iottype.responder.IotResponder;

import java.util.List;

public interface ResMsgSetter {
    IotResponder publishingMessage(String message);

    IotResponder publishingMessages(List<String> messages);
}
