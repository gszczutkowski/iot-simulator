package com.testcraftsmanship.iotsimulator.iottype.subscriber;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.testcraftsmanship.iotsimulator.item.IotDeviceSettings;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;
import static com.testcraftsmanship.iotsimulator.data.JsonMessageMatcher.jsonMatch;

@Slf4j
public class SubscribedTopic extends AWSIotTopic {
    private static final int FIRST_ITEM = 0;
    private final List<String> actualMessages = new ArrayList<>();
    private final SubscriptionData subscriptionData;
    private final IotDeviceSettings settings;

    public SubscribedTopic(SubscriptionData subscriptionData, IotDeviceSettings settings) {
        super(subscriptionData.getIotMessage().getTopic());
        this.subscriptionData = subscriptionData;
        this.settings = settings;
    }

    @Override
    public synchronized void onMessage(AWSIotMessage message) {
        if (isExpectedMaskSet() && isMessageMatchingTheMask(message.getStringPayload())) {
            log.info("Subscriber received message {} matching the mask", message.getStringPayload());
            actualMessages.add(message.getStringPayload());
        } else if (!isExpectedMaskSet()) {
            log.info("Subscriber received message {}, but no mask has been set", message.getStringPayload());
            actualMessages.add(message.getStringPayload());
        } else {
            log.debug("Message received by subscriber: {}", message.getStringPayload());
        }
    }

    public synchronized Optional<String> popPublishedMessage() {
        if (actualMessages.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(actualMessages.remove(FIRST_ITEM));
    }

    public synchronized List<String> allPublishedMessages() {
        return actualMessages;
    }

    private boolean isMessageMatchingTheMask(String message) {
        return jsonMatch(subscriptionData.getIotMessage().getMessage(), message);
    }

    private boolean isExpectedMaskSet() {
        return !isNullOrEmpty(subscriptionData.getIotMessage().getMessage());
    }
}
