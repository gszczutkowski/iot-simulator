package com.testcraftsmanship.iotsimulator.iottype.responder;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.testcraftsmanship.iotsimulator.item.IotDeviceSettings;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;
import static com.testcraftsmanship.iotsimulator.data.JsonMessageMatcher.jsonStructureMatch;

@Slf4j
public class IotResponderTopic extends AWSIotTopic {
    private final IotDeviceSettings settings;
    private final String expectedMessageMask;
    private String iotMessage;

    public IotResponderTopic(SubscriptionData subscriptionData, IotDeviceSettings settings) {
        super(subscriptionData.getIotMessage().getTopic());
        this.settings = settings;
        this.expectedMessageMask = subscriptionData.getIotMessage().getMessage();
    }

    @SneakyThrows
    @Override
    public synchronized void onMessage(AWSIotMessage message) {
        if (isExpectedMaskSet() && isMessageMatchingTheMask(message.getStringPayload())) {
            this.iotMessage = message.getStringPayload();
            log.info("Subscriber received message {} matching the mask", message.getStringPayload());
        } else if (!isExpectedMaskSet()) {
            log.warn("Subscriber received message {}, but no mask has been set", message.getStringPayload());
        }
    }

    public Optional<String> popMessage() {
        if (iotMessage == null) {
            return Optional.empty();
        } else {
            Optional<String> result = Optional.of(iotMessage);
            iotMessage = null;
            return result;
        }
    }

    private boolean isMessageMatchingTheMask(String message) {
        return jsonStructureMatch(message, expectedMessageMask, settings.isStrictMatching());
    }

    private boolean isExpectedMaskSet() {
        return !isNullOrEmpty(expectedMessageMask);
    }
}
