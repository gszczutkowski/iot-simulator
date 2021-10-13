package com.testcraftsmanship.iotsimulator.iottype.subscriber;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.iottype.generic.IotDevice;
import com.testcraftsmanship.iotsimulator.exception.IotConfigurationException;
import com.testcraftsmanship.iotsimulator.item.IotDeviceSettings;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.testcraftsmanship.iotsimulator.constant.GeneralConstants.SUBSCRIBER_POLLING_VALUE_IN_SEC;

public class IotSubscriber extends IotDevice<IotSubscriber> {
    private final SubscribedTopic subscribedTopic;

    public IotSubscriber(AWSIotMqttClient iotMqttClient, SubscriptionData subscriptionData, IotDeviceSettings settings) {
        super(iotMqttClient);
        this.subscribedTopic = new SubscribedTopic(subscriptionData, settings);
    }

    public String waitForMatchingMessage(int timeoutInSeconds) throws TimeoutException {
        long timeout = Instant.now().getEpochSecond() + timeoutInSeconds;
        while (Instant.now().getEpochSecond() < timeout) {
            Optional<String> message = subscribedTopic.popReceivedMessage();
            if (message.isPresent()) {
                return message.get();
            }
            try {
                TimeUnit.SECONDS.sleep(SUBSCRIBER_POLLING_VALUE_IN_SEC);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        throw new TimeoutException("Expected message didn't reach given topic or message does not match the mask");
    }

    public boolean doesExpectedMessagesReachedTheTopic() {
        return subscribedTopic.receivedExpectedMessagesMatchingMask();
    }

    public List<String> allMatchingMessages() {
        return subscribedTopic.matchingReceivedMessages();
    }

    public List<String> allMessages() {
        return subscribedTopic.allReceivedMessages();
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
