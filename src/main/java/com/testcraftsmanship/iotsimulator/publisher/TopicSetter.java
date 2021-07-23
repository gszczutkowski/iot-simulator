package com.testcraftsmanship.iotsimulator.publisher;

import java.util.List;
import java.util.Map;

public interface TopicSetter {
    MessageSetter topic(String topic);

    IotPublisher topicsWithMessages(Map<String, List<String>> topicsWitMessages);
}
