package com.testcraftsmanship.iotsimulator.iottype.publisher;


import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.item.IotMessage;
import com.testcraftsmanship.iotsimulator.iottype.generic.IotDevice;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.amazonaws.services.iot.client.AWSIotConnectionStatus.CONNECTED;

@Slf4j
public class IotPublisher extends IotDevice<IotPublisher> {
    private final Map<String, List<String>> topicsWithMessages;
    private int delayInSeconds;

    public IotPublisher(AWSIotMqttClient iotMqttClient, Map<String, List<String>> topicsWithMessages, int delayInSeconds) {
        super(iotMqttClient);
        this.topicsWithMessages = topicsWithMessages;
        this.delayInSeconds = delayInSeconds;
    }

    @Override
    protected IotPublisher getThis() {
        return this;
    }

    public IotPublisher publish() {
        Optional<IotMessage> event = popMessageToPublish();
        if (event.isEmpty()) {
            log.info("There is no message to publish");
            return this;
        }
        startIotSimulatorWhenDisconnected();
        publishIotMessage(event.get());
        return this;
    }

    public IotPublisher publishAll() {
        startIotSimulatorWhenDisconnected();
        Optional<IotMessage> event = popMessageToPublish();
        while (event.isPresent()) {
            publishIotMessage(event.get());
            event = popMessageToPublish();
        }
        return this;
    }

    private Optional<IotMessage> popMessageToPublish() {
        if (topicsWithMessages == null || topicsWithMessages.isEmpty()) {
            return Optional.empty();
        }
        Map.Entry<String, List<String>> entry = topicsWithMessages.entrySet().iterator().next();
        String topic = entry.getKey();
        List<String> messages = entry.getValue();
        if (messages.size() == 1) {
            IotMessage result = new IotMessage(topic, messages.remove(0));
            topicsWithMessages.remove(topic);
            return Optional.of(result);
        } else if (messages.size() > 1) {
            IotMessage result = new IotMessage(topic, messages.remove(0));
            return Optional.of(result);
        } else {
            topicsWithMessages.remove(topic);
            return popMessageToPublish();
        }
    }

    private void publishIotMessage(@NonNull IotMessage message) {
        try {
            String payload = new JSONObject(message.getMessage()).toString();
            getIotMqttClient().publish(message.getTopic(), payload);
            log.info("Published message {} to topic {}", message.getMessage(), message.getTopic());
        } catch (AWSIotException e) {
            throw new IllegalStateException("Unable to publish message " + message.getMessage()
                    + " to topic " + message.getTopic());
        }
    }

    private void startIotSimulatorWhenDisconnected() {
        if (!CONNECTED.equals(getIotMqttClient().getConnectionStatus())) {
            start();
        }
    }
}






