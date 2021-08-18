package com.testcraftsmanship.iotsimulator.iottype.creator.publisher;

import com.testcraftsmanship.iotsimulator.iottype.publisher.IotPublisher;

import java.util.List;
import java.util.Map;

public interface PubTopicSetter {
    PubTopicSetter publishingDelay(int seconds);

    PubMessageSetter topic(String topic);

    IotPublisher topicsWithMessages(Map<String, List<String>> topicsWitMessages);
}
