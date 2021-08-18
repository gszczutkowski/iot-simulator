package com.testcraftsmanship.iotsimulator.iottype.creator.publisher;

import com.testcraftsmanship.iotsimulator.iottype.publisher.IotPublisher;

import java.util.List;

public interface PubMessageSetter {
    IotPublisher message(String message);

    IotPublisher messages(List<String> messages);
}
