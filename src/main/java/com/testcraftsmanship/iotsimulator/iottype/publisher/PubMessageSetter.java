package com.testcraftsmanship.iotsimulator.iottype.publisher;

import java.util.List;

public interface PubMessageSetter {
    IotPublisher message(String message);

    IotPublisher messages(List<String> messages);
}
