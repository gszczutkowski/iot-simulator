package com.testcraftsmanship.iotsimulator.iottype.subscriber;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.testcraftsmanship.iotsimulator.item.IotDeviceSettings;
import com.testcraftsmanship.iotsimulator.item.MatchingType;
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
    private final List<String> matchingMessages = new ArrayList<>();
    private final List<String> notMatchingMessages = new ArrayList<>();
    private final SubscriptionData subscriptionData;
    private final IotDeviceSettings settings;
    private int messagesMatchingMask = 0;
    private int messagesNotMatchingMask = 0;
    private int messagesWithNoMask = 0;

    public SubscribedTopic(SubscriptionData subscriptionData, IotDeviceSettings settings) {
        super(subscriptionData.getIotMessage().getTopic());
        this.subscriptionData = subscriptionData;
        this.settings = settings;
    }

    @Override
    public synchronized void onMessage(AWSIotMessage message) {
        if (isExpectedMaskSet() && isMessageMatchingTheMask(message.getStringPayload())) {
            log.info("Subscriber received message {} matching the mask", message.getStringPayload());
            matchingMessages.add(message.getStringPayload());
            messagesMatchingMask++;
        } else if (!isExpectedMaskSet()) {
            log.info("Subscriber received message {}, but no mask has been set", message.getStringPayload());
            matchingMessages.add(message.getStringPayload());
            messagesWithNoMask++;
        } else {
            log.debug("Message received by subscriber: {}", message.getStringPayload());
            notMatchingMessages.add(message.getStringPayload());
            messagesNotMatchingMask++;
        }
    }

    public synchronized boolean receivedExpectedMessagesMatchingMask() {
        if (MatchingType.MATCH_ALL.equals(settings.getMatchingType())) {
            return isAtLeasOneMessageMatchingMask() && noMessagesNotMatchingMask();
        } else if (MatchingType.MATCH_ANY.equals(settings.getMatchingType())) {
            return isAtLeasOneMessageMatchingMask();
        } else {
            throw new IllegalStateException("Support of matching type " + settings.getMatchingType() + " is not implemented");
        }
    }

    public synchronized List<String> allReceivedMessages() {
        List<String> allMessages = new ArrayList<>();
        allMessages.addAll(matchingMessages);
        allMessages.addAll(notMatchingMessages);
        return allMessages;
    }

    public synchronized List<String> matchingReceivedMessages() {
        return matchingMessages;
    }

    public synchronized Optional<String> popReceivedMessage() {
        if (matchingMessages.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(matchingMessages.remove(FIRST_ITEM));
    }

    private boolean isMessageMatchingTheMask(String message) {
        return jsonMatch(subscriptionData.getIotMessage().getMessage(), message);
    }

    private boolean isExpectedMaskSet() {
        return !isNullOrEmpty(subscriptionData.getIotMessage().getMessage());
    }

    private boolean isAtLeasOneMessageMatchingMask() {
        return messagesMatchingMask > 0 || messagesWithNoMask > 0;
    }

    private boolean noMessagesNotMatchingMask() {
        return messagesNotMatchingMask == 0;
    }
}
