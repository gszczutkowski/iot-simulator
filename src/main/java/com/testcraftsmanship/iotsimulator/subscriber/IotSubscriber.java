package com.testcraftsmanship.iotsimulator.subscriber;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.device.IotDevice;
import com.testcraftsmanship.iotsimulator.exception.IotConfigurationException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class IotSubscriber extends IotDevice<IotSubscriber> {
    private static final int POLLING_VALUE_IN_SEC = 1;
    private final SubscribedTopic subscribedTopic;

    public IotSubscriber(AWSIotMqttClient iotMqttClient, String topic) {
        super(iotMqttClient);
        subscribedTopic = new SubscribedTopic(topic);
    }

    public IotSubscriber(AWSIotMqttClient iotMqttClient, String topic, String messageMask) {
        super(iotMqttClient);
        subscribedTopic = new SubscribedTopic(topic, messageMask);
    }

    public String waitForMatchingMessage(int timeoutInSeconds) throws TimeoutException {
        long timeout = Instant.now().getEpochSecond() + timeoutInSeconds;
        while (Instant.now().getEpochSecond() < timeout) {
            Optional<String> message = subscribedTopic.popPublishedMessage();
            if (message.isPresent()) {
                return message.get();
            }
            try {
                TimeUnit.SECONDS.sleep(POLLING_VALUE_IN_SEC);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        throw new TimeoutException("Expected message didn't reach given topic or message does not match the mask");
    }

    public boolean doesMatchingMessageReachedTheTopic() {
        return subscribedTopic.popPublishedMessage().isPresent();
    }

    public List<String> allMatchingMessages() {
        return subscribedTopic.allPublishedMessages();
    }

    @Override
    public IotSubscriber start() {
        super.start();
        try {
            getIotMqttClient().subscribe(subscribedTopic, false);
        } catch (AWSIotException e) {
            throw new IotConfigurationException("Unable to subscribe to given topic");
        }
        return getThis();
    }

    @Override
    protected IotSubscriber getThis() {
        return this;
    }
}
