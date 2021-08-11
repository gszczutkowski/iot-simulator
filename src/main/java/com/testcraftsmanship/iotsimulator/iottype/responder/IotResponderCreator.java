package com.testcraftsmanship.iotsimulator.iottype.responder;

import com.amazonaws.services.iot.client.AWSIotMqttClient;

import java.util.List;
import java.util.Map;

public class IotResponderCreator implements StateSelector, PreconditionSetter, ListenerSetter, ResponderTopicSetter,
        ResponderMsgSetter {
    private final AWSIotMqttClient iotMqttClient;

    public IotResponderCreator(AWSIotMqttClient iotMqttClient) {
        this.iotMqttClient = iotMqttClient;
    }

    @Override
    public PreconditionSetter given() {
        return null;
    }

    @Override
    public IotResponderCreator subscribedTopic(String topicWildcard) {
        return null;
    }

    @Override
    public IotResponderCreator responseDelay(int responseDelayInSec) {
        return null;
    }

    @Override
    public ListenerSetter when() {
        return null;
    }

    @Override
    public IotResponderCreator topicIs(String topic) {
        return null;
    }

    @Override
    public IotResponderCreator messageIs(String message) {
        return null;
    }

    @Override
    public IotResponderCreator then() {
        return null;
    }

    @Override
    public IotResponder publishingMessage(String message) {
        return null;
    }

    @Override
    public IotResponder publishingMessages(List<String> messages) {
        return null;
    }

    @Override
    public ResponderMsgSetter publishingTopicIs(String topic) {
        return null;
    }

    @Override
    public IotResponder publishingTopicsWithMessages(Map<String, List<String>> topicsWitMessages) {
        return null;
    }
}
