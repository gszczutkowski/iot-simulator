package com.testcraftsmanship.iotsimulator.iottype.responder;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.data.JsonParamsExtractor;
import com.testcraftsmanship.iotsimulator.data.JsonParamsUpdater;
import com.testcraftsmanship.iotsimulator.exception.IotConfigurationException;
import com.testcraftsmanship.iotsimulator.exception.MappingException;
import com.testcraftsmanship.iotsimulator.item.IotDeviceSettings;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;
import com.testcraftsmanship.iotsimulator.iottype.generic.IotDevice;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.testcraftsmanship.iotsimulator.constant.GeneralConstants.RESPONDER_POOLING_TIME_IN_MILLIS;

@Slf4j
public class IotResponder extends IotDevice<IotResponder> {
    private final IotResponderTopic iotResponderTopic;
    private final String expectedMessageMask;
    private final IotDeviceSettings settings;
    private final Map<String, List<String>> responseTopicsWithMessages;
    private boolean started = false;

    public IotResponder(AWSIotMqttClient iotMqttClient, SubscriptionData subscriptionData,
                        Map<String, List<String>> responseTopicsWithMessages, IotDeviceSettings settings) {
        super(iotMqttClient);
        this.iotResponderTopic = new IotResponderTopic(subscriptionData, settings);
        this.expectedMessageMask = subscriptionData.getIotMessage().getMessage();
        this.responseTopicsWithMessages = responseTopicsWithMessages;
        this.settings = settings;
    }

    @Override
    public IotResponder start() {
        super.start();
        try {
            started = true;
            getIotMqttClient().subscribe(iotResponderTopic, false);
            log.info("Responder started");
            ExecutorService service = Executors.newFixedThreadPool(1);
            service.execute(this::respondWhenReceive);
        } catch (AWSIotException e) {
            throw new IotConfigurationException("Unable to subscribe to given topic");
        }
        return getThis();
    }

    public IotResponder stop() {
        super.stop();
        started = false;
        return this;
    }

    @Override
    protected IotResponder getThis() {
        return this;
    }

    private void respondWhenReceive() {
        while (started) {
            Optional<String> message = iotResponderTopic.popMessage();
            if (message.isPresent()) {
                log.info("Sending response message");
                JsonParamsExtractor paramsExtractor;
                try {
                    paramsExtractor = new JsonParamsExtractor(message.get(), expectedMessageMask, settings.isStrictMatching());
                } catch (MappingException e) {
                    throw new RuntimeException("Error occurred while extracting parameters values from message", e);
                }
                publishMessagesToTopics(paramsExtractor.getParamsWithValues());
            }
            try {
                TimeUnit.MILLISECONDS.sleep(RESPONDER_POOLING_TIME_IN_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void publishMessagesToTopics(Map<String, String> paramsWithValues) {
        for (Map.Entry<String, List<String>> entry : responseTopicsWithMessages.entrySet()) {
            String topic = entry.getKey();
            List<String> messages = updateMessagesWithParams(entry.getValue(), paramsWithValues);
            publishMessagesToTopic(topic, messages);
        }
    }

    private void publishMessagesToTopic(final String topic, List<String> messages) {
        for (String message : messages) {
            try {
                getIotMqttClient().publish(topic, message);
                log.info("Published message {} to topic {}", message, topic);
            } catch (AWSIotException e) {
                log.error("Could not publish message {} to topic {}", message, topic);
            }
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
}
