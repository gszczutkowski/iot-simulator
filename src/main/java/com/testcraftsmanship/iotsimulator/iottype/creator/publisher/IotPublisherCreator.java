package com.testcraftsmanship.iotsimulator.iottype.creator.publisher;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.iottype.publisher.IotPublisher;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.testcraftsmanship.iotsimulator.constant.GeneralConstants.DEFAULT_TIME_BETWEEN_PUBLISHES;

@Slf4j
public class IotPublisherCreator implements PubStateSelector, PubTopicSetter, PubMessageSetter {
    private final AWSIotMqttClient iotMqttClient;
    private Map<String, List<String>> topicsWithMessages;
    private int publishingDelay = DEFAULT_TIME_BETWEEN_PUBLISHES;

    public IotPublisherCreator(AWSIotMqttClient iotMqttClient) {
        this.iotMqttClient = iotMqttClient;
        this.topicsWithMessages = new HashMap<>();
    }

    @Override
    public PubTopicSetter given() {
        return this;
    }

    @Override
    public PubTopicSetter publishingDelay(int seconds) {
        this.publishingDelay = seconds;
        return this;
    }

    @Override
    public PubMessageSetter topic(String topic) {
        this.topicsWithMessages.put(topic, new ArrayList<>());
        log.info("Set publishing topic to {}", topic);
        return this;
    }

    @Override
    public IotPublisher topicsWithMessages(Map<String, List<String>> messages) {
        this.topicsWithMessages = new HashMap<>();
        for (String key : messages.keySet()) {
            topicsWithMessages.put(key, new ArrayList<>(messages.get(key)));
        }
        log.info("Set publishing topics with messages to to {}", topicsWithMessages);
        return new IotPublisher(iotMqttClient, topicsWithMessages, publishingDelay);
    }

    @Override
    public IotPublisher message(String message) {
        checkOnlyOneTopicIsAdded();
        this.topicsWithMessages.entrySet().iterator().next().getValue().add(message);
        log.info("Set publishing message to {}", message);
        return new IotPublisher(iotMqttClient, topicsWithMessages, publishingDelay);
    }

    @Override
    public IotPublisher messages(List<String> messages) {
        checkOnlyOneTopicIsAdded();
        this.topicsWithMessages.entrySet().iterator().next().setValue(new ArrayList<>(messages));
        log.info("Set list of messages to publish to {}", messages);
        return new IotPublisher(iotMqttClient, topicsWithMessages, publishingDelay);
    }

    private void checkOnlyOneTopicIsAdded() {
        if (this.topicsWithMessages == null || this.topicsWithMessages.size() != 1) {
            throw new IllegalStateException("There should be one topic passed to the simulator");
        }
    }

}
