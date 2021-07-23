package com.testcraftsmanship.iotsimulator.publisher;

import java.util.List;

public interface MessageSetter {
    IotPublisher message(String message);

    IotPublisher messages(List<String> messages);
}
