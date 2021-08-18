package com.testcraftsmanship.iotsimulator.iottype.responder;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.testcraftsmanship.iotsimulator.data.JsonParamsExtractor;
import com.testcraftsmanship.iotsimulator.data.JsonParamsUpdater;
import com.testcraftsmanship.iotsimulator.exception.MappingException;
import com.testcraftsmanship.iotsimulator.item.IotDeviceSettings;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;
import static com.testcraftsmanship.iotsimulator.data.JsonMessageMatcher.jsonMatch;

@Slf4j
public class IotResponderTopic extends AWSIotTopic {
    private final AWSIotMqttClient responder;
    private final IotDeviceSettings settings;
    private final String expectedMessageMask;
    private final Map<String, List<String>> responseTopicsWithMessages;

    public IotResponderTopic(AWSIotMqttClient responder, SubscriptionData subscriptionData,
                             Map<String, List<String>> responseTopicsWithMessages, IotDeviceSettings settings) {
        super(subscriptionData.getIotMessage().getTopic());
        this.responder = responder;
        this.settings = settings;
        this.expectedMessageMask = subscriptionData.getIotMessage().getMessage();
        this.responseTopicsWithMessages = responseTopicsWithMessages;
    }

    @SneakyThrows
    @Override
    public synchronized void onMessage(AWSIotMessage message) {
        if (isExpectedMaskSet() && isMessageMatchingTheMask(message.getStringPayload())) {
            log.info("Subscriber received message {} matching the mask", message.getStringPayload());
            JsonParamsExtractor paramsExtractor = new JsonParamsExtractor(message.getStringPayload(),
                    expectedMessageMask, settings.isStrictMatching());
            publishMessagesToTopics(paramsExtractor.getParamsWithValues());
        } else if (!isExpectedMaskSet()) {
            log.info("Subscriber received message {}, but no mask has been set", message.getStringPayload());
        }
    }

    private void publishMessagesToTopics(Map<String, String> paramsWithValues) {
        for (Map.Entry<String, List<String>> entry : responseTopicsWithMessages.entrySet()) {
            String topic = entry.getKey();
            List<String> messages = updateMessagesWithParams(entry.getValue(), paramsWithValues);
            publishMessagesToTopic(topic, messages);
        }
    }

    private List<String> updateMessagesWithParams(List<String> messages, Map<String, String> paramsWithValues) {
        if (paramsWithValues == null || paramsWithValues.isEmpty()) {
            return messages;
        }
        return messages.stream().map(message -> {
                    try {
                        return new JsonParamsUpdater(message, paramsWithValues).updateJsonParamsWithValues().toString();
                    } catch (MappingException e) {
                        throw new IllegalArgumentException("Unable to update message " + message + " with given parameters");
                    }
                })
                .collect(Collectors.toList());
    }

    private void publishMessagesToTopic(final String topic, List<String> messages) {
        messages.forEach(message -> {
            try {
                responder.publish(topic, message);
            } catch (AWSIotException e) {
                log.error("Could not publish message {} to topic {}", message, topic);
            }
        });
    }

    private boolean isMessageMatchingTheMask(String message) {
        return jsonMatch(expectedMessageMask, message);
    }

    private boolean isExpectedMaskSet() {
        return !isNullOrEmpty(expectedMessageMask);
    }
}
