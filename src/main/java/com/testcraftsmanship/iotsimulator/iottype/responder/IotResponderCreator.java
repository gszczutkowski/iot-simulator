package com.testcraftsmanship.iotsimulator.iottype.responder;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.item.IotMessage;
import com.testcraftsmanship.iotsimulator.item.ResponderSettings;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class IotResponderCreator implements StateSelector, PreconditionSetter, ListenerSetter, ResponderTopicSetter,
        ResponderMsgSetter {
    private final ResponderSettings settings = new ResponderSettings();
    private final AWSIotMqttClient iotMqttClient;
    private SubscriptionData subscriptionData;
    private Map<String, List<String>> publishingTopicsWithMessages;

    public IotResponderCreator(AWSIotMqttClient iotMqttClient) {
        this.iotMqttClient = iotMqttClient;
    }

    @Override
    public PreconditionSetter given() {
        return this;
    }

    @Override
    public IotResponderCreator subscribedTo(String topicWildcard) {
        log.debug("Set subscriber to listen on topic {}", topicWildcard);
        this.subscriptionData = new SubscriptionData();
        subscriptionData.setTopicWildcard(topicWildcard);
        return this;
    }

    @Override
    public IotResponderCreator responseDelay(int responseDelayInSec) {
        log.debug("Set the time between publisher responses to {} second(s)", responseDelayInSec);
        this.settings.setTimeBetweenResponsesInSec(responseDelayInSec);
        return this;
    }

    @Override
    public IotResponderCreator strictMatchingDisabled() {
        log.debug("Subscriber strict matching mechanism has been disabled");
        this.settings.setStrictMatching(false);
        return this;
    }

    @Override
    public ListenerSetter when() {
        return this;
    }

    @Override
    public IotResponderCreator topicIs(String topic) {
        IotMessage iotMessage;
        if (subscriptionData == null) {
            subscriptionData = new SubscriptionData();
        }
        if (subscriptionData.getTopicWildcard() == null) {
            log.debug("Set subscriber to listen on topic {}", topic);
            subscriptionData.setTopicWildcard(topic);
        }
        if (subscriptionData.getIotMessage() == null) {
            iotMessage = new IotMessage();
        } else {
            iotMessage = subscriptionData.getIotMessage();
        }
        log.debug("Set subscriber to listen for message with topic {}", topic);
        iotMessage.setTopic(topic);
        subscriptionData.setIotMessage(iotMessage);
        return this;
    }

    @Override
    public IotResponderCreator messageIs(String message) {
        log.debug("Set subscriber to wait for message {}", message);
        IotMessage iotMessage;
        if (subscriptionData == null) {
            subscriptionData = new SubscriptionData();
        }
        if (subscriptionData.getIotMessage() == null) {
            iotMessage = new IotMessage();
        } else {
            iotMessage = subscriptionData.getIotMessage();
        }
        iotMessage.setMessage(message);
        subscriptionData.setIotMessage(iotMessage);
        return this;
    }

    @Override
    public IotResponderCreator then() {
        return this;
    }

    @Override
    public ResponderMsgSetter publishingTo(String topic) {
        publishingTopicsWithMessages.put(topic, new ArrayList<>());
        log.debug("Set publishing topic to {}", topic);
        return this;
    }

    @Override
    public IotResponder publishingMessage(String message) {
        checkOnlyOneTopicIsAdded();
        publishingTopicsWithMessages.entrySet().iterator().next().getValue().add(message);
        log.debug("Set publishing message to {}", message);
        return new IotResponder(iotMqttClient, subscriptionData, publishingTopicsWithMessages, settings);
    }

    @Override
    public IotResponder publishingMessages(List<String> messages) {
        checkOnlyOneTopicIsAdded();
        publishingTopicsWithMessages.entrySet().iterator().next().getValue().addAll(messages);
        log.debug("Set publishing messages to {}", messages);
        return new IotResponder(iotMqttClient, subscriptionData, publishingTopicsWithMessages, settings);
    }

    @Override
    public IotResponder publishingTopicsWithMessages(Map<String, List<String>> topicsWitMessages) {
        log.debug("Set publishing to multiple topics with multiple messages");
        publishingTopicsWithMessages = topicsWitMessages;
        return new IotResponder(iotMqttClient, subscriptionData, publishingTopicsWithMessages, settings);
    }

    private void checkOnlyOneTopicIsAdded() {
        if (publishingTopicsWithMessages == null || publishingTopicsWithMessages.size() != 1) {
            throw new IllegalStateException("There should be one topic passed to the simulator");
        }
    }
}
