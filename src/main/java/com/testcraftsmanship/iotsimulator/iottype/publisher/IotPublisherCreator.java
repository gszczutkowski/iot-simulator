package com.testcraftsmanship.iotsimulator.iottype.publisher;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class IotPublisherCreator implements PubTopicSetter, PubMessageSetter {
    private final AWSIotMqttClient iotMqttClient;
    private Map<String, List<String>> topicsWithMessages;

    public IotPublisherCreator(AWSIotMqttClient iotMqttClient) {
        this.iotMqttClient = iotMqttClient;
        topicsWithMessages = new HashMap<>();
    }

    public PubMessageSetter topic(String topic) {
        topicsWithMessages.put(topic, new ArrayList<>());
        log.info("Set publishing topic to {}", topic);
        return this;
    }

    public IotPublisher message(String message) {
        checkOnlyOneTopicIsAdded();
        topicsWithMessages.entrySet().iterator().next().getValue().add(message);
        log.info("Set publishing message to {}", message);
        return new IotPublisher(iotMqttClient, topicsWithMessages);
    }

    public IotPublisher messages(List<String> messages) {
        checkOnlyOneTopicIsAdded();
        topicsWithMessages.entrySet().iterator().next().setValue(new ArrayList<>(messages));
        log.info("Set list of messages to publish to {}", messages);
        return new IotPublisher(iotMqttClient, topicsWithMessages);
    }

    public IotPublisher topicsWithMessages(Map<String, List<String>> messages) {
        topicsWithMessages = new HashMap<>();
        for (String key : messages.keySet()) {
            topicsWithMessages.put(key, new ArrayList<>(messages.get(key)));
        }
        log.info("Set publishing topics with messages to to {}", topicsWithMessages);
        return new IotPublisher(iotMqttClient, topicsWithMessages);
    }

    private void checkOnlyOneTopicIsAdded() {
        if (topicsWithMessages == null || topicsWithMessages.size() != 1) {
            throw new IllegalStateException("There should be one topic passed to the simulator");
        }
    }
}
