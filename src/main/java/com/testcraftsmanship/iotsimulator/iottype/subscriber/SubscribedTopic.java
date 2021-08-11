package com.testcraftsmanship.iotsimulator.iottype.subscriber;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotTopic;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;
import static com.testcraftsmanship.iotsimulator.data.JsonMessageMatcher.jsonMatch;

@Slf4j
public class SubscribedTopic extends AWSIotTopic {
    private String expectedMessageMask;
    private final List<String> actualMessages = new ArrayList<>();

    public SubscribedTopic(String topic) {
        super(topic);
    }

    public SubscribedTopic(String topic, String messageMask) {
        super(topic);
        expectedMessageMask = messageMask;
    }

    @Override
    public synchronized void onMessage(AWSIotMessage message) {
        if (isExpectedMaskSet() && isMessageMatchingTheMask(message.getStringPayload())) {
            log.info("Subscriber received message {} matching the mask", message.getStringPayload());
            actualMessages.add(message.getStringPayload());
        } else if (!isExpectedMaskSet()) {
            log.info("Subscriber received message {}, but no mask has been set", message.getStringPayload());
            actualMessages.add(message.getStringPayload());
        }
    }

    public synchronized Optional<String> popPublishedMessage() {
        if (actualMessages.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(actualMessages.remove(0));
    }

    public synchronized List<String> allPublishedMessages() {
        return actualMessages;
    }

    private boolean isMessageMatchingTheMask(String message) {
        return jsonMatch(expectedMessageMask, message);
    }
    private boolean isExpectedMaskSet() {
        return !isNullOrEmpty(expectedMessageMask);
    }
}
