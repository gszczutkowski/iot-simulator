package com.testcraftsmanship.iotsimulator.iottype.publisher;

import java.util.List;
import java.util.Map;

public interface PubTopicSetter {
    PubMessageSetter topic(String topic);

    IotPublisher topicsWithMessages(Map<String, List<String>> topicsWitMessages);
}
