package com.testcraftsmanship.iotsimulator.publisher;

import java.util.List;

public interface PubMessageSetter {
    IotPublisher message(String message);

    IotPublisher messages(List<String> messages);
}
